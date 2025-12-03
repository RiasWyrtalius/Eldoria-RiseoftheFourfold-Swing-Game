package Characters.Base;

import Abilities.Skill;
import Characters.Character;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// TODO: refine
public abstract class Enemy extends Character {
    private String type;
    private int rewardXP;
    protected List<Skill> skills;
    protected String description;

    public Enemy(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, String imageKey, String description) {
        super(name, initialHealth, baseAtk, maxMana, level, imageKey);
        this.skills = new ArrayList<>();
        this.type = type;
        this.rewardXP = rewardXP;
        registerAssets();
        initializeSkills();
    }

    public Enemy(String name, int health, int baseAtk, int maxMana, String type, int rewardXP, String imageKey, String description) {
        this(name, health, baseAtk, maxMana, 1, type, rewardXP, imageKey, description);
    }

    protected abstract void registerAssets();

    protected abstract void initializeSkills();

    public abstract void makeAttack(List<Character> targets, Runnable onSkillComplete);

    @Override
    protected void onDeath() {
        super.onDeath();
        LogManager.log("(ENEMY) : " + this.name + " has died!", LogFormat.ENEMY_DEATH);
    }

    @Override
    protected void onDefeat(Character finalAttacker) {
        if (finalAttacker instanceof Hero winner) {
            LogManager.log(winner.getName() + " delivered the final blow to " + this.name + "!", Color.CYAN);
            winner.gainXP(this.getRewardXP()); // Grant XP to the winner
            return;
        }
        LogManager.log("(HERO) :" + finalAttacker.getName() + " has slain " + "(ENEMY) : " + this.name + "!");
    }

    // =============== PUBLIC GETTERS FOR UI ===============

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getType() {
        return type;
    }
    public int getRewardXP() {
        return rewardXP;
    }

}
