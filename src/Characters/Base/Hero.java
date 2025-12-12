package Characters.Base;

import Abilities.JobClass;
import Abilities.ReactionSkill;
import Abilities.Skill;
import Characters.Character;
import Core.Battle.BattleController;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;
import Core.Visuals.VisualEffectsManager;

import java.awt.*;
import java.util.List;

public class Hero extends Character {
    protected int XP;
    protected int baseXP;
    protected int requiredXP;
    protected double growthRate;
    protected JobClass job;

    public Hero(String name, int baseHealth, int baseAtk, int baseMana, int xp, int level, JobClass job) {
        super(name, baseHealth, baseAtk, baseMana, level);
        this.job = job;

        this.XP = xp;

        this.baseXP = 100;

        // is this a constant?
        this.growthRate = 1.15;
        this.requiredXP = (int)(baseXP * Math.pow(growthRate,this.level - 1));

        recalculateStats();

        // necessary for new characters
        this.health = maxHealth;
        this.mana = maxMana;

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
                this.baseMaxMana + job.getMpBonus(),
                job.getMpFlat(),
                job.getMpGrowth()
        );
    }

    public void gainXP(int amount) {
        LogManager.log("(EXP) : " + getName() + " gains " + amount + " xp!", LogFormat.XP_GAIN);
        XP += amount;

        while (XP >= requiredXP) {
            XP = XP - requiredXP;
            levelUp();
        }
    }

    protected void levelUp() {
        int prevHealth = this.maxHealth;
        int prevMana = this.maxMana;

        this.level++;
        this.requiredXP = (int)(baseXP * Math.pow(growthRate,this.level - 1));

        VisualEffectsManager.getInstance().showFloatingText(this, "LEVEL " + this.level, LogFormat.HIGHLIGHT_BUFF);
        LogManager.log(this.name + " has leveled up to level " + this.level + "!", LogFormat.HIGHLIGHT_LEVELUP);

        recalculateStats();

        boolean isMilestone = isReviveMilestone(this.level);

        if (!this.isAlive && isMilestone) {
            this.isAlive = true;
            this.health = this.maxHealth / 2;
            this.mana = this.maxMana;

            LogManager.log(this.name + " has been REVIVED by reaching level " + this.level + "!", LogFormat.HIGHLIGHT_BUFF);
            VisualEffectsManager.getInstance().showFloatingText(this, "REVIVED!", Color.CYAN);
        } else if (this.isAlive) {
            int hpGained = this.maxHealth - prevHealth;
            int mpGained = this.maxMana - prevMana;

            if (hpGained > 0) {
                LogManager.log(this.name + " max health increased from " + prevHealth + " to " + this.maxHealth + "!", LogFormat.HP);
                this.receiveHealing(hpGained, null);
            }
            if (mpGained > 0) {
                LogManager.log(this.name + " max mana increased from " + prevMana + " to " + this.maxMana + "!", LogFormat.MP);
                this.receiveMana(mpGained, null);
            }
        } else { // dead & not milestone
            LogManager.log(this.name + " reached Level " + this.level + "!", LogFormat.XP_GAIN);
        }
    }

    public void reviveFromMapMilestone() {
        if (!this.isAlive) { // kung patay nya recover ra 50% stats
            this.isAlive = true;
            this.health = this.maxHealth / 2;
            this.mana = this.maxMana / 2;

            LogManager.log(this.name + " has been REVIVED by reaching a Milestone Floor!", LogFormat.HIGHLIGHT_BUFF);
            VisualEffectsManager.getInstance().showFloatingText(this, "REVIVED!", Color.CYAN);
        } else {
            int hpBonus = (int) (this.maxHealth * 0.20);
            int mpBonus = (int) (this.maxMana * 0.20);

            if (hpBonus < 1) hpBonus = 1;
            if (mpBonus < 1) mpBonus = 1;

            this.receiveHealing(hpBonus, null);
            this.receiveMana(mpBonus, null);

            LogManager.log(this.name + " rests at the milestone and recovers resources.", LogFormat.HIGHLIGHT_BUFF);
            VisualEffectsManager.getInstance().showFloatingText(this, "+25%!", Color.CYAN);
        }
    }

    private boolean isReviveMilestone(int level) { return level % 10 == 0; }

    public void regenerateTurnResources() {
        if (!isAlive) { return; }

        if (getHealth() < getMaxHealth()) {
            int passiveHP = (int) (getMaxHealth() * 0.07);
            if (passiveHP < 1) passiveHP = 1;

            this.receiveHealing(passiveHP, null);
        }

        if (getMana() < getMaxMana()) {
            int passiveMana = (int) (getMaxMana() * 0.04);
            if (passiveMana < 5) passiveMana = 5; //incase it's lower.
            this.receiveMana(passiveMana, null);
        }
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        LogManager.log("(HERO) : " + this.name + " has died!", LogFormat.DEAD);
    }


    public void useSkill(BattleController controller, Skill skill, List<Character> targets, Runnable onSkillComplete) {
        LogManager.log("(HERO) : " + this.name + " is attempting to use " + skill.getName() + " on " + Skill.formatTargetList(targets), LogFormat.HIGHLIGHT_PLAYER_TURN);

        skill.execute(controller,this, targets, onSkillComplete);
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    @Override public String getDescription() { return job.getDescription();}
    @Override public List<Skill> getSkills() { return job.getSkills(); }
    public List<ReactionSkill> getReactions() { return reactions; }
    public int getXP() { return XP; }
    public int getRequiredXP() { return requiredXP; }
    public JobClass getJob() { return job; }
    @Override public String getIdleImageKey() { return this.job.getIdleImageKey(); }
}
