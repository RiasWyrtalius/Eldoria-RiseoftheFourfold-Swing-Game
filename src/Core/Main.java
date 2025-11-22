package Core;
import Characters.Enemies.Goblin;
import UI.MainMenu;

//TEMPORARY IMPORTS
import Characters.Party;
import Abilities.Jobs.FireMage;
import Characters.Base.Hero;

public class Main {
    public static void main(String[] args) {

        // initialize
        new MainMenu();

        //TEMPORARY HERO SETUP
        Party heroesParty = new Party("The Godslayers");
        FireMage mage_Fire = new FireMage();
        Hero player_1 = new Hero("Rias", 100, 100, 100, mage_Fire);
        heroesParty.addPartyMember(player_1);

        //TEMPORARY ENEMY SETUP
        Party enemiesParty = new Party("Swarm of Goblins");
        Goblin goblin_1 = new Goblin();
        Goblin goblin_2 = new Goblin();
        enemiesParty.addPartyMember(goblin_1);
        enemiesParty.addPartyMember(goblin_2);


        BattleController battle = new BattleController(heroesParty, enemiesParty);
        battle.startConsoleBattle();
    }
}
