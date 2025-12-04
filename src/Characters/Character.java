package Characters;

import Abilities.ReactionSkill;
import Abilities.ReactionTrigger;
import Abilities.Skill;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Character {
    protected String name;

    protected int initialHealth;
    protected int health;
    protected boolean isAlive = true;

    protected int level;
    protected int baseAtk;

    protected int mana;
    protected int maxMana;

    protected boolean isExhausted = false;

    protected List<ReactionSkill> reactions = new ArrayList<>();

    public Character(String name, int initialHealth, int baseAtk, int maxMana, int level) {
        this.name = name;
        this.initialHealth = initialHealth;
        this.health = initialHealth;
        this.baseAtk = baseAtk;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.level = level;
    }

    public Character(String name, int health, int baseAtk, int maxMana) {
        this(name, health, baseAtk, maxMana, 1);
    }

    public void receiveDamage(int rawDamage, Character attacker, Skill incomingSkill) {
        int finalDamage = processReactions(ReactionTrigger.ON_RECEIVE_DAMAGE, attacker, incomingSkill, rawDamage);

        if (finalDamage == 0) {
            LogManager.log(this.name + " took no damage (Mitigated)!");
        } else {
            LogManager.log(this.name + " takes " + finalDamage + " damage.");
        }

        int potentialHealth = this.health - finalDamage;

        if (potentialHealth <= 0) {
            int savedCheck = processReactions(ReactionTrigger.ON_FATAL_DAMAGE, attacker, incomingSkill, 0);
            boolean wasSaved = (savedCheck == 1);

            if (wasSaved) {
                return;
            }
        }

        setHealth(potentialHealth, attacker);
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
    };

    public boolean canCast(int manaCost) {
        return this.mana >= manaCost;
    }

    public void gainMana(int amount) {
//        LogManager.log("mana amount received: " + amount, LogFormat.DEBUG_INFO);
        setMana(this.mana + amount);

        if (amount > 0) {
            Core.Utils.LogManager.log(this.name + " recovered " + amount + " MP.", LogFormat.MP);
        }
    }

    public boolean spendMana(int manaCost) {
        // TODO: Front end logging for spending mana here or within in the
        //      subclass method called
        if (canCast(manaCost)) {
            setMana(this.mana - manaCost);
            return true;
        } else {
            return false;
        }
    }

    public void attack(Character target) {
        LogManager.log("(CHARACTER) : " + this.name + " attacks " + target.getName());
    }

    public void receiveHealing(int rawAmount, Character source) {
        int finalAmount = processReactions(ReactionTrigger.ON_RECEIVE_HEAL, source, null, rawAmount);

        setHealth(this.health + finalAmount, source);

        if (finalAmount > 0) {
            LogManager.log(this.name + " recovers " + finalAmount + " HP.", LogFormat.HP);
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
        int finalAmount = processReactions(ReactionTrigger.ON_RECEIVE_MANA, source, null, rawAmount);
        gainMana(finalAmount);
    }

    public void setHealth(int newHealth, Character source) {
        if(newHealth > initialHealth){
            newHealth = initialHealth;
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
        processReactions(ReactionTrigger.ON_REVIVE, source, null, 0);

        if (source != null) {
            LogManager.log(this.name + " was revived by " + source.getName() + "!");
        } else {
            LogManager.log(this.name + " has been revived!");
        }

        onRevive();
    }

    public void setInitialHealth(int initialHealth){this.initialHealth = initialHealth;}
    public void setMaxMana(int maxMana){this.maxMana = maxMana;}

    public void addReaction(ReactionSkill reaction) {
        this.reactions.add(reaction);
    }

    protected int processReactions(ReactionTrigger trigger, Character source, Skill skill, int inputVal) {
        int currentValue = inputVal;

        if (trigger == ReactionTrigger.ON_RECEIVE_DAMAGE) {
            VisualEffectsManager.getInstance().flashDamage(this);
        }

        for (ReactionSkill reaction : reactions) {
            if (reaction.trigger() != trigger) continue;

            int result = reaction.logic().tryReact(this, source, skill, currentValue);

            if (result != -1) {

                switch (trigger) {
                    case ON_RECEIVE_DAMAGE:
                    case ON_RECEIVE_HEAL:
                    case ON_RECEIVE_MANA:
                        currentValue = result;
                        // If 0 then optionally stop processing further reactions
                        if (currentValue <= 0) {
                            return 0;
                        }
                        break;
                    case ON_FATAL_DAMAGE:
                        return 1; // 1 = True (Saved)

                    case ON_REVIVE:
                        // side effects only
                        break;
                }
            }
        }

        // Default returns if loops finish without short-circuiting
        if (trigger == ReactionTrigger.ON_FATAL_DAMAGE) return 0;

        return currentValue;
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    public int getInitialHealth() {
        return initialHealth;
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
    public boolean isExhausted() {
        return isExhausted;
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
