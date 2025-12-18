package Characters.Enemies;

import Abilities.*;
import Characters.Base.Boss;
import Characters.Character;
import Core.Battle.BattleController;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;
import Core.Visuals.VisualEffectsManager;
import Resource.Animation.AnimationLoopType;
import Resource.Animation.AssetManager;

import java.util.List;

public class DragonBoss extends Boss {
    public DragonBoss(){this(1);}

    //TODO: add defense field to Boss class
    public DragonBoss(int level){
        this(   // HP / ATK / DEF / MANA
                "Baby Dragon",
                ScalingLogic.calculateStat(level,300,35,0.1),
                ScalingLogic.calculateStat(level,15,2,0.05),
                ScalingLogic.calculateStat(level,15,2,0.05),
                ScalingLogic.calculateStat(level,300,50,0.2),
                level,
                "Boss"
                ,ScalingLogic.calculateStat(level,250,30,0.09),
                1.0);
    }
    public DragonBoss(String name, int initialHealth, int baseAtk, int baseDefense, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(
                name, initialHealth, baseAtk, baseDefense, maxMana, level, type, rewardXP,  healthMultiplier, "DRAGON_BOSS-IDLE", "boss of the level");
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "DRAGON_BOSS-IDLE",
                "Assets/Animations/Enemies/Boss/Dragon/Idle/sprite_%d.png",
                3, 100, 100 , 320,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "FIREBALL",
                "Assets/Animations/Heroes/Mage-Fire/Effects/FireBall/sprite_%d.png",
                6, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer basicAttackLogic = (controller,self, user, targets, onSkillComplete) -> {
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
                int calculateDamage = ScalingLogic.calculatePhysicalDamage(user,(int)(baseAtk * 1.5),0.4,0.1);
                LogManager.log(self.getActionLog(user, "focuses on and strikes", List.of(weakTarget)), LogFormat.ENEMY_ACTION);
                Character target = weakTarget;
                VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", target, () -> {
                    target.receiveDamage(calculateDamage, user, self, onSkillComplete);
                    if (onSkillComplete != null) onSkillComplete.run();
                }, true);
            }

        };

        Skill basicAttack = new Skill(
                "Dragon's Breath", "Attacks the weakest target", 0, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                basicAttackLogic
        );

        SkillLogicConsumer devastatingStrikeLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculatePhysicalDamage(user,(int)(baseAtk * 0.6),0.2,0.2);

            LogManager.log(self.getActionLog(user, "unleashes a DEVASTATING STRIKE on", targets), LogFormat.ENEMY_ACTION);

            for (Character target : targets) {
                if (target.getHealth() > 0) {
                    target.receiveDamage(calculateDamage, user, self, () -> {
                        if (onSkillComplete != null) onSkillComplete.run();
                    });
                }
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
    public List<Skill> getSkills() {
        return List.of();
    }

    @Override
    public void makeAttack(BattleController controller, List<Character> targets, Runnable onSkillComplete) {
        Skill basicAttack = skills.get(0);
        Skill devastatingStrike = skills.get(1);

        boolean useAOE = (this.getMana() >= devastatingStrike.getManaCost()) && (Math.random() < 0.4);

        if (useAOE) {
            devastatingStrike.execute(controller, this, targets, onSkillComplete);
        } else {
            basicAttack.execute(controller, this, targets, onSkillComplete);
        }
    }
}