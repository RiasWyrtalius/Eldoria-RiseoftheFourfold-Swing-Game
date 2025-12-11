package Characters;

import Abilities.*;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Character {
    protected String name;

    protected int level;
    protected int health;
    protected int mana;

    protected int baseMaxHealth;
    protected int baseMaxMana;
    protected int baseAtk;

    protected int maxHealth;
    protected int maxMana;

    protected boolean isAlive = true;
    protected boolean isExhausted = false;

    protected List<ReactionSkill> reactions = new ArrayList<>();
    protected List<StatusEffect> activeStatusEffects = new ArrayList<>();

    public Character(String name, int baseHealth, int baseAtk, int baseMana, int level) {
        this.name = name;

        this.baseMaxHealth = baseHealth;
        this.baseMaxMana = baseMana;
        this.baseAtk = baseAtk;

        this.level = level;

        this.maxHealth = baseHealth;
        this.maxMana = baseMana;
        this.health = maxHealth;
        this.mana = maxMana;
    }

    public Character(String name, int baseHealth, int baseAtk, int maxMana) {
        this(name, baseHealth, baseAtk, maxMana, 1);
    }

    /**
     * Kicks off the asynchronous reaction queue for damage events.
     */
    protected void processDamageReactions(Character attacker, Skill incomingSkill, int incomingDamage, Consumer<ReactionResult> onComplete) {
        VisualEffectsManager.getInstance().flashDamage(this);

        List<ReactionSkill> queue = this.reactions.stream()
                .filter(r -> r.trigger() == ReactionTrigger.ON_RECEIVE_DAMAGE)
                .collect(Collectors.toList());

        processReactionQueue(queue, incomingDamage, attacker, incomingSkill, onComplete);
    }

    /*
     * called by skill logic, reaction logic, effect logic and item logic
     */
    public void receiveDamage(int rawDamage, Character attacker, Skill incomingSkill, Runnable onDamageResolved) {

        // This is the final callback after ALL reactions are done.
        Consumer<ReactionResult> afterReactionsCallback = (finalResult) -> {

            int finalDamage = finalResult.newDamageValue();

            if (!finalResult.wasTriggered()) {
                LogManager.log(this.name + " takes " + finalDamage + " damage.");
                VisualEffectsManager.getInstance().showFloatingText(this, "-" + finalDamage + "HP", LogFormat.DAMAGE_TAKEN);
            } else {
                LogManager.log(this.name + " reaction mitigated damage!");
            }

            int potentialHealth = this.health - finalDamage;

            if (potentialHealth <= 0) {

                Consumer<Boolean> onFatalCheckComplete = (wasSaved) -> {
                    if (!wasSaved) {
                        setHealth(potentialHealth, attacker);
                    }
                    if (onDamageResolved != null) onDamageResolved.run();
                };

                processFatalReactions(attacker, incomingSkill, onFatalCheckComplete);

            } else {
                setHealth(potentialHealth, attacker);
                if (onDamageResolved != null) onDamageResolved.run();
            }
        };

        processDamageReactions(attacker, incomingSkill, rawDamage, afterReactionsCallback);
    }

    protected void processFatalReactions(Character attacker, Skill incomingSkill, Consumer<Boolean> onComplete) {
        List<ReactionSkill> queue = this.reactions.stream()
                .filter(r -> r.trigger() == ReactionTrigger.ON_FATAL_DAMAGE)
                .collect(Collectors.toList());
        processFatalReactionQueue(queue, attacker, incomingSkill, onComplete);
    }

    /**
     * Asynchronously checks a queue of ON_FATAL_DAMAGE reactions.
     * @param onChainComplete Callback that receives 'true' if saved, 'false' otherwise.
     */
    private void processFatalReactionQueue(List<ReactionSkill> queue, Character attacker, Skill skill, Consumer<Boolean> onChainComplete) {
        if (queue.isEmpty()) {
            onChainComplete.accept(false); // No more reactions, character was not saved.
            return;
        }

        ReactionSkill currentReaction = queue.remove(0);

        Consumer<ReactionResult> onThisReactionFinished = (result) -> {
            if (result.wasTriggered()) {
                onChainComplete.accept(true);
            } else {
                processFatalReactionQueue(queue, attacker, skill, onChainComplete);
            }
        };
        // Pass 0 as damage context, the 'wasTriggered' flag is what matters.
        currentReaction.logic().tryReact(this, attacker, skill, 0, onThisReactionFinished);
    }

    /**
     * The recursive engine for processing a chain of potentially animated reactions.
     */
    private void processReactionQueue(List<ReactionSkill> queue, int currentDamage,
                                      Character attacker, Skill skill,
                                      Consumer<ReactionResult> onChainComplete) {

        if (queue.isEmpty()) {
            onChainComplete.accept(ReactionResult.FAILED(currentDamage)); // Pass final value up
            return;
        }

        ReactionSkill currentReaction = queue.remove(0);

        Consumer<ReactionResult> onThisReactionFinished = (result) -> {

            if (result.wasTriggered() && result.isFinal()) {
                onChainComplete.accept(result);
            }
            else {
                processReactionQueue(queue, result.newDamageValue(), attacker, skill, onChainComplete);
            }
        };

        currentReaction.logic().tryReact(this, attacker, skill, currentDamage, onThisReactionFinished);
    }

    /**
     * Synchronous processor for instant reactions (Heal, Mana, Revive, Fatal).
     */
    private int processSyncReactions(ReactionTrigger trigger, Character source, Skill skill, int inputVal) {
        int currentValue = inputVal;

        for (ReactionSkill reaction : reactions) {
            if (reaction.trigger() == trigger) {

                final ReactionResult[] syncResult = {null};

                reaction.logic().tryReact(this, source, skill, currentValue, res -> syncResult[0] = res);

                if (syncResult[0] != null && syncResult[0].wasTriggered()) {
                    currentValue = syncResult[0].newDamageValue();
                    if (currentValue <= 0 || syncResult[0].isFinal()) {
                        return currentValue;
                    }
                }
            }
        }
        return currentValue;
    }

    public final void die() {
        this.health = 0;
        this.isAlive = false;
        onDeath();
        setMana(0);
    }

    // Subclass Hooks
    protected void onDeath() {
        VisualEffectsManager.getInstance().pauseCharacterAnimation(this);
    }

    protected void onRevive() {
        VisualEffectsManager.getInstance().resumeCharacterAnimation(this);
        VisualEffectsManager.getInstance().reviveEffect(this);
    }

    protected void onDefeat(Character attacker) {
        if (attacker == null) return;
        LogManager.log(this.name + " is killed by " + attacker.getName() + "!", LogFormat.ENEMY_ACTION);
    }

    public boolean canCast(int manaCost) {
        return this.mana >= manaCost;
    }

    public void gainMana(int amount) {
//        LogManager.log("mana amount received: " + amount, LogFormat.DEBUG_INFO);
        setMana(this.mana + amount);

        if (amount > 0) {
            Core.Utils.LogManager.log(this.name + " recovered " + amount + " MP.", LogFormat.MP);
            VisualEffectsManager.getInstance().showFloatingText(this, "+" + amount +"MP", LogFormat.MP);
        }
    }

    public boolean spendMana(int manaCost) {
        // TODO: Front end logging for spending mana here or within in the
        //      subclass method called
        if (canCast(manaCost)) {
            setMana(this.mana - manaCost);
            VisualEffectsManager.getInstance().showFloatingText(this, "-" + manaCost +"MP", LogFormat.MP);
            return true;
        } else {
            return false;
        }
    }

    public void attack(Character target) {
        LogManager.log("(CHARACTER) : " + this.name + " attacks " + target.getName());
    }

    public void receiveHealing(int rawAmount, Character source) {
        int finalAmount = processSyncReactions(ReactionTrigger.ON_RECEIVE_HEAL, source, null, rawAmount);

        setHealth(this.health + finalAmount, source);

        if (finalAmount > 0) {
            LogManager.log(this.name + " recovers " + finalAmount + " HP.", LogFormat.HP);
            VisualEffectsManager.getInstance().showFloatingText(this, "+" + finalAmount +"HP", LogFormat.HP);
        }
    }

    public void revive(int amount, Character source) {
        if (this.isAlive) {
            // this shouldn't happen as the targeting ensures that
            // revive abilities or items is not called on alive characters
            LogManager.log(this.name + " is already alive!");
            return;
        }

        setHealth(amount, source);
    }

    public void receiveMana(int rawAmount, Character source) {
        int finalAmount = processSyncReactions(ReactionTrigger.ON_RECEIVE_MANA, source, null, rawAmount);
        gainMana(finalAmount);
    }

    public void setHealth(int newHealth, Character source) {
        if(newHealth > maxHealth){
            newHealth = maxHealth;
        }
        if (newHealth < 0) {
            newHealth = 0;
        }
        this.health = newHealth;
        if (this.health == 0 && this.isAlive) {
            die();

            onDefeat(source);
        }
        else if (this.health > 0 && !this.isAlive) {
            reviveState(source);
        }
    }

    private void reviveState(Character source) {
        this.isAlive = true;

        // Trigger ON_REVIVE reactions
        processSyncReactions(ReactionTrigger.ON_REVIVE, source, null, 0);

        if (source != null) {
            LogManager.log(this.name + " was revived by " + source.getName() + "!");
        } else {
            LogManager.log(this.name + " has been revived!");
        }

        onRevive();
    }

    public void setMaxHealth(int maxHealth){this.maxHealth = maxHealth;}
    public void setMaxMana(int maxMana){this.maxMana = maxMana;}

    public void addReaction(ReactionSkill reaction) {
        this.reactions.add(reaction);
    }

//    protected int processReactions(ReactionTrigger trigger, Character source, Skill skill, int inputVal, Consumer<Integer> onComplete) {
//
//        int currentValue = inputVal;
//
//        if (trigger == ReactionTrigger.ON_RECEIVE_DAMAGE) {
//            VisualEffectsManager.getInstance().flashDamage(this);
//        }
//
//        for (ReactionSkill reaction : reactions) {
//            if (reaction.trigger() != trigger) continue;
//
//            int result = reaction.logic().tryReact(this, source, skill, currentValue, onComplete);
//
//            if (result != -1) {
//
//                switch (trigger) {
//                    case ON_RECEIVE_DAMAGE:
//                    case ON_RECEIVE_HEAL:
//                    case ON_RECEIVE_MANA:
//                        currentValue = result;
//                        // If 0 then optionally stop processing further reactions
//                        if (currentValue <= 0) {
//                            return 0;
//                        }
//                        break;
//                    case ON_FATAL_DAMAGE:
//                        return 1; // 1 = True (Saved)
//
//                    case ON_REVIVE:
//                        // side effects only
//                        break;
//                }
//            }
//        }
//
//        // Default returns if loops finish without short-circuiting
//        if (trigger == ReactionTrigger.ON_FATAL_DAMAGE) return 0;
//
//        return currentValue;
//    }


    public void applyStatusEffect(StatusEffect effect) {
        for (StatusEffect existingEffect : activeStatusEffects) {
            if (existingEffect.getName().equalsIgnoreCase(effect.getName())) {
                LogManager.log(this.name + "'s" + effect.getName() + " duration is extended.", LogFormat.SYSTEM);
                existingEffect.setDuration(existingEffect.getDuration() + effect.getDuration());
            }
        }
        activeStatusEffects.add(effect);

        Color color = (effect.getType() == StatusEffectType.BUFF) ? LogFormat.HIGHLIGHT_BUFF : LogFormat.HIGHLIGHT_DEBUFF;

        LogManager.log(this.name + " is afflicted with " + effect.getName() + "!", color);
    }

    public void removeStatusEffect(StatusEffect effect) {
        if (activeStatusEffects.remove(effect)) {
            LogManager.log(this.name + " has been cleansed from " + effect.getName() + "!", LogFormat.DEFEAT);
        }
    }

    public void processTurnEffects() {
        if (activeStatusEffects.isEmpty()) return;
        // just discovered u can use this to safely remove   items while looping "w <3" - charlz
        // so we dont gotta do like i = 0, needs java 24 tho
        Iterator<StatusEffect> iterator = activeStatusEffects.iterator();
        while (iterator.hasNext()) {
            StatusEffect effect = iterator.next();
            effect.tick(this);
            if (effect.getDuration() <= 0) {
                LogManager.log(effect.getName() + " wore off of " + this.name + ".", LogFormat.SYSTEM);
                iterator.remove();
            }
        }
    }

    public boolean isExhausted() {
        return isExhausted;
    }

    // =============== PUBLIC GETTERS AND SETTERS ===============
    public int getMaxHealth() {
        return maxHealth;
    }
    public String getName() {
        return name;
    }
    public int getHealth() {
        return health;
    }
    public boolean isAlive() {
        return isAlive;
    }
    public int getBaseAtk() {
        return baseAtk;
    }
    public int getLevel() {
        return level;
    }
    public int getMana() { return mana; }
    public int getMaxMana() {
        return maxMana;
    }
    public int getBaseMaxHealth() {
        return baseMaxHealth;
    }
    public int getBaseMaxMana() {
        return baseMaxMana;
    }
    public void setExhausted(boolean exhausted) {
        isExhausted = exhausted;
    }
    public void setMana(int newMana) {
        if (newMana > maxMana) {
            newMana = maxMana;
        }
        if (newMana < 0) {
            newMana = 0;
        }
        this.mana = newMana;
    }
    public abstract List<Skill> getSkills();
    public abstract String getDescription();
    public abstract String getIdleImageKey();
    public void setName(String name) { this.name = name; }
}
