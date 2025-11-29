package Characters;

import Abilities.ReactionSkill;
import Abilities.ReactionTrigger;
import Abilities.Skill;
import Core.Utils.Dice;
import Core.Utils.LogColor;
import Core.Utils.LogManager;

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

    protected String imageKey;

    public Character(String name, int initialHealth, int baseAtk, int maxMana, int level, String imageKey) {
        this.name = name;
        this.initialHealth = initialHealth;
        this.health = initialHealth;
        this.baseAtk = baseAtk;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.level = level;
        this.imageKey = imageKey;
    }

    public Character(String name, int health, int baseAtk, int maxMana, String imageKey) {
        this(name, health, baseAtk, maxMana, 1, imageKey);
    }

    public void takeDamage(int rawDamage, Character attacker, Skill incomingSkill) {
        int finalDamage = processDamageReactions(attacker, incomingSkill, rawDamage);

        setHealth(this.health - finalDamage);

        if (finalDamage == 0) {
            LogManager.log(this.name + " took no damage (Mitigated)!");
        } else {
            LogManager.log(this.name + " takes " + finalDamage + " damage.");
        }

        // PHASE 2: Check for Death or Revival
        if (this.health <= 0) {
            // Before we call die(), check if a reaction saves us!
            boolean wasSaved = processFatalReactions(attacker, incomingSkill);

            // If NOT saved, then proceed to die
            if (!wasSaved) {
                die();
                if (attacker != null) {
                    onDefeat(attacker);
                }
            }
        }
    }

    public final void die() {
        this.health = 0;
        this.isAlive = false;
        setMana(0);
        onDeath();
    }

    // Subclass Hooks
    protected abstract void onDeath();

    protected void onDefeat(Character attacker) {
        LogManager.log(this.name + " is killed by " + attacker.getName() + "!", LogColor.ENEMY_ACTION);
    };

    public boolean canCast(int manaCost) {
        return this.mana >= manaCost;
    }

    public void gainMana(int amount) {
        setMana(this.mana + amount);
    }

    public boolean spendMana(int manaCost) {
        // TODO: Front end logging for spending mana here or within in the
        //      subclass method called
        if (canCast(manaCost)) {
            setMana(this.maxMana - manaCost);
            return true;
        } else {
            return false;
        }
    }

    public void attack(Character target) {
        LogManager.log("(CHARACTER) : " + this.name + " attacks " + target.getName());
    }

    public void setHealth(int newHealth) {
        if(newHealth > initialHealth){
            newHealth = initialHealth;
        }
        if (newHealth < 0) {
            newHealth = 0;
        }
        this.health = newHealth;
        if (this.health == 0 && this.isAlive) {
            die();
        }
        else if (this.health > 0 && !this.isAlive) {
            reviveState();
        }
    }

    private void reviveState() {
        this.isAlive = true;
        LogManager.log(this.name + " has been revived!");
    }

    public void setInitialHealth(int initialHealth){this.initialHealth = initialHealth;}
    public void setMaxMana(int maxMana){this.maxMana = maxMana;}

    public void addReaction(ReactionSkill reaction) {
        this.reactions.add(reaction);
    }

    protected int processDamageReactions(Character attacker, Skill incomingSkill, int incomingDamage) {
        int currentDamage = incomingDamage;

        for (ReactionSkill reaction : reactions) {
            if (reaction.trigger() == ReactionTrigger.ON_RECIEVE_DAMAGE) {
                int result = reaction.logic().tryReact(this, attacker, incomingSkill, incomingDamage);
                if (result != -1) {
                    currentDamage = result;
                    // TODO: what about other chain reactions
                    if (result == 0) break; // negates all damage
                }
            }
        }
        return currentDamage;
    }

    protected boolean processFatalReactions(Character attacker, Skill incomingSkill) {
        for (ReactionSkill reaction : reactions) {
            if (reaction.trigger() == ReactionTrigger.ON_FATAL_DAMAGE) {
                int result = reaction.logic().tryReact(this, attacker, incomingSkill, 0);
                if (result != -1) return true;
            }
        }
        return false;
    }

//    protected int processReactions(Character attacker, Skill incomingSkill, int incomingDamage) {
//        int currentDamage = incomingDamage;
//        // TODO: add process the most fit reaction but this will take a while, just do random
//        if (reactions.isEmpty()) {
//            return currentDamage;
//        }
//
//        ReactionSkill reaction = Dice.pickRandom(reactions);
//        int result = reaction.logic().tryReact(this, attacker, incomingSkill, incomingDamage);
//        if (result != -1) {
//            currentDamage = result;
//        }
//        return currentDamage;
//    }

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
    public String getImageKey() {
        return imageKey;
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
}
