package Core;

import Characters.Base.Hero;
import Characters.Party;
import Core.Battle.BattleController;
import Core.GameFlow.GameLoader;
import Core.GameFlow.Level;
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
import java.util.function.Consumer;

public class GameManager {
    private static final GameManager INSTANCE = new GameManager();

    private BattleController battleController;
    private GameLoader gameLoader;
    private Party heroParty;
    private BattleInterface mainView;

    private JFrame activeWindow;

    private GameManager() {
        this.gameLoader = new GameLoader();
    }

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public void startApplication() {
        // run all UI initialization safely on the swing thread
        // cuz swing runs on a separate thread for some reason lol
        SwingUtilities.invokeLater(this::initializeApplication);
    }

    private void initializeApplication() {
        new MainMenu(this);
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
    }

    public void startNewGame(JFrame currentWindow) {
        this.activeWindow = currentWindow;

        List<StorySlide> introScript = GameLoader.loadIntroSequence();

        StoryView storyView = new StoryView();

        activeWindow.setContentPane(storyView);
        activeWindow.revalidate();
        activeWindow.repaint();

        storyView.startSequence(introScript, this::initializeGameplay);

//        // dummy view to get the text area for synchronization reasons
//        mainView = new BattleInterface();
//
//        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
//        LogManager.log("Logger initialized with UI component", LogFormat.SYSTEM);
//        VisualEffectsManager.getInstance().setMainView(mainView);
//
//        this.heroParty = GameLoader.loadStarterParty();
//        long seed = System.currentTimeMillis();
//        // DONT DELETE! DEBUG to check if seed generation works properly
//        // 2 goblins -> 2 slimes -> 1 goblin 1 spider -> etc. etc.
//        // seed = 1764436935686L;
//        gameLoader.generateCampaign(seed, 20); // Generate n levels
//
//        loadNextLevel();
    }

    /**
     * The actual game initialization.
     * Runs after the intro cutscene.
     */
    private void initializeGameplay() {
        mainView = new BattleInterface();

        activeWindow.setContentPane(mainView);
        activeWindow.revalidate(); // Tell layout manager to recalculate
        activeWindow.repaint();    // Draw the new pixels

        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
        VisualEffectsManager.getInstance().setMainView(mainView);

        long seed = System.currentTimeMillis();
        gameLoader.generateCampaign(seed, 20);

        loadNextLevel();
    }

    public void showCharacterSelectionScreen() {
        BiConsumer<Hero, String> onCharacterPicked = (selectedHero, partyName) -> {
            System.out.println("Character Selected. Resuming flow...");

            this.heroParty = GameLoader.loadStarterParty(partyName);

            heroParty.addPartyMember(selectedHero);

            if (partyName != null && !partyName.isEmpty()) {
                heroParty.setPartyName(partyName);
                LogManager.log("Party renamed to: " + partyName, LogFormat.SYSTEM);
            }

            LogManager.log(selectedHero.getName() + " joined the adventure!", LogFormat.PLAYER_JOIN);

            closeOverlay(activeWindow);

            loadNextLevel();
        };

        CharacterSelection selectionView = new CharacterSelection(onCharacterPicked);

        JLayeredPane layeredPane = activeWindow.getLayeredPane();

        selectionView.setBounds(0, 0, activeWindow.getWidth(), activeWindow.getHeight());

        // pop up layers are so cool
        // make other UI components popup layers as well
        layeredPane.add(selectionView, JLayeredPane.POPUP_LAYER);

        activeWindow.revalidate();
        activeWindow.repaint();
    }

    private void closeOverlay(JFrame frame) {
        JLayeredPane layeredPane = frame.getLayeredPane();

        Component[] comps = layeredPane.getComponentsInLayer(JLayeredPane.POPUP_LAYER);
        for (Component c : comps) {
            if (c instanceof CharacterSelection) {
                layeredPane.remove(c);
            }
        }

        frame.revalidate();
        frame.repaint();
    }
}
