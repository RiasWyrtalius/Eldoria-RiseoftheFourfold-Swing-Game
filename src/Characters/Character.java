package Characters;

public abstract class Character {
    protected String name;

    protected final int initialHealth;
    protected int health;
    protected boolean isAlive = true;

    protected int level;
    protected int baseAtk;

    protected int mana;
    protected int maxMana;

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

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0)
            die();
        // TODO: Frontend logging for damage taken
    }

    public final void die() {
        this.health = 0;
        this.isAlive = false;
        // TODO: Frontend for death
        onDeath();
    }

    // Subclass Hooks
    protected abstract void onDeath();
    protected abstract void onDefeat(Character defeatedTarget);

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
}
