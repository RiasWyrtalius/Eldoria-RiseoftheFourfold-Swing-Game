package Core;

import Characters.Base.Hero;
import Characters.Party;
import Core.Battle.BattleController;
import Core.GameFlow.*;
import Core.Story.StorySlide;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Audio.AudioManager;
import UI.Components.IconLoader;
import UI.SceneManager;
import UI.Views.BattleView;
import UI.Views.CharacterSelection;
import UI.Views.MainMenu;
import UI.Views.StoryView;

import javax.swing.*;
import java.util.List;
import java.util.function.BiConsumer;

public class GameManager {
    private static final GameManager INSTANCE = new GameManager();

    private BattleController battleController;
    private final GameLoader gameLoader;
    private Party heroParty;

    private JFrame gameWindow;
    private MainMenu mainMenuView;
    private BattleView battleView;

    private GameManager() {
        this.gameLoader = new GameLoader();
    }

    public static GameManager getInstance() {
        return INSTANCE;
    }


    /**
     * Called after a party has been created (either new or loaded).
     * This method generates the campaign and transitions the UI to the first battle.
     */
    public void startGameLoop() {
        SceneManager.getInstance().transitionTo(battleView);

        // link Infrastructure
        LogManager.initialize(battleView.getGameLogPanelTextPane(), battleView.getGameLogHighlightPanelTextPane());
        VisualEffectsManager.getInstance().setMainView(battleView);


        // generate campaign
        long seed = System.currentTimeMillis();
        gameLoader.generateCampaign(seed, 20);

        // start level 1
        loadNextLevel();
    }

