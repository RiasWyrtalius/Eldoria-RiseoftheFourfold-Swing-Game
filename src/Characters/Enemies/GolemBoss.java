package Characters.Enemies;

import Abilities.*;
import Characters.Base.Boss;
import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;
import Core.Visuals.VisualEffectsManager;
import Resource.Animation.AnimationLoopType;
import Resource.Animation.AssetManager;

import java.util.List;

public class GolemBoss extends Boss {
    public GolemBoss(){this(1);}

    public GolemBoss(int level){
        this(
                "Golem",
                ScalingLogic.calculateStat(level,125,30,0.1),
                ScalingLogic.calculateStat(level,50,20,0.05),
                ScalingLogic.calculateStat(level,300,50,0.2),
                level,
                "Boss"
                ,ScalingLogic.calculateStat(level,250,30,0.1),
                (int)(2.5));
    }
    public GolemBoss(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(name, initialHealth, baseAtk, maxMana, level, type, rewardXP, healthMultiplier, "GOLEM_BOSS-IDLE", "boss of the level");
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "GOLEM_BOSS-IDLE",
                "Assets/Animations/Enemies/Boss/Golem/Idle/sprite_%d.png",
                3, 100, 100 , 320,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "GOLEM_BOSS-ATTACK",
                "Assets/Animations/Enemies/Boss/Golem/Effects/Attack/sprite_%d.png",
                4, 100, 100 , 320,
                AnimationLoopType.ONE_CYCLE
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
                LogManager.log(self.getActionLog(user, "focuses on and strikes", List.of(weakTarget)), LogFormat.ENEMY_ACTION);
                Character target = weakTarget;
                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("GOLEM_BOSS-ATTACK", target, () -> {
                target.receiveDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    }
                }, true);
            }

        };

        Skill basicAttack = new Skill(
                "Cruel Strike", "Attacks the weakest target", 0, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                basicAttackLogic
        );

        SkillLogicConsumer devastatingStrikeLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int) (user.getBaseAtk() * 1.5);

            LogManager.log(self.getActionLog(user, "unleashes a DEVASTATING STRIKE on", targets), LogFormat.ENEMY_ACTION);

            for (Character target : targets) {
                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("GOLEM_BOSS-ATTACK", target, () -> {
                    target.receiveDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    }
                }, true);
            }

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        Skill devastatingStrike = new Skill(
                "Golem Devastating Strike", "Massive AOE damage", 50, 0,
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
        VisualEffectsManager.getInstance().pauseAnimation("BOSS_IDLE");
//        VisualEffectsManager.getInstance().hideCharacterVisual(this);
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
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