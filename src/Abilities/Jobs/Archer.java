package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;
import java.util.function.BiConsumer;


public class Archer extends JobClass {
    public Archer(){
        super("Archer","Attacks from a range with their Bow and Arrow",0,0 );

        AssetManager.getInstance().registerAnimation(
                "ARCHER_IDLE",
                "Assets/Animations/Archer/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_DODGE",
                "Assets/Animations/Archer/Effects/Dodge/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_SHOOT_ARROW",
                "Assets/Animations/Archer/Effects/Shoot_Arrow/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "ARCHER_SHOOT_ARROW-Rapid",
                "Assets/Animations/Archer/Effects/Shoot_Arrow/sprite_%d.png",
                5, 100, 100 , 70,
                AnimationLoopType.TWO_CYCLES
        );
    }
    public List<Skill> createSkills() {
        FullExecuteConsumer rapidFireLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(user.getBaseAtk() +(user.getLevel() * 1.10));
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, "Unleashes array of Arrows", targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("ARCHER_SHOOT_ARROW-Rapid", user, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        FullExecuteConsumer heavyArrowLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(user.getBaseAtk() +(user.getLevel() * 1.6));
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, "Pulls their bow the hardest they can to release", targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("ARCHER_SHOOT_ARROW", user, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        Skill RapidFire = new Skill(
                "Rapid Fire", "Single-target long ranged attack ", 10, 15,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                rapidFireLogic
        );

        Skill HeavyArrow = new Skill(
                "Heavy Arrow", "Single-target heavy long ranged attack", 30, 40,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                heavyArrowLogic
        );

        return List.of(RapidFire,HeavyArrow);
    }

}