    public void startApplication() {
        // run all UI initialization safely on the swing thread
        // cuz swing     runs on a separate thread for some reason lol
        SwingUtilities.invokeLater(() -> {
            gameWindow = new JFrame("Elordia: Rise of the Fourfold");
            IconLoader.setIcons(gameWindow);
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);

            SceneManager.getInstance().initialize(gameWindow);

            // preload all views!!!
            mainMenuView = new MainMenu(this);
            battleView = new BattleView();
            LogManager.initialize(battleView.getGameLogPanelTextPane(), battleView.getGameLogHighlightPanelTextPane());

            transitionToMainMenu();

            gameWindow.setVisible(true);
        });
    }

    public void transitionToMainMenu() {
        LogManager.log("Transitioning to main menu.");
        AudioManager.getInstance().stopMusic();
        AudioManager.getInstance().playMusic("MAIN-THEME");
        SceneManager.getInstance().transitionTo(mainMenuView);
    }

    public void transitionToBattleView() {
        SceneManager.getInstance().transitionTo(battleView);
        VisualEffectsManager.getInstance().setMainView(battleView);
    }

    public void loadSavedGame() {
        GameState state = SaveManager.loadGame();

        if (state == null) {
            LogManager.log("Failed to load save file.", LogFormat.SYSTEM_ERROR);
            JOptionPane.showMessageDialog(
                    battleView,
                    "Could not load the save file. It may be corrupted.",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );

            transitionToMainMenu();
            return;
        }

        LogManager.log("Save file loaded...", LogFormat.SYSTEM);

        SceneManager.getInstance().transitionTo(battleView);

        LogManager.initialize(battleView.getGameLogPanelTextPane(), battleView.getGameLogHighlightPanelTextPane());
        VisualEffectsManager.getInstance().setMainView(battleView);

        // load game state
        this.heroParty = gameLoader.loadPartyFromSave(state);
        gameLoader.generateCampaign(state.seed, 20);

        for (int i = 0; i < state.levelsCompleted; i++) {
            gameLoader.loadNextLevel();
        }

        // Start from save point
        loadNextLevel();
    }

    public void startNewGame() {
        List<StorySlide> introScript = GameLoader.loadIntroSequence();
        StoryView storyView = new StoryView();

        SceneManager.getInstance().transitionTo(storyView);
        AudioManager.getInstance().playMusic("STORYVIEW");

        storyView.startSequence(introScript, this::initializeGameplay);
    }

    private void initializeGameplay() {
        // Create an empty party to be filled by the player
        this.heroParty = GameLoader.createInitialParty("Adventurers");

        BiConsumer<Hero, String> onCharacterPicked = (selectedHero, partyName) -> {
            this.heroParty.addPartyMember(selectedHero);
            if (partyName != null && !partyName.isEmpty()) {
                this.heroParty.setPartyName(partyName);
            }
            SceneManager.getInstance().goBack(); // close selection overlay
            startGameLoop();
        };

        showCharacterSelectionScreen(CharacterSelectionMode.CREATE_NEW_PARTY, onCharacterPicked);
    }

    /**
     * public method to play cutscenes
     * @param slides
     * @param onFinished
     */
    public void playStorySequence(List<StorySlide> slides, Runnable onFinished) {
        if (slides == null || slides.isEmpty()) {
            if (onFinished != null) onFinished.run();
            return;
        }

        StoryView storyView = new StoryView();
        SceneManager.getInstance().transitionTo(storyView);
        storyView.startSequence(slides, onFinished);
    }

    public void loadNextLevel() {
        Level nextLevel = gameLoader.loadNextLevel();

        if (nextLevel == null) {
            gameLoader.finishCampaign();
            AudioManager.getInstance().stopMusic();
            AudioManager.getInstance().playMusic("VICTORY_MUSIC_1");

            if (battleView != null) {
                battleView.showCampaignVictoryScreen();
            }
            return;
        }

        LogManager.log("Entering Level " + nextLevel.levelNumber(), LogFormat.SYSTEM);

        if (nextLevel.levelNumber() % 5 == 0) {
            LogManager.log("Milestone Floor Reached! The party's determination restores them.", LogFormat.HIGHLIGHT_BUFF);

            for (Characters.Character member : heroParty.getPartyMembers()) {
                if (member instanceof Hero) {
                    ((Hero) member).reviveFromMapMilestone();
                }
            }
        }

        List<StorySlide> preLevelCutscene = nextLevel.preLevelCutscene();
        if (preLevelCutscene != null && !preLevelCutscene.isEmpty()) {
            playStorySequence(preLevelCutscene, () -> {
                setupBattle(nextLevel);
                transitionToBattleView();
            });
        } else {
            setupBattle(nextLevel);
        }

        saveCurrentGame();
    }

    private void setupBattle(Level level) {
        battleView.setBattleBackground(level.battleBackground());

        Party enemyParty = level.buildEnemyParty();
        this.battleController = new BattleController(heroParty, enemyParty, level);

        battleController.setMainView(battleView);
        battleView.linkControllerAndData(battleController);
    }

    public void saveCurrentGame() {
        if (battleController == null) {
            LogManager.log("Cannot save: No active battle.", LogFormat.SYSTEM);
            return;
        }

        int levelNum = battleController.getLevelNumber();
        // Number of levels COMPLETED is the current level number - 1
        int levelsDone = Math.max(0, levelNum - 1);

        // Create the DTO snapshot
        GameState currentState = new GameState(
                gameLoader.getCurrentSeed(),
                levelsDone,
                heroParty
        );

        SaveManager.saveGame(currentState);
    }

    public void showCharacterSelectionScreen(CharacterSelectionMode mode, BiConsumer<Hero, String> onCharacterPicked) {
        CharacterSelection selectionView = new CharacterSelection(mode, onCharacterPicked);
        SceneManager.getInstance().showOverlay(selectionView);
    }

    public void createPartyFromSelection(Hero selectedHero, String partyName) {
        this.heroParty = GameLoader.createInitialParty(partyName);
        this.heroParty.addPartyMember(selectedHero);
        LogManager.log(selectedHero.getName() + " begins their adventure!", LogFormat.PLAYER_JOIN);
    }


    public Party getHeroParty() {
        return heroParty;
    }

}
