package Core;

import Abilities.Jobs.*;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Enemies.Boss;
import Characters.Enemies.Goblin;
import Characters.Enemies.Slime;
import Characters.Party;
import Core.Battle.BattleController;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AssetManager;
import UI.Views.MainInterface;
import UI.Views.MainMenu;

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
     * All da setup logic teehee TODO: move to game loader
     */
    private void createGameModel() {
        mainView.setBattleBackground("Assets/Images/Backgrounds/sample.jpg");

        //TEMPORARY HERO SETUP
        heroParty = new Party("The Godslayers");
        Warrior warrior = new Warrior();
        Character charlie = new Hero("Charlie",150,50,100,1,warrior,"WARRIOR_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        FireMage fireMage = new FireMage();
        Character chaniy = new Hero("Chaniy the doubter",100,60,120,1,fireMage,"MAGE_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        CryoMancer iceMage = new CryoMancer();
        Character sammy = new Hero("Sammy", 100, 60, 120, 1, iceMage, "MAGE_ICE-IDLE") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        EarthMage earthMage = new EarthMage();
        Character ythan = new Hero("Ythanny W", 100, 60, 120, 1, earthMage, "MAGE_EARTH-IDLE") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        Cleric cleric = new Cleric();
        Character erick = new Hero("Erick the cleric", 100, 60, 120, 1, cleric, "CLERIC_IDLE") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        Archer archer = new Archer();
        Character gianmeni = new Hero("Gian Meni",80,70,100,1,archer,"ARCHER_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

        AeroMancer aeoroMancer = new AeroMancer();
        Character kervs = new Hero("Kurtis", 100, 60, 120, 1, aeoroMancer, "MAGE_WIND-IDLE");

//        TODO: add max amount of party members
//        heroParty.addPartyMember(charlie);
//        heroParty.addPartyMember(ythan);
        heroParty.addPartyMember(erick);
        heroParty.addPartyMember(sammy);
//        heroParty.addPartyMember(gianmeni);
        heroParty.addPartyMember(kervs);
        heroParty.addPartyMember(chaniy);

        //TEMPORARY ENEMY SETUP
        enemyParty = new Party("Swarm of Goblins");
        Goblin goblin_1 = new Goblin();
        Boss boss = new Boss("Serato The Destroyer" , 20,10,200,1,"Boss",200,3);
        Slime slime_1 = new Slime();
        enemyParty.addPartyMember(goblin_1);
        enemyParty.addPartyMember(boss);
        enemyParty.addPartyMember(slime_1);
    }

    public void startNewGame() {
        // dummy view to get the text area for synchronization reasons
        mainView = new MainInterface();
        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
        LogManager.log("Logger initialized with UI component", LogColor.SYSTEM);

        createGameModel();
        battleController = new BattleController(heroParty, enemyParty);
        battleController.setMainView(mainView);
        VisualEffectsManager.getInstance().setMainView(mainView);

        mainView.linkControllerAndData(battleController);
    }
}
