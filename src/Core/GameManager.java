package Core;

import Abilities.Jobs.*;
import Characters.Party;
import Core.Battle.BattleController;
import Core.GameFlow.GameLoader;
import Core.GameFlow.Level;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Items.*;
import UI.Views.BattleInterface;
import UI.Views.MainMenu;

import javax.swing.*;

public class GameManager {
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
        // cuz swing runs on a separate thread for some reason lol
        SwingUtilities.invokeLater(this::initializeApplication);
    }

    private void initializeApplication() {
        new MainMenu(this);
    }

    public void loadNextLevel() {
        Level nextLevel = gameLoader.loadNextLevel();

        if (nextLevel == null) {
            gameLoader.finishCampaign();
            return;
        }

        LogManager.log("");
        LogManager.log("Entering Level " + nextLevel.levelNumber(), LogColor.SYSTEM);

        mainView.setBattleBackground(nextLevel.battleBackground());

        Party enemyParty = nextLevel.createEnemyParty();
        this.battleController = new BattleController(heroParty, enemyParty, nextLevel);

        battleController.setMainView(mainView);
        mainView.linkControllerAndData(battleController);
    }

    public void startNewGame() {
        // dummy view to get the text area for synchronization reasons
        mainView = new BattleInterface();

        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
        LogManager.log("Logger initialized with UI component", LogColor.SYSTEM);
        VisualEffectsManager.getInstance().setMainView(mainView);

        this.heroParty = GameLoader.loadStarterParty();
        long seed = System.currentTimeMillis();
        // DONT DELETE! DEBUG to check if seed generation works properly
        // 2 goblins -> 2 slimes -> 1 goblin 1 spider -> etc. etc.
        // seed = 1764436935686L;
        gameLoader.generateCampaign(seed, 5); // Generate 5 levels

        loadNextLevel();
    }
}
