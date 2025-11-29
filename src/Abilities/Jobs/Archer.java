package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
import Core.Utils.ScalingLogic;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualAsset;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;


public class Archer extends JobClass {

    private static final String description = "Agile and precise, the Archer strikes from afar with deadly accuracy. Her keen eyesight and swift reflexes allow her to rain arrows upon foes before they can close the distance. She embodies speed, cunning, and tactical finesse.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Archer/Idle/sprite_%d.png";

    public Archer(){
        super("Archer", description, 0, 0 );

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
    }

    // dodges if less than 80 percent of health
    // TODO: account for animation completion
    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic dodgeLogic = (defender, attacker, incomingSkill, incomingDamage) -> {
            double hp_percent = (double)defender.getHealth() / defender.getInitialHealth();
            if (Dice.chance(0.5) && hp_percent < 0.60) {
                LogManager.log(defender.getName() + " swiftly dodges the attack!", LogColor.ENEMY_ACTION);
                VisualEffectsManager.getInstance().playAnimation("ARCHER_DODGE", defender, null, true);
                return 0;
            }
            return -1;
        };

        ReactionSkill dodge = new ReactionSkill("Dodge", ReactionTrigger.ON_RECIEVE_DAMAGE, dodgeLogic);

        return List.of(dodge);
    }

    public List<Skill> createSkills() {
        SkillLogicConsumer rapidFireLogic = (self, user, targets, onSkillComplete) -> {
            int dmg = ScalingLogic.calculateDamage(user, 20, 15, 1.2, 0.05);
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, "Unleashes array of Arrows", targets), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("ARCHER_SHOOT_ARROW-Rapid", user, () -> {
                target.takeDamage(dmg, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        SkillLogicConsumer heavyArrowLogic = (self, user, targets, onSkillComplete) -> {
            int dmg = ScalingLogic.calculateDamage(user, 40, 20, 1.2, 0.05);
            Character target = targets.get(0);
            LogManager.log(self.getActionLog(user, "Pulls their bow the hardest they can to release", targets), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("ARCHER_SHOOT_ARROW", user, () -> {
                target.takeDamage(dmg, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
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
