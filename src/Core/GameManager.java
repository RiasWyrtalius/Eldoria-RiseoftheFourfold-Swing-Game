package Core;

import Abilities.Jobs.FireMage;
import Abilities.Jobs.Warrior;
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
        Warrior warrior = new Warrior();
        Character charlie = new Hero("Charlie",150,50,100,1,warrior,"Assets/Images/bstudios.png"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        FireMage fireMage = new FireMage();
        Character sam = new Hero("Sammie Wammy", 100, 60, 120, 1, fireMage, "Assets/Images/bstudios.png") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        heroParty.addPartyMember(charlie);
        heroParty.addPartyMember(sam);

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
        battleController.setMainView(mainView);

        mainView.linkControllerAndData(battleController);
    }
}
