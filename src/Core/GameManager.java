package Core;

import Characters.Base.Hero;
import Characters.Party;
import Core.Battle.BattleController;
import Core.GameFlow.*;
import Core.Story.StorySlide;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import UI.Views.BattleInterface;
import UI.Views.CharacterSelection;
import UI.Views.MainMenu;
import UI.Views.StoryView;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class GameManager {
    private JFrame gameWindow;
    private MainMenu mainMenuView;

    private static final GameManager INSTANCE = new GameManager();

    private BattleController battleController;
    private GameLoader gameLoader;
    private Party heroParty;
    private BattleInterface mainView;

    private GameManager() {
        this.gameLoader = new GameLoader();
    }

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public void startApplication() {
        // run all UI initialization safely on the swing thread
        // cuz swing     runs on a separate thread for some reason lol
        SwingUtilities.invokeLater(() -> {
            this.gameWindow = new JFrame("Elordia: Rise of the Fourfold");
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);

            showMainMenu();
        });
    }

    public void showMainMenu() {
        if (mainMenuView == null) {
            mainMenuView = new MainMenu(this);
        }

        // Swap the content to the menu panel
        gameWindow.setContentPane(mainMenuView);
        gameWindow.revalidate();
        gameWindow.repaint();
        gameWindow.setVisible(true);
    }

    public void loadSavedGame() {
        GameState state = SaveManager.loadGame();

        if (state == null) {
            LogManager.log("Failed to load save file.", LogFormat.SYSTEM_ERROR);

            JOptionPane.showMessageDialog(
                    gameWindow,
                    "Could not load the save file. It may be corrupted.",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE
            );

            showMainMenu();
        }

        LogManager.log("Save file loaded...", LogFormat.SYSTEM);

        // setup battle view
        mainView = new BattleInterface();
        gameWindow.setContentPane(mainView);
        gameWindow.revalidate();
        gameWindow.repaint();

        // re link infrastructure
        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
        VisualEffectsManager.getInstance().setMainView(mainView);

        // reload party
        this.heroParty = gameLoader.loadPartyFromSave(state);
        gameLoader.generateCampaign(state.seed, 20); // Regenerate same campaign

        // skip done levels
        for (int i = 0; i < state.levelsCompleted; i++) {
            gameLoader.loadNextLevel();
        }

        // start game from savepoint
        loadNextLevel();
    }

    public void startNewGame(JFrame currentWindow) {
        this.gameWindow = currentWindow;

        List<StorySlide> introScript = GameLoader.loadIntroSequence();

        StoryView storyView = new StoryView();

        gameWindow.setContentPane(storyView);
        gameWindow.revalidate();
        gameWindow.repaint();

        storyView.startSequence(introScript, null);
    }

    public void loadNextLevel() {
        Level nextLevel = gameLoader.loadNextLevel();

        if (nextLevel == null && battleController == null) {
            return;
        }

        if (nextLevel == null) {
            gameLoader.finishCampaign();
            return;
        }

        LogManager.log("Entering Level " + nextLevel.levelNumber(), LogFormat.SYSTEM);

        mainView.setBattleBackground(nextLevel.battleBackground());

        Party enemyParty = nextLevel.buildEnemyParty();
        this.battleController = new BattleController(heroParty, enemyParty, nextLevel);

        battleController.setMainView(mainView);
        mainView.linkControllerAndData(battleController);

        LogManager.log("Creating save point...");
        saveCurrentGame();

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

    // TODO: character summoning
//    public void onCharacterSummon() {
//
//    }

    /**
     * Called after a party has been created (either new or loaded).
     * This method generates the campaign and transitions the UI to the first battle.
     */
    public void startGameLoop() {
        mainView = new BattleInterface();
        gameWindow.setContentPane(mainView);
        gameWindow.revalidate();
        gameWindow.repaint();

        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
        VisualEffectsManager.getInstance().setMainView(mainView);

        long seed = System.currentTimeMillis();
        gameLoader.generateCampaign(seed, 20);

        loadNextLevel();
    }

    public void showCharacterSelectionScreen(CharacterSelectionMode mode, BiConsumer<Hero, String> onCharacterPicked) {
        CharacterSelection selectionView = new CharacterSelection(mode, onCharacterPicked);

        JLayeredPane layeredPane = gameWindow.getLayeredPane();
        selectionView.setBounds(0, 0, gameWindow.getWidth(), gameWindow.getHeight());
        layeredPane.add(selectionView, JLayeredPane.POPUP_LAYER);

        gameWindow.revalidate();
        gameWindow.repaint();
    }

    public void createPartyFromSelection(Hero selectedHero, String partyName) {
        this.heroParty = GameLoader.createInitialParty(partyName);
        this.heroParty.addPartyMember(selectedHero);
        LogManager.log(selectedHero.getName() + " begins their adventure!", LogFormat.PLAYER_JOIN);
    }

    public void closeOverlay() { // <-- Removed parameter
        if (gameWindow == null) return;

        JLayeredPane layeredPane = gameWindow.getLayeredPane();
        Component[] comps = layeredPane.getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component c : comps) {
            if (c instanceof CharacterSelection) {
                layeredPane.remove(c);
            }
        }
        gameWindow.revalidate();
        gameWindow.repaint();
    }

}
