package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class DragonBoss extends Enemy {

    public DragonBoss(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(name, initialHealth * (int)healthMultiplier, baseAtk, maxMana, level, type, rewardXP, "DRAGON_BOSS-IDLE", "boss of the level");
        AssetManager.getInstance().registerAnimation(
                "DRAGON_BOSS-IDLE",
                "Assets/Animations/Enemies/Boss/Dragon/Idle/sprite_%d.png",
                3, 100, 100 , 320,
                AnimationLoopType.INFINITE
        );
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer basicAttackLogic = (self, user, targets, onSkillComplete) -> {
            Character weakTarget = null;
            int lowHP = Integer.MAX_VALUE;

            for (Character c : targets) {
                if (c.getHealth() > 0 && c.getHealth() < lowHP) {
                    lowHP = c.getHealth();
                    weakTarget = c;
                }
            }

            if (weakTarget == null && !targets.isEmpty()) {
                weakTarget = targets.getFirst();
            }

            if (weakTarget != null) {
                int calculateDamage = user.getBaseAtk();
                LogManager.log(self.getActionLog(user, "focuses on and strikes", List.of(weakTarget)), LogColor.ENEMY_ACTION);
                weakTarget.takeDamage(calculateDamage, user, self);
            }

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        Skill basicAttack = new Skill(
                "Cruel Strike", "Attacks the weakest target", 0, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                basicAttackLogic
        );

        SkillLogicConsumer devastatingStrikeLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int) (user.getBaseAtk() * 1.5);

            LogManager.log(self.getActionLog(user, "unleashes a DEVASTATING STRIKE on", targets), LogColor.ENEMY_ACTION);

            for (Character target : targets) {
                if (target.getHealth() > 0) {
                    target.takeDamage(calculateDamage, user, self);
                }
            }

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        Skill devastatingStrike = new Skill(
                "Devastating Strike", "Massive AOE damage", 50, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                devastatingStrikeLogic
        );

        skills.add(basicAttack);
        skills.add(devastatingStrike);
    }

    // TODO: character death animation
    @Override
    protected void onDeath() {
        super.onDeath();
//        VisualEffectsManager.getInstance().pauseAnimation("DRAGON_BOSS-IDLE");
//        VisualEffectsManager.getInstance().hideCharacterVisual(this);
    }

    @Override
    public void makeAttack(List<Character> targets, Runnable onSkillComplete) {
        Skill basicAttack = skills.get(0);
        Skill devastatingStrike = skills.get(1);

        if (this.getMana() >= devastatingStrike.getManaCost()) {
            devastatingStrike.execute(this, targets, onSkillComplete);
        } else {
            basicAttack.execute(this, targets, onSkillComplete);
        }
    }
}