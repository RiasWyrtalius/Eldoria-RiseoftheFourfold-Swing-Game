package Core;

import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;

import java.util.List;
import java.util.Scanner;

public class BattleController {
    private final Party heroParty;
    private final Party enemyParty;
    private int turnCounter;
    private boolean isBattleActive;
    private final Scanner scan;

    public BattleController(Party heroParty, Party enemyParty) {
        this.heroParty = heroParty;
        this.enemyParty = enemyParty;
        this.turnCounter = 1;
        this.isBattleActive = true;
        this.scan = new Scanner(System.in);

        LogManager.log("--- BATTLE START ---");
    }

    public void startConsoleBattle() {
        while(isBattleActive) {
            startNextTurn();
        }
    }

    public boolean checkWin() {
        return enemyParty.getPartyMembers().stream()
                .noneMatch(Character::isAlive);
    }

    public boolean checkLose() {
        return heroParty.getPartyMembers().stream()
                .noneMatch(Character::isAlive);
    }

    public boolean isBattleOver() {
        return checkWin() || checkLose();
    }

    public void startNextTurn() {
        if (isBattleOver()) {
            endBattle();
            return;
        }

        LogManager.log("\n+========+");
        LogManager.log("| TURN " + turnCounter +" |");
        LogManager.log("+========+");

        executeHeroTurn();

        if (checkWin()) {
            endBattle();
            return;
        }

        executeEnemyPhase();
        if (checkLose()) {
            endBattle();
            return;
        }

        executeTurnCleanUp();
    }


    // TODO: UI linkage HEHEHEHEHEH
    private void executeHeroTurn() {
        for (Character hero : heroParty.getAliveMembers()) {
            System.out.println("\nIt is " + hero.getName() + "'s turn!");
            System.out.println("1. Attack\t2. Defend");

            int choice = -1;
            while(choice < 1 || choice > 2) {
                System.out.print("Choose action: ");
                if (scan.hasNextInt()) choice = scan.nextInt();
                else scan.next(); //clear
            }

            if (choice == 1) {
                Character target = selectEnemyTarget(enemyParty.getAliveMembers());

                if (target != null) {
                    LogManager.log(hero.getName() + " attacks " + target.getName() + "!");
                    Hero.attack(target);
                    target.takeDamage(10);
                }
            } else {
                LogManager.log(hero.getName() + " braces for impact!");
            }

            if (checkWin()) return;
        }
    }

    private void executeEnemyPhase() {
        LogManager.log("\n+=============+"); //legit unnecessary fanciness..
        LogManager.log("\n| ENEMY PHASE |"); // the right amount of fanciness :D
        LogManager.log("\n+=============+");

        for (Character enemy : enemyParty.getAliveMembers()) {
//            you can just call getAliveHeroes
            Character randomAliveHero = heroParty.getRandomAliveMember();

            if (randomAliveHero == null) break;

            LogManager.log(enemy.getName() + " attacks " + randomAliveHero.getName() + "!");

            //enemy.attack(target);

            if (checkLose()) return;
        }
    }

    private Character selectEnemyTarget(List<Character> enemies) {
        System.out.println("Select Target: ");
        for (int i = 0; i < enemies.size(); i++) {
            System.out.println((i + 1) + ". " + enemies.get(i).getName() +
                    " (HP: " + enemies.get(i).getHealth() + ")");
        }

        int targetIndex = -1;
        while(targetIndex < 0 || targetIndex >= enemies.size()) {
            System.out.print("Target #: ");
            if (scan.hasNextInt()) targetIndex = scan.nextInt() - 1;
            else scan.next();
        }

        return enemies.get(targetIndex);
    }

    private void executeTurnCleanUp() {
        turnCounter++;
        // TODO: mana regen, poison, etc.
        // "will finish tomorrow" - Charlz
    }

    public void endBattle() {

        isBattleActive = false;

        if (checkLose() && checkWin()) {
            LogManager.log("TIE!: Truly everyone is dead and gone.");
        } else if (checkWin()) {
            LogManager.log("VICTORY! " + heroParty.getPartyName() + " is Triumphant!");
        } else if (checkLose()) {
            LogManager.log("DEFEAT! " + enemyParty.getPartyName() + " has wiped " + heroParty.getPartyName() + " out!");
        }
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    public Party getHeroParty() {
        return heroParty;
    }

    public Party getEnemyParty() {
        return enemyParty;
    }

}
