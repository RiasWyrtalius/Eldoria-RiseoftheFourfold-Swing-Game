package Characters.Base;

import Abilities.JobClass;
import Abilities.Skill;
import Characters.Character;
import Core.LogColor;
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
        super(name, initialHealth + job.getHpBonus(), baseAtk, maxMana, level, imageKey);
        this.XP = 0;
        this.baseXP = 100;
        this.incrementXP = 50;
        this.requiredXP = XP + (incrementXP * (this.level - 1));
        this.job = job;
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

    public String regenerateTurnResources() {
        if (!isAlive) { return null; }

        StringBuilder logMsg = new StringBuilder();

        if (getHealth() < getInitialHealth()) {
            
            int passiveHP = (int) (getInitialHealth() * 0.05);
            if (passiveHP < 1) passiveHP = 1;

            int newHP = getHealth() + passiveHP;

            if (newHP > getInitialHealth()) {
                newHP = getInitialHealth();
            }

            setHealth(newHP);
            logMsg.append(this.name).append(" has healed ").append(passiveHP).append(" HP.\n");
        }

        if (getMana() < getMaxMana()) {
            int passiveMana = (int) (getMaxMana() * 0.03); // 3% max MP
            int newMana = getMana() + passiveMana;
             if (newMana > getMaxMana()) {
                 newMana = getMaxMana();
             }

             setMana(newMana);
             logMsg.append(this.name).append(" has recovered ").append(passiveMana).append(" mana.\n");
        }

        return logMsg.toString();
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

    public void useSkill(Skill skill, List<Character> targets, Runnable onSkillComplete) {
        LogManager.log("(HERO) : " + this.name + " is attempting to use " + skill.getName() + " on " + Skill.formatTargetList(targets), Color.GREEN);

        skill.execute(this, targets, onSkillComplete);
    }

    // =============== PUBLIC GETTERS FOR UI ===============

    public int getRequiredXP() {
        return requiredXP;
    }

    public JobClass getJob() {
        return job;
    }
}
