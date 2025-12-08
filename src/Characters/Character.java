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
    protected void processDamageReactions(Character attacker, Skill incomingSkill, int incomingDamage, Consumer<Integer> onComplete) {
        VisualEffectsManager.getInstance().flashDamage(this);

        List<ReactionSkill> queue = this.reactions.stream()
                .filter(r -> r.trigger() == ReactionTrigger.ON_RECEIVE_DAMAGE)
                .collect(Collectors.toList());

        // start the recursive chain
        processReactionQueue(queue, incomingDamage, attacker, incomingSkill, onComplete);
    }


    /*
     * called by skill logic, reaction logic, effect logic and item logic
     */
    public void receiveDamage(int rawDamage, Character attacker, Skill incomingSkill, Runnable onDamageResolved) {
        LogManager.log("receive damage debug: " + rawDamage, LogFormat.DEBUG_INFO);
        Consumer<Integer> afterReactions = (finalDamage) -> {
            if (finalDamage == 0) {
                LogManager.log(this.name + " took no damage (Mitigated)!");
            } else {
                LogManager.log(this.name + " takes " + finalDamage + " damage.");
                VisualEffectsManager.getInstance().showFloatingText(this, "-" + finalDamage + "HP", LogFormat.DAMAGE_TAKEN);
            }
            int potentialHealth = this.health - finalDamage;

            // fatal check
            if (potentialHealth <= 0) {
                boolean wasSaved = processFatalReactions(attacker, incomingSkill);
                if (wasSaved) {
                    if (onDamageResolved != null) onDamageResolved.run();
                    return;
                }
            }

            setHealth(potentialHealth, attacker);
            if (onDamageResolved != null) onDamageResolved.run();
        };

        processDamageReactions(attacker, incomingSkill, rawDamage, afterReactions);
    }

    protected boolean processFatalReactions(Character attacker, Skill incomingSkill) {
        for (ReactionSkill reaction : reactions) {
            if (reaction.trigger() == ReactionTrigger.ON_FATAL_DAMAGE) {
                // use a dummy consumer because this path is sync
                final int[] result = {-1};
                reaction.logic().tryReact(this, attacker, incomingSkill, 0, res -> result[0] = res);
                if (result[0] != -1) return true;
            }
        }
        return false;
    }


    private void processReactionQueue(List<ReactionSkill> queue, int currentDamage, Character attacker, Skill skill, Consumer<Integer> onChainComplete) {
        if (queue.isEmpty() || currentDamage <= 0) {
            onChainComplete.accept(currentDamage);
            return;
        }

        // get next reaction
        ReactionSkill currentReaction = queue.remove(0);

        Consumer<Integer> onReactionFinished = (newDamage) -> {
            processReactionQueue(queue, newDamage, attacker, skill, onChainComplete);
        };

        currentReaction.logic().tryReact(this, attacker, skill, currentDamage, onReactionFinished);
    }

    /**
     * Synchronous processor for instant reactions (Heal, Mana, Revive, Fatal).
     */
    private int processSyncReactions(ReactionTrigger trigger, Character source, Skill skill, int inputVal) {
        int currentValue = inputVal;

        for (ReactionSkill reaction : reactions) {
            if (reaction.trigger() == trigger) {

                // Use a wrapper to capture the result from the lambda
                final int[] syncResult = { -1 };

                // The lambda here runs immediately if the reaction has no animation
                reaction.logic().tryReact(this, source, skill, currentValue, res -> syncResult[0] = res);

                // IMPORTANT: This only works if your Heal/Mana reactions are INSTANT
                // (i.e., they call onComplete.accept() immediately).

                // Only update if the reaction was successful (did not return -1)
                if (syncResult[0] != -1) {
                    currentValue = syncResult[0];

                    // Optional: If value is 0, we can stop checking
                    if (currentValue <= 0) return 0;
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
        // just discovered u can use this to safely remove   items while looping
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
