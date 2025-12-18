package Characters.Base;

import Characters.Character;
import Core.Battle.BattleController;

import java.util.List;

public abstract class Boss extends Enemy {
    protected double healthMultiplier;

    public Boss(String name, int baseHealth, int baseAtk, int baseDefense, int maxMana, int level, String type, int rewardXP,
                double healthMultiplier, String idleImageKey, String desc) {
        super(
                name,
                (int)(baseHealth * healthMultiplier),
                baseAtk,
                baseDefense,
                maxMana,
                level,
                type,
                rewardXP,
                desc,
                idleImageKey
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
    public void makeAttack(BattleController controller, List<Character> targets, Runnable onSkillComplete) {
    }
}
