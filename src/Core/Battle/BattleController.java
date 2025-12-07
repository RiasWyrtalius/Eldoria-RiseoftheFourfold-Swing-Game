package Core.Battle;

import Abilities.Skill;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;
import Resource.Audio.AudioManager;
import Core.GameFlow.Level;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Items.Inventory;
import Items.Item;
import UI.Views.BattleInterface;

import java.util.ArrayList;
import java.util.List;

public class BattleController {
    private BattleInterface mainView;
    private final Party heroParty;
    private final Party enemyParty;
    private int turnCounter;
    private boolean isBattleActive;
    private BattlePhase currentPhase = BattlePhase.IDLE; // Default stat)e
    private BattleResult finalResult = BattleResult.NONE;
    private final Level currentLevel;

    //ui summary
    private int earnedXP = 0;
    private List<Item> earnedItems = new ArrayList<>();


    public BattleController(Party heroParty, Party enemyParty, Level currentLevel) {
        this.mainView = null;
        this.heroParty = heroParty;
        this.enemyParty = enemyParty;
        this.turnCounter = 1;
        this.isBattleActive = true;
        this.currentLevel = currentLevel;

//        VisualEffectsManager.getInstance().pauseAllAnimations();
        runBattleIntro();
    }

    public void runBattleIntro() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                LogManager.logHighlight("BATTLE", LogFormat.BATTLE_HEADER, LogFormat.SIZE_HEADER, false);
                Thread.sleep(500);
                LogManager.clearHighlight();
                LogManager.logHighlight("START!!", LogFormat.BATTLE_HEADER, LogFormat.SIZE_HEADER, false);
                Thread.sleep(500);
                LogManager.clearHighlight();

                LogManager.log("┌──── ∘°❉ - ❉°∘ ────┐", LogFormat.BATTLE_HEADER);
                LogManager.log("│   BATTLE START    │", LogFormat.BATTLE_HEADER);
                LogManager.log("└──── °∘❉ - ❉∘° ────┘", LogFormat.BATTLE_HEADER);

                VisualEffectsManager.getInstance().resumeAllAnimations();

                setCurrentPhase(BattlePhase.HERO_ACTION_WAIT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public boolean checkWin() {
        return enemyParty.isAllMembersDead();
    }
    public boolean checkLose() { return heroParty.isAllMembersDead(); }

    public boolean isBattleOver() {
        return checkWin() || checkLose();
    }

    private void resetTurnReadiness() {
        heroParty.setPartyExhaustion(false);
        enemyParty.setPartyExhaustion(false);
    }

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

    public void executeItemActionFromUI(Item item, List<Character> targets) {
        if (!isBattleActive) {
            LogManager.log("cannot use item right now.");
            return;
        }

        Inventory inventory = heroParty.getInventory();

        if (!inventory.consumeItem(item.getName())) {
            LogManager.log("You have no " + item.getName() + " left!", LogFormat.ENEMY_ACTION);
        }

        List<Character> finalTargets = (targets == null) ? new ArrayList<>() : new ArrayList<>(targets);

        Runnable onItemComplete = () -> {
            LogManager.log("Item used. Turn Cycle Advancing...");
            mainView.refreshUI();
            advanceTurnCycle(true); // Manual override to force phase change
        };

        LogManager.log("Party used " + item.getName() + "!", LogFormat.PLAYER_JOIN);
        item.use(null, finalTargets, onItemComplete);
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
        }

        if (this.mainView != null)
            this.mainView.refreshUI();
    }

    private void executeEnemyPhase() {
        LogManager.log("┌──── ∘°❉ - ❉°∘ ────┐", LogFormat.TURN_INDICATOR); //legit unnecessary fanciness..
        LogManager.log("│   ENEMY PHASE   │", LogFormat.TURN_INDICATOR); // the right amount of fanciness :D
        LogManager.log("└──── °∘❉ - ❉∘° ────┘", LogFormat.TURN_INDICATOR);

        setCurrentPhase(BattlePhase.ENEMY_ACTION);

        List<Character> enemies = enemyParty.getAliveMembers();
        processNextEnemy(enemies, 0); // starting from first
    }

