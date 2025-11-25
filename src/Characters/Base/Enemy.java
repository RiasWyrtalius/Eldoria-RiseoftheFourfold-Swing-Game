package Characters.Base;

import Abilities.Skill;
import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// TODO: refine
public abstract class Enemy extends Character {
    private String type;
    private int rewardXP;
    protected List<Skill> skills;

    public Enemy(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, String imageKey) {
        super(name, initialHealth, baseAtk, maxMana, level, imageKey);
        this.skills = new ArrayList<>();
        this.type = type;
        this.rewardXP = rewardXP;
        initializeSkills();
    }

    public Enemy(String name, int health, int baseAtk, int maxMana, String type, int rewardXP, String imageKey) {
        this(name, health, baseAtk, maxMana, 1, type, rewardXP, imageKey);
    }

    protected abstract void initializeSkills();

    public abstract void makeAttack(List<Character> targets, Runnable onSkillComplete);

    @Override
    protected void onDeath() {
        LogManager.log("(ENEMY) : " + this.name + " has died!", LogColor.ENEMY_DEATH);
    }

    @Override
    protected void onDefeat(Character finalAttacker) {
        if (finalAttacker instanceof Hero winner) {
            LogManager.log(winner.getName() + " delivered the final blow to " + this.name + " and gained " + this.getRewardXP() + " XP!", Color.CYAN);
            winner.gainXP(this.getRewardXP()); // Grant XP to the winner
            return;
        }
        LogManager.log("(HERO) :" + finalAttacker.getName() + " has slain " + "(ENEMY) : " + this.name + " DAMN!");
    }

    // =============== PUBLIC GETTERS FOR UI ===============

    public String getType() {
        return type;
    }
    public int getRewardXP() {
        return rewardXP;
    }

    public List<Skill> getSkills() {
        return skills;
    }
}
