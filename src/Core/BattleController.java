package Core;

import Abilities.Skill;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BattleController {
    private final Party heroParty;
    private final Party enemyParty;
    private int turnCounter;
    private boolean isBattleActive;
    private BattlePhase currentPhase = BattlePhase.HERO_ACTION_WAIT; // Default state

    public BattleController(Party heroParty, Party enemyParty) {
        this.heroParty = heroParty;
        this.enemyParty = enemyParty;
        this.turnCounter = 1;
        this.isBattleActive = true;

        LogManager.log("--- BATTLE START ---");
    }

    public boolean checkWin() {
        return enemyParty.isAllMembersDead();
    }
    public boolean checkLose() { return heroParty.isAllMembersDead(); }

    public boolean isBattleOver() {
        return checkWin() || checkLose();
    }

    private void resetTurnReadiness() {
        LogManager.log("Turn " + turnCounter);
        heroParty.setPartyExhaustion(false);
        enemyParty.setPartyExhaustion(false);
    }

    // TODO: The UI must now check isExhausted() to enable/disable the hero portrait.

    public void executeActionFromUI(Hero hero, Skill skill, Character target) {
        if (!isBattleActive || !hero.isAlive() || hero.isExhausted()) {
            LogManager.log(hero.getName() + " cannot act right now.");
            return;
        }

        hero.useSkill(skill, target);
        hero.setExhausted(true);
    }

    public void endHeroPhaseManually() {
        LogManager.log("");
        LogManager.log(heroParty.getPartyName() + " ends their turn early.");
        advanceTurnCycle(true);
    }

    /**
     * controls the flow of the battle
     * @param manualOverride
     */
    public void advanceTurnCycle(boolean manualOverride) {
        if (isBattleOver()) {
            endBattle();
            return;
        }

        /**
         * end hero phase if all members are exhausted or
         * player clicked the end turn button
         */
        if (manualOverride || heroParty.isAllMembersExhausted()) {
            LogManager.log(heroParty.getPartyName() + "'s turn has ended.");

            executeEnemyPhase();
            resetTurnReadiness();
            executeTurnCleanUp();

            if (isBattleOver()) {
                endBattle();
                return;
            }
            currentPhase = BattlePhase.HERO_ACTION_WAIT;
            turnCounter++;
            LogManager.log("");
            LogManager.log("TURN " + turnCounter + " BEGINS");

            // pause here waiting for hero selection
        }
    }

    private void executeEnemyPhase() {
        LogManager.log("\n+=============+"); //legit unnecessary fanciness..
        LogManager.log("\n| ENEMY PHASE |"); // the right amount of fanciness :D
        LogManager.log("\n+=============+");

        currentPhase = BattlePhase.ENEMY_ACTION;

        for (Character enemy : enemyParty.getAliveMembers()) {

            List<Character> validTargets = heroParty.getAliveMembers();

            if (validTargets.isEmpty()) break;

            int randomIndex = (int) (Math.random() * validTargets.size());
            Character target = validTargets.get(randomIndex);

            LogManager.log(enemy.getName() + " attacks " + target.getName() + "!");

            ((Enemy)enemy).makeAttack(validTargets);

            if (checkLose()) return;
        }
    }

    private void executeTurnCleanUp() {
        // TODO: mana regen, poison, etc.
        // "will finish tomorrow" - Charlz
        LogManager.log("Turn Cleanup: Mana Regenerated, Status Effect Ticked.");
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

    public BattlePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(BattlePhase currentPhase) {
        this.currentPhase = currentPhase;
    }
}
