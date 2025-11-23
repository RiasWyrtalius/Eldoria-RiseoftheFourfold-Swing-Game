package Characters.Base;

import Characters.Character;
import Core.LogManager;

import java.util.List;

// TODO: refine
public abstract class Enemy extends Character {
    private String type;
    private int rewardXP;

    public Enemy(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, String imageKey) {
        super(name, initialHealth, baseAtk, maxMana, level, imageKey);
        this.type = type;
        this.rewardXP = rewardXP;
    }

    public Enemy(String name, int health, int baseAtk, int maxMana, String type, int rewardXP, String imageKey) {
        this(name, health, baseAtk, maxMana, 1, type, rewardXP, imageKey);
    }

    public abstract void makeAttack(List<Character> targets);

    @Override
    protected void onDeath() {
        LogManager.log("(ENEMY) : " + this.name + " has died!");
    }

    @Override
    protected void onDefeat(Character finalAttacker) {
        if (finalAttacker instanceof Hero winner) {
            winner.gainXP(this.getRewardXP()); // Grant XP to the winner

            LogManager.log(winner.getName() + " delivered the final blow and gained " + this.getRewardXP() + " XP!");
            return;
        }
        LogManager.log("(ENEMY) :" + finalAttacker.getName() + " has slain " + "(ENEMY) : " + this.name + " DAMN!");
    }

    // =============== PUBLIC GETTERS FOR UI ===============

    public String getType() {
        return type;
    }
    public int getRewardXP() {
        return rewardXP;
    }
}
