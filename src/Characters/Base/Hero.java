package Characters.Base;

import Abilities.JobClass;
import Abilities.Skill;
import Characters.Character;
import Core.LogManager;

import java.awt.*;
import java.util.List;

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
        LogManager.log(this.name + " has leveled up to level " + this.level + "!");
    }

    @Override
    protected void onDeath() {
        LogManager.log("(HERO) : " + this.name + " has died!");
    }

    @Override
    protected void onDefeat(Character finalAttacker) {
        LogManager.log("(ENEMY) : " + finalAttacker.getName(), Color.RED);
        LogManager.log(" has slain ", Color.BLACK);
        LogManager.log("(HERO) : " + this.name, Color.BLUE);
    }

    public void useSkill(Skill skill, List<Character> targets) {
        if (targets.size() == 1) {
            LogManager.log(this.name + " is attempting to use " + skill.getName() + " on "
                + targets.getFirst().getName() + ".");
        } else if (targets.size() > 1) {
            String targetNames = "";
            for (int i = 0; i < targets.size()-1; i++) {
                targetNames += targets.get(i).getName();
                if (i < targets.size() - 1) {
                    targetNames += ", ";
                }
            }
            LogManager.log("(HERO) : " + this.name + " is attempting to use " + skill.getName() + " on " +
                    targetNames + " and " + targets.getLast().getName(), Color.GREEN);
        }

        skill.execute(this, targets);
    }

    // =============== PUBLIC GETTERS FOR UI ===============

    public int getRequiredXP() {
        return requiredXP;
    }

    public JobClass getJob() {
        return job;
    }
}