    /**
     * TODO: find a way to make this randomized
     * Recursive helper in order to handle enemy ai one by one instead of all at once
     * @param enemies
     * @param index
     */
    private void processNextEnemy(List<Character> enemies, int index) {
        if (checkLose()) {
            endBattle();
        }

        if (index >= enemies.size()) {
            onEnemyPhaseComplete();
            return;
        }

        Character enemyChar = enemies.get(index);

        Runnable onCurrentEnemyFinished = () -> {
            processNextEnemy(enemies, index + 1);
        };

        // execute the attack
        List<Character> validTargets = heroParty.getAliveMembers();
        if (validTargets == null || validTargets.isEmpty()) {
            onEnemyPhaseComplete(); // no targets left oop
            return;
        }

        if (enemyChar instanceof Enemy) {
            ((Enemy) enemyChar).makeAttack(validTargets, onCurrentEnemyFinished);
        } else {
            onCurrentEnemyFinished.run();
        }

        if (this.mainView != null) this.mainView.refreshUI();
    }

    private void onEnemyPhaseComplete() {
        executeTurnCleanUp();
        resetTurnReadiness();

        turnCounter++;

        if(isBattleOver()) {
            endBattle();
            return;
        }

        setCurrentPhase(BattlePhase.HERO_ACTION_WAIT);
        LogManager.log("");
        LogManager.log("TURN " + turnCounter + " BEGINS", LogFormat.TURN_INDICATOR);

        if (this.mainView != null) this.mainView.refreshUI();
    }

    private void executeTurnCleanUp() {
        LogManager.log("┌──── ∘°❉ - ❉°∘ ────┐", LogFormat.TURN_INDICATOR);
        LogManager.log("│     Turn Recovery     │", LogFormat.TURN_INDICATOR);
        LogManager.log("└──── °∘❉ - ❉∘° ────┘", LogFormat.TURN_INDICATOR);

        List<Character> allCharacters = new ArrayList<>();
        allCharacters.addAll(heroParty.getPartyMembers());
        allCharacters.addAll(enemyParty.getPartyMembers());

        // only process effects for alive characters
        for (Character character : allCharacters) {
            if (character.isAlive()) {
                character.processTurnEffects();
                if (character instanceof Hero) {
                    ((Hero) character).regenerateTurnResources();
                }
            }
        }

        if (this.mainView != null) {
            this.mainView.refreshUI();
        }
    }

    public void endBattle() {
        if (!isBattleActive) return; // additional safety check

        resetTurnReadiness();
        isBattleActive = false;
        setCurrentPhase(BattlePhase.BATTLE_ENDED);

        if (checkLose() && checkWin()) {
            finalResult = BattleResult.TIE;
        } else if (checkWin()) {
            finalResult = BattleResult.VICTORY;
            AudioManager.getInstance().playSound("VICTORY_MUSIC_1");
            processVictoryRewards();
        } else if (checkLose()) {
            finalResult = BattleResult.DEFEAT;
        }

        //printBattleEndVisuals(finalResult); - replaced with battlesumamry.form

//        VisualEffectsManager.getInstance().stopAllTimers();

        if (this.mainView != null) {
            this.mainView.refreshUI();
        }
    }

    private void processVictoryRewards() {
        LogManager.log("--- STAGE CLEAR REWARDS ---", LogFormat.VICTORY);

        earnedXP = 0;
        earnedItems.clear();

        int xpBonus = currentLevel.xpReward();
        earnedXP = xpBonus;

        if (xpBonus > 0) {
            LogManager.log("Party gained " + xpBonus + " XP!");
            for (Character hero : heroParty.getPartyMembers()) {
                if (hero instanceof Hero) {
                    ((Hero) hero).gainXP(xpBonus);
                }
            }
        }

        List<Item> loot = currentLevel.possibleItemDrops();
        if (!loot.isEmpty()) {
            LogManager.log("Loot found!");
            for (Item item : loot) {


                heroParty.getInventory().addItem(item, 1);//updates game state
                earnedItems.add(item);//battleinterface
                LogManager.logHighlight("+" + xpBonus + " XP", LogFormat.HIGHLIGHT_LEVELUP, LogFormat.SIZE_IMPACT, false);
                LogManager.log(" - " + item.getName(), LogFormat.PLAYER_JOIN);
            }
        } else {
            LogManager.log("No items found.");
        }
    }

    // =============== PUBLIC SETTERS AND GETTERS FOR UI ===============
    public void setMainView(BattleInterface mainView) {
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

    public int getLevelNumber() {
        return currentLevel.levelNumber();
    }

    public int getEarnedXP() {
        return earnedXP;
    }

    public List<Item> getEarnedItems() {
        return earnedItems;
    }
}