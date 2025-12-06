package Characters.Base;

import Abilities.JobClass;
import Abilities.ReactionSkill;
import Abilities.Skill;
import Characters.Character;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;

import java.awt.*;
import java.util.List;

public class Hero extends Character {
    protected int XP;
    protected int baseXP;
    protected int requiredXP;
    protected double growthRate;
    protected JobClass job;

    public Hero(String name, int baseHealth, int baseAtk, int maxMana, int xp, int level, JobClass job) {
        super(name, baseHealth + job.getHpBonus(), baseAtk, maxMana, level);
        this.job = job;

        this.XP = xp;

        this.baseXP = 100;
        this.growthRate = 1.15;
        this.requiredXP = (int)(baseXP * Math.pow(growthRate,this.level - 1));

        this.reactions.addAll(job.createReactions());
    }

    public Hero(String name, int baseHealth, int baseAtk, int maxMana, int level, JobClass job) {
        this(name, baseHealth, baseAtk, maxMana, 0, level, job);
    }

    public Hero(String name, int health, int baseAtk, int maxMana, JobClass job) {
        this(name, health, baseAtk, maxMana, 1, job);
    }

    private void recalculateStats() {
        this.maxHealth = ScalingLogic.calculateStat(
                this.level,
                this.baseMaxHealth + job.getHpBonus(),
                job.getHpFlat(),
                job.getHpGrowth()
        );

        this.maxMana = ScalingLogic.calculateStat(
                this.level,
                this.baseMaxMana + job.getManaBonus(),
                job.getMpFlat(),
                job.getMpGrowth()
        );
    }

    public void gainXP(int amount) {
        LogManager.log(getName() + " gains " + amount + "XP!", LogFormat.XP_GAIN);
        XP += amount;

        while (XP >= requiredXP) {
            XP = XP - requiredXP;
            levelUp();
        }
    }

    protected void levelUp() {
//        this.level++;
//        this.requiredXP = baseXP + (incrementXP * (level - 1));
//
//        int prev_mana = getMaxMana();
//        int new_health = (int)(maxHealth + ((this.level) * 4) + (maxHealth * 0.05 * (this.level - 1)));
//
//        // increase logs
//        LogManager.log(this.name + " has leveled up to level " + this.level + "!");
//        LogManager.log(this.name + " increased their health from " + getMaxHealth() + " to " + new_health + "!");
//        super.setInitialHealth(new_health);
//        //if it stayed the same its redundant ignore
//        int new_mana = (int)(maxMana + ((this.level) * 2) + (maxMana * 0.009 * (this.level - 1)));
//        if(prev_mana == new_mana){
//            return;
//        }
//        LogManager.log(this.name + " increased their Mana from " + getMaxMana() + " to " + new_mana + "!");
//        super.setMaxMana(new_mana);

        this.level++;
        this.requiredXP = (int)(baseXP * Math.pow(growthRate,this.level - 1));

        int oldMaxHp = this.maxHealth;
        int oldMaxMp = this.maxMana;

        recalculateStats();

        int hpGained = this.maxHealth - oldMaxHp;
        int mpGained = this.maxMana - oldMaxMp;

        this.receiveHealing(hpGained, null);
        this.receiveMana(mpGained, null);

        LogManager.log(this.name + " reached Level " + this.level + "!", LogFormat.HIGHLIGHT_BUFF);

        // Log gains only if they happened
        if (hpGained > 0) LogManager.log(this.name + " gained +" + hpGained + " Max Health!");
        if (mpGained > 0) LogManager.log(this.name + " gained +" + mpGained + " Max Mana!");
    }


    public void regenerateTurnResources() {
        if (!isAlive) { return; }

        String logMsg = "";

        if (getHealth() < getMaxHealth()) {
            
            int passiveHP = (int) (getMaxHealth() * 0.05);
            if (passiveHP < 1) passiveHP = 1;

            this.receiveHealing(passiveHP, null);
        }

        if (getMana() < getMaxMana()) {
            int passiveMana = (int) (getMaxMana() * 0.03); // 3% max MP

            this.receiveMana(passiveMana, null);
        }
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        LogManager.log("(HERO) : " + this.name + " has died!");
    }

    @Override
    public String getDescription() {
        return job.getDescription();
    }

    public void useSkill(Skill skill, List<Character> targets, Runnable onSkillComplete) {
        LogManager.log("(HERO) : " + this.name + " is attempting to use " + skill.getName() + " on " + Skill.formatTargetList(targets), Color.GREEN);

        skill.execute(this, targets, onSkillComplete);
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    @Override public List<Skill> getSkills() { return job.getSkills(); }
    public List<ReactionSkill> getReactions() { return reactions; }
    public int getXP() {
        return XP;
    }
    public int getRequiredXP() {
        return requiredXP;
    }
    public JobClass getJob() {
        return job;
    }
    @Override public String getIdleImageKey() { return this.job.getIdleImageKey(); }
}
