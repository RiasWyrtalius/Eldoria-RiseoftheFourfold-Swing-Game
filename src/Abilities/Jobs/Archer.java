package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
import Core.Utils.ScalingLogic;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Animation.AnimationLoopType;
import Resource.Animation.AssetManager;

import java.util.List;


public class Archer extends JobClass {

    private static final String description = "Agile and precise, the Archer strikes from afar with deadly accuracy. Her keen eyesight and swift reflexes allow her to rain arrows upon foes before they can close the distance. She embodies speed, cunning, and tactical finesse.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Archer/Idle/sprite_%d.png";

    public Archer(){
        super("Archer", description, "ARCHER_IDLE",0,30);
    }

    @Override
    public void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "ARCHER_IDLE",
                "Assets/Animations/Heroes/Archer/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_DODGE",
                "Assets/Animations/Heroes/Archer/Effects/Dodge/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_SHOOT_ARROW",
                "Assets/Animations/Heroes/Archer/Effects/Shoot_Arrow/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_SHOOT_ARROW-Rapid",
                "Assets/Animations/Heroes/Archer/Effects/Shoot_Arrow/sprite_%d.png",
                5, 100, 100 , 70,
                AnimationLoopType.TWO_CYCLES
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_DEATH",
                "Assets/Animations/Heroes/Archer/Death/sprite_%d.png",
                1, 100, 100 , 500,
                AnimationLoopType.INFINITE
        );
    }

    // dodges if less than 60 percent of health
    // TODO: account for animation completion
    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic dodgeLogic = (defender, _, _, incomingDamage, onComplete) -> {
            double hp_percent = (double)defender.getHealth() / defender.getMaxHealth();
            if (hp_percent < 0.60 && Dice.getInstance().chance(0.3)) {
                LogManager.log(defender.getName() + " swiftly dodges the attack!", LogFormat.ENEMY_ACTION);
                VisualEffectsManager.getInstance().playAnimation("ARCHER_DODGE", defender, () -> {
                    // Pass 0 damage to the next step in the reaction chain
                    onComplete.accept(0);
                }, true);
            } else {
                onComplete.accept(incomingDamage);
            }
        };

        ReactionSkill dodge = new ReactionSkill("Dodge", ReactionTrigger.ON_RECEIVE_DAMAGE, dodgeLogic);

        return List.of(dodge);

    }

    @Override
    public List<Skill> createSkills() {
        SkillLogicConsumer rapidFireLogic = (_, self, user, targets, onSkillComplete) -> {
            int dmg = ScalingLogic.calculatePhysicalDamage(user, 20, 2.0, 0.03);
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, "Unleashes array of Arrows", targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("ARCHER_SHOOT_ARROW-Rapid", user, () -> {
                target.receiveDamage(dmg, user, self, onSkillComplete);
            }, true);
        };

        SkillLogicConsumer heavyArrowLogic = (_, self, user, targets, onSkillComplete) -> {
            int dmg = ScalingLogic.calculatePhysicalDamage(user, 35, 3.0, 0.05);
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, "Pulls their bow the hardest they can to release", targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("ARCHER_SHOOT_ARROW", user, () -> {
                target.receiveDamage(dmg, user, self, onSkillComplete);
                target.applyStatusEffect(StatusEffectFactory.stun(2));
            }, true);
        };

        Skill RapidFire = new Skill(
                "Rapid Fire", "Single-target long ranged attack ", 10, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                rapidFireLogic
        );

        Skill HeavyArrow = new Skill(
                "Heavy Arrow", "Single-target heavy long ranged attack", 30, 40,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                heavyArrowLogic
        );

        return List.of(RapidFire,HeavyArrow);
    }
    //TEMPORARY
    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}
