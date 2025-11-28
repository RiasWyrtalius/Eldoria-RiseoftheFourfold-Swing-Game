package Core;

import Abilities.Jobs.*;
import Characters.Enemies.Vampire;
import Core.Battle.TargetType;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Enemies.Boss;
import Characters.Enemies.Goblin;
import Characters.Enemies.Slime;
import Characters.Party;
import Core.Battle.BattleController;
import Core.GameFlow.GameLoader;
import Core.GameFlow.Level;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Items.Item;
import Items.Rarity;
import Items.ResourceItem;
import UI.Views.BattleInterface;
import UI.Views.MainMenu;

import javax.swing.*;

public class GameManager {
    private BattleController battleController;
    private GameLoader gameLoader;
    private Party heroParty;
    private Party enemyParty;
    private BattleInterface mainView;

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

//      TODO: battle controller keep main
        Party enemyParty = nextLevel.createEnemyParty();
        this.battleController = new BattleController(heroParty, enemyParty);

        battleController.setMainView(mainView);
        mainView.linkControllerAndData(battleController);
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
        //TODO: ADD image key for rouge
        Rogue rogue = new Rogue();
        Character elyi = new Hero("Elyi",80,50,100,1,rogue,"WARRIOR_IDLE"){
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
//        heroParty.addPartyMember(sammy);
        heroParty.addPartyMember(gianmeni);
        heroParty.addPartyMember(elyi);
//        heroParty.addPartyMember(kervs);
//        heroParty.addPartyMember(chaniy);

        //TEMPORARY ENEMY SETUP
        enemyParty = new Party("Swarm of Goblins");
        Goblin goblin_1 = new Goblin();
        Boss boss = new Boss("Serato The Destroyer" , 20,10,200,1,"Boss",200,3);
        Slime slime_1 = new Slime();
        Vampire vamp = new Vampire();
        enemyParty.addPartyMember(goblin_1);
        enemyParty.addPartyMember(boss);
        enemyParty.addPartyMember(slime_1);
        enemyParty.addPartyMember(vamp);

        createStartingItems();
    }

    private void createStartingItems() {
        Item smallPotion = new ResourceItem(
                "Small Potion",
                "Restores 50 HP to a selected target",
                null,
                Rarity.COMMON,
                50,0,
                TargetType.SINGLE_TARGET
        );

        heroParty.getInventory().addItem(smallPotion, 3);
    }

    public void startNewGame() {
        // dummy view to get the text area for synchronization reasons
        mainView = new BattleInterface();
        LogManager.initialize(mainView.getGameLogPanelTextPane(), mainView.getGameLogHighlightPanelTextPane());
        LogManager.log("Logger initialized with UI component", LogColor.SYSTEM);

        createGameModel();
        battleController = new BattleController(heroParty, enemyParty);
        battleController.setMainView(mainView);
        VisualEffectsManager.getInstance().setMainView(mainView);

        mainView.linkControllerAndData(battleController);
    }
}
