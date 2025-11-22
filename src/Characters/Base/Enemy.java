package Characters.Base;

import Characters.Character;
import java.util.List;

// TODO: refine
public abstract class Enemy extends Character {
    private String type;
    private int rewardXP;

    public Enemy(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP) {
        super(name, initialHealth, baseAtk, maxMana, level);
        this.type = type;
        this.rewardXP = rewardXP;
    }

    public Enemy(String name, int health, int baseAtk, int maxMana, String type, int rewardXP) {
        this(name, health, baseAtk, maxMana, 1, type, rewardXP);
    }

    public abstract void makeAttack(List<Character> targets);

    @Override
    protected void onDeath() {

    }

    @Override
    protected void onDefeat(Character defeatedTarget) {

    }

    // =============== PUBLIC GETTERS FOR UI ===============

    public String getType() {
        return type;
    }

    public int getRewardXP() {
        return rewardXP;
    }
}
