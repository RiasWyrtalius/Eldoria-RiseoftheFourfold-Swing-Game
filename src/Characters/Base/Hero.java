package Characters.Base;

import Abilities.JobClass;
import Characters.Character;

public class Hero extends Character {
    protected int XP;
    protected int baseXP;
    protected int requiredXP;
    protected final int incrementXP;
    protected JobClass job;

    public Hero(String name, int initialHealth, int baseAtk, int maxMana, int level, JobClass job) {
        super(name, initialHealth, baseAtk, maxMana, level);
        this.XP = 0;
        this.baseXP = 100;
        this.incrementXP = 50;
        this.requiredXP = XP + (incrementXP * (this.level - 1));
        this.job = job;

        // applying stat modifiers
        this.health += job.getHpBonus();
        this.mana += job.getManaBonus();
    }

    public Hero(String name, int health, int baseAtk, int maxMana, JobClass job) {
        this(name, health, baseAtk, maxMana, 1, job);
    }


    public void gainXP(int amount) {
        XP += amount;

        while (XP >= requiredXP) {
            XP = XP - requiredXP;
            levelUp();
        }
    }

    protected void levelUp() {
        this.level++;
        this.requiredXP = baseXP + (incrementXP * (level - 1));
        //  TODO: Frontend logging for leveling up
    }

    // TODO: Add Logging for hero specific death logic
    @Override
    protected void onDeath() {
//        System.out.println("Hero " + this.name + " has fallen!");
    }

    @Override
    protected void onDefeat(Character defeatedTarget) {

    }


    // =============== PUBLIC GETTERS FOR UI ===============

    public int getRequiredXP() {
        return requiredXP;
    }
}
