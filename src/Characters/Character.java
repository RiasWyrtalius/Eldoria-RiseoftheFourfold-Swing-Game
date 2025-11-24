package Characters;

import Core.LogManager;

public abstract class Character {
    protected String name;

    protected final int initialHealth;
    protected int health;
    protected boolean isAlive = true;

    protected int level;
    protected int baseAtk;

    protected int mana;
    protected int maxMana;

    protected boolean isExhausted = false;

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

    public void takeDamage(int damage, Character attacker) {
        this.health -= damage;
        LogManager.log(this.name + " took " + damage + " damage from " + attacker.getName());
        if (this.health <= 0) {
            onDefeat(attacker);
            die();
        }
    }

    public final void die() {
        this.health = 0;
        this.isAlive = false;
        onDeath();
    }

    // Subclass Hooks
    protected abstract void onDeath();

    protected abstract void onDefeat(Character finalAttacker);

    public boolean canCast(int manaCost) {
        return this.mana >= manaCost;
    }

    public boolean spendMana(int manaCost) {
        // TODO: Front end logging for spending mana here or within in the
        //      subclass method called
        if (canCast(manaCost)) {
            this.mana -= manaCost;
            return true;
        } else {
            return false;
        }
    }

    public void attack(Character target) {
        LogManager.log("(CHARACTER) : " + this.name + " attacks " + target.getName());
    }

    //added setter for boss multiplier
    public void setHealth(int health) { this.health = health; }

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
}
