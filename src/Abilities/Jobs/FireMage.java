package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.Utils.ScalingLogic;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;


public class FireMage extends JobClass {

    public FireMage() {
        super("Fire Mage", "Wields fire", 0, 0);

        AssetManager.getInstance().registerAnimation(
                "MAGE_IDLE",
                "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "FIREBALL",
                "Assets/Animations/Mage-Fire/Effects/FireBall/sprite_%d.png",
                6, 100,100, 100,
                AnimationLoopType.ONE_CYCLE
        );

        AssetManager.getInstance().registerAnimation(
                "FIRE_CYCLONE",
                "Assets/Animations/Mage-Fire/Effects/FireCyclone/sprite_%d.png",
                5, 100,100, 300,
                AnimationLoopType.TWO_CYCLES
        );
    }

    public List<Skill> createSkills() {

        // FIXME: turn doesn't end when animation is finished
        SkillLogicConsumer fireBallLogic = (self, user, targets, onSkillComplete) -> {

            int calculateDamage = ScalingLogic.calculateDamage(user,25,15,1.2,0.05);
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);

        };

        SkillLogicConsumer fireCycloneLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,50,30,1.2,0.05);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);
            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("FIRE_CYCLONE", t, () -> {

                        t.takeDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }


            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };
        SkillLogicConsumer staffAttackLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,10,0,1.2,0.05);
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);
            target.takeDamage(calculateDamage, user, self);
            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        Skill fireball = new Skill(
                "Fireball", "Single-target fire spell", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                fireBallLogic
        );

        Skill fireCyclone = new Skill(
                "Fire Cyclone", "Multi-target fire spell", 50, 55,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_ALL_TARGETS,
                fireCycloneLogic
        );

        Skill staffAttack= new Skill(
                "Staff Attack", "Single-target spell", 0, 10,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                staffAttackLogic
        );


        return List.of(staffAttack,fireball, fireCyclone);
    }

}