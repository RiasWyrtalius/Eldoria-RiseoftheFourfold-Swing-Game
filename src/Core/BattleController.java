package Core;

import Abilities.Skill;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;
import Resource.AssetManager;
import UI.MainInterface;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// FIXME: Turn and game doesn’t end automatically when final blow is from an animation
public class BattleController {
    private MainInterface mainView;
    private final Party heroParty;
    private final Party enemyParty;
    private int turnCounter;
    private boolean isBattleActive;
    private BattlePhase currentPhase = BattlePhase.HERO_ACTION_WAIT; // Default state
    private BattleResult finalResult = BattleResult.NONE;

    public BattleController(Party heroParty, Party enemyParty) {
        this.mainView = null;
        this.heroParty = heroParty;
        this.enemyParty = enemyParty;
        this.turnCounter = 1;
        this.isBattleActive = true;

        LogManager.log("+==============+", LogColor.BATTLE_HEADER);
        LogManager.log("|   BATTLE START   |", LogColor.BATTLE_HEADER);
        LogManager.log("+==============+", LogColor.BATTLE_HEADER);
    }

    public boolean checkWin() {
        return enemyParty.isAllMembersDead();
    }
    public boolean checkLose() { return heroParty.isAllMembersDead(); }

    public boolean isBattleOver() {
        return checkWin() || checkLose();
    }

    private void resetTurnReadiness() {
        LogManager.log("Turn " + turnCounter, LogColor.TURN_INDICATOR);
        heroParty.setPartyExhaustion(false);
        enemyParty.setPartyExhaustion(false);
    }

    // TODO: The UI must now check isExhausted() to enable/disable the hero portrait.

    public void executeActionFromUI(Hero hero, Skill skill, List<Character> targets) {
        if (!isBattleActive || !hero.isAlive() || hero.isExhausted()) {
            LogManager.log(hero.getName() + " cannot act right now.");
            return;
        }

        Runnable onSkillComplete = () -> {
            advanceTurnCycle(false);
        };

        hero.useSkill(skill, targets, onSkillComplete);
        hero.setExhausted(true);

        if (this.mainView != null)
            this.mainView.refreshUI();
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
            LogManager.log("TURN " + turnCounter + " BEGINS", LogColor.TURN_INDICATOR);

            // pause here waiting for hero selection
        }

        if (this.mainView != null)
            this.mainView.refreshUI();
    }

    private void executeEnemyPhase() {
        LogManager.log("+=============+", LogColor.TURN_INDICATOR); //legit unnecessary fanciness..
        LogManager.log("| ENEMY PHASE |", LogColor.TURN_INDICATOR); // the right amount of fanciness :D
        LogManager.log("+=============+", LogColor.TURN_INDICATOR);

        currentPhase = BattlePhase.ENEMY_ACTION;

        for (Character enemy : enemyParty.getAliveMembers()) {

            List<Character> validTargets = heroParty.getAliveMembers();

            if (validTargets.isEmpty()) break;

            int randomIndex = (int) (Math.random() * validTargets.size());
            Character target = validTargets.get(randomIndex);

            LogManager.log(enemy.getName() + " attacks " + target.getName() + "!", LogColor.ENEMY_ACTION);

            ((Enemy)enemy).makeAttack(validTargets);

            if (checkLose()) return;
        }

        if (this.mainView != null)
            this.mainView.refreshUI();
    }

    private void executeTurnCleanUp() {
        // TODO: mana regen, poison, etc.
        LogManager.log("Turn Cleanup: Mana Regenerated, Status Effect Ticked.", LogColor.TURN_INDICATOR);
    }

    public void endBattle() {
        isBattleActive = false;
        currentPhase = BattlePhase.BATTLE_ENDED;

        if (checkLose() && checkWin()) {
            LogManager.log("TIE!: Truly everyone is dead and gone.", LogColor.TIE);
            finalResult = BattleResult.TIE;
        } else if (checkWin()) {
            LogManager.log("VICTORY! " + heroParty.getPartyName() + " is Triumphant!", LogColor.VICTORY);
            finalResult = BattleResult.VICTORY;
        } else if (checkLose()) {
            LogManager.log("DEFEAT! " + enemyParty.getPartyName() + " has wiped " + heroParty.getPartyName() + " out!", LogColor.DEFEAT);
            finalResult = BattleResult.DEFEAT;
        }

        VisualEffectsManager.getInstance().stopAllTimers();

        if (this.mainView != null) {
            this.mainView.refreshUI();
        }
    }

    // =============== PUBLIC SETTERS AND GETTERS FOR UI ===============
    public void setMainView(MainInterface mainView) {
        this.mainView = mainView;
    }

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

    public BattleResult getFinalResult() {
        return finalResult;
    }
}
