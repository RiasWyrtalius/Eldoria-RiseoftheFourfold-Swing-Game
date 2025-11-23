package Core;

import Abilities.Jobs.FireMage;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Enemies.Goblin;
import Characters.Party;
import UI.MainInterface;
import UI.MainMenu;

import javax.swing.*;

public class GameManager {
    private BattleController battleController;
    private Party heroParty;
    private Party enemyParty;
    private MainInterface mainView;

    public void startApplication() {
        // run all UI initialization safely on the swing thread
        // cuz swing runs on a separate thread for some reason lol
        SwingUtilities.invokeLater(this::initializeApplication);
    }

    private void initializeApplication() {
        new MainMenu(this);
    }

    /**
     * All da setup logic teehee
     */
    private void createGameModel() {
        //TEMPORARY HERO SETUP
        heroParty = new Party("The Godslayers");
        FireMage fireMage = new FireMage();
        Character kai = new Hero("Kai", 100, 50, 100, 1, fireMage, "Assets/Images/bstudios.png") {
            @Override
            protected void onDeath() {
                
            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        heroParty.addPartyMember(kai);

        //TEMPORARY ENEMY SETUP
        enemyParty = new Party("Swarm of Goblins");
        Goblin goblin_1 = new Goblin();
        Goblin goblin_2 = new Goblin();
        enemyParty.addPartyMember(goblin_1);
        enemyParty.addPartyMember(goblin_2);
    }

    public void startNewGame() {
        // dummy view to get the text area for synchronization reasons
        mainView = new MainInterface();
        LogManager.initialize(mainView.getGameLogPanelTextArea());
        LogManager.log("Logger initialized with UI component");

        createGameModel();
        battleController = new BattleController(heroParty, enemyParty);

        mainView.linkControllerAndData(battleController);
        LogManager.log("New game started. Battle Initialized");
    }
}
