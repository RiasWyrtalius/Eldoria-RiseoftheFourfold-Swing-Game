package Characters.Base;

import Characters.Character;

import java.util.List;

public abstract class Boss extends Enemy {
    protected double healthMultiplier;

    public Boss(String name, int baseHealth, int baseAtk, int maxMana, int level, String type, int rewardXP,
                double healthMultiplier, String imageKey, String desc) {
        super(
                name,
                (int)(baseHealth * healthMultiplier),
                baseAtk,
                maxMana,
                level,
                type,
                rewardXP,
                imageKey,
                desc
        );
        this.healthMultiplier = healthMultiplier;
    }

    @Override
    protected void registerAssets() {

    }

    @Override
    protected void initializeSkills() {

    }

    @Override
    public void makeAttack(List<Character> targets, Runnable onSkillComplete) {

    }
}
