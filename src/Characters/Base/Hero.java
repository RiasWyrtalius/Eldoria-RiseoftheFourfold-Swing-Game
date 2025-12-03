package Characters.Base;

import Abilities.JobClass;
import Abilities.ReactionSkill;
import Abilities.Skill;
import Characters.Character;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;

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
        this.job = job;
        this.XP = 0;
        this.baseXP = 100;
        this.incrementXP = 50;
        // lvl 1 needs 100, level 2 needs 150, level 3 needs 200. etc......
        this.requiredXP = baseXP + (incrementXP * (this.level - 1));

        reactions = job.createReactions();
    }

    public Hero(String name, int health, int baseAtk, int maxMana, JobClass job, String imageKey) {
        this(name, health, baseAtk, maxMana, 1, job, imageKey);
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
        this.level++;
        this.requiredXP = baseXP + (incrementXP * (level - 1));
        int prev_mana = getMaxMana();

        int new_health = (int)(initialHealth + ((this.level) * 4) + (initialHealth * 0.05 * (this.level - 1)));

        // increase logs
        LogManager.log(this.name + " has leveled up to level " + this.level + "!");
        LogManager.log(this.name + " increased their health from " + getInitialHealth() + " to " + new_health + "!");
        super.setInitialHealth(new_health);
        //if it stayed the same its redundant ignore
        int new_mana = (int)(maxMana + ((this.level) * 2) + (maxMana * 0.009 * (this.level - 1)));
        if(prev_mana == new_mana){
            return;
        }
        LogManager.log(this.name + " increased their Mana from " + getMaxMana() + " to " + new_mana + "!");
        super.setMaxMana(new_mana);
    }



    public String regenerateTurnResources() {
        if (!isAlive) { return null; }

        String logMsg = "";

        if (getHealth() < getInitialHealth()) {
            
            int passiveHP = (int) (getInitialHealth() * 0.05);
            if (passiveHP < 1) passiveHP = 1;

            this.receiveHealing(passiveHP, null);
        }

        if (getMana() < getMaxMana()) {
            int passiveMana = (int) (getMaxMana() * 0.03); // 3% max MP

            this.receiveMana(passiveMana, null);
        }

        return logMsg;
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
}
