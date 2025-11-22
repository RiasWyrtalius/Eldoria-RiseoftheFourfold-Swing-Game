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

    private void executeHeroTurn() {
        List<Character> heroes = heroParty.getPartyMembers();
        List<Character> enemies = enemyParty.getPartyMembers();

        for (Character hero : heroes) {
            if (!hero.isAlive()) continue; //skips defeated Heroes

            System.out.println("\nIt is " + hero.getName() + "'s turn!");
            System.out.println("1. Attack\t2. Defend");

            int choice = -1;
            while(choice < 1 || choice > 2) {
                System.out.print("Choose action: ");
                if (scan.hasNextInt()) choice = scan.nextInt();
                else scan.next(); //clear
            }

            if (choice == 1) {
                Character target = selectEnemyTarget(enemies);

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
        LogManager.log("\n| ENEMY PHASE |");
        LogManager.log("\n+=============+");

        List<Character> heroes = heroParty.getPartyMembers();
        List<Character> enemies = enemyParty.getPartyMembers();

        for (Character enemy : enemies) {
            if (!enemy.isAlive()) continue;

            List<Character> aliveHeroes = heroes.stream().filter(Character::isAlive).toList();

            if (aliveHeroes.isEmpty()) break;

            Character target = aliveHeroes.get((int)(Math.random() * aliveHeroes.size()));

            LogManager.log(enemy.getName() + " attacks " + target.getName() + "!");

            //enemy.attack(target);

            if (checkLose()) return;
        }
    }

    private Character selectEnemyTarget(List<Character> enemies) {
        System.out.println("Select Target: ");
        List<Character> aliveEnemies = enemies.stream().filter(Character::isAlive).toList();


        for (int i = 0; i < aliveEnemies.size(); i++) {
            System.out.println((i + 1) + ". " + aliveEnemies.get(i).getName() +
                    " (HP: " + aliveEnemies.get(i).getHealth() + ")");
        }

        int targetIndex = -1;
        while(targetIndex < 0 || targetIndex >= aliveEnemies.size()) {
            System.out.print("Target #: ");
            if (scan.hasNextInt()) targetIndex = scan.nextInt() - 1;
            else scan.next();
        }

        return aliveEnemies.get(targetIndex);
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
}
