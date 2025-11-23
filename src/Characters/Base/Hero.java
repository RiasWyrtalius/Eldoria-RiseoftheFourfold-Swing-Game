package Characters.Base;

import Abilities.JobClass;
import Characters.Character;
import Core.LogManager;

public class Hero extends Character {
    protected int XP;
    protected int baseXP;
    protected int requiredXP;
    protected final int incrementXP;
    protected JobClass job;

    public Hero(String name, int initialHealth, int baseAtk, int maxMana, int level, JobClass job, String imageKey) {
        super(name, initialHealth, baseAtk, maxMana, level, imageKey);
        this.XP = 0;
        this.baseXP = 100;
        this.incrementXP = 50;
        this.requiredXP = XP + (incrementXP * (this.level - 1));
        this.job = job;

        // applying stat modifiers
        this.health += job.getHpBonus();
        this.mana += job.getManaBonus();
    }

    public Hero(String name, int health, int baseAtk, int maxMana, JobClass job, String imageKey) {
        this(name, health, baseAtk, maxMana, 1, job, imageKey);
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


    public static void attack(Character target) {
        //TODO: attack target
        System.out.println("hero attacks target"); //temporary
    }

    // TODO: Add Logging for hero specific death logic
    @Override
    protected void onDeath() {
        LogManager.log("(HERO) : " + this.name + " has died!");
    }

    @Override
    protected void onDefeat(Character defeatedTarget) {
        LogManager.log("(HERO) : " + this.name + " has slain (ENEMY) : " + defeatedTarget.getName());
    }


    // =============== PUBLIC GETTERS FOR UI ===============

    public int getRequiredXP() {
        return requiredXP;
    }
}
