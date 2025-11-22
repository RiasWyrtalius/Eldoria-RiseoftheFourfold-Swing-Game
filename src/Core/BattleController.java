package Core;

import Characters.Character;
import Characters.Party;

public class BattleController {
    private final Party heroParty;
    private final Party enemyParty;
    private int turnCounter;

    public BattleController(Party heroParty, Party enemyParty, int turnCounter) {
        this.heroParty = heroParty;
        this.enemyParty = enemyParty;
        this.turnCounter = turnCounter;

        LogManager.log("--- BATTLE START: Turn " + turnCounter + " ---");
    }

    public BattleController(Party heroParty, Party enemyParty) {
        this(heroParty, enemyParty, 1);
    }

    public boolean checkWin() {
        return enemyParty.getPartyMembers().stream()
                .noneMatch(Character::isAlive);
    }

    public boolean checkLose() {
        return heroParty.getPartyMembers().stream()
                .allMatch(Character::isAlive);
    }

    public boolean isBattleOver() {
        return checkWin() || checkLose();
    }

    public void startNextTurn() {
        if (isBattleOver()) {
            endBattle();
            return;
        }


    }

    private void executeEnemyPhase() {

    }

    private void executeTurnCleanUp() {
//        TODO: bruh put here the logic for status ticks, mana regen, etc..

    }

    public void endBattle() {
        if (checkLose() && checkWin()) {
            LogManager.log("TIE!: Truly everyone is dead and gone.");
        } else if (checkWin()) {
            LogManager.log("VICTORY! " + heroParty.getPartyName() + " is Triumphant!");
        } else if (checkLose()) {
            LogManager.log("DEFEAT! " + enemyParty.getPartyName() + " has wiped " + heroParty.getPartyName() + " out!");
        }
    }
}
