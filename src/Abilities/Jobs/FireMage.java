package Abilities.Jobs;

import Abilities.*;

import Characters.Base.Hero;
import Characters.Character;
import Core.LogColor;
import Core.LogManager;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;
import java.util.function.BiConsumer;


public class FireMage extends JobClass {

    public FireMage() {
        super("Fire Mage", "Wields fire", 0, 0);

        AssetManager.getInstance().registerAnimation(
                "MAGE_IDLE",
                "Assets/Animations/Mage-Fire/Idle/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "FIREBALL",
                "Assets/Animations/Mage-Fire/Effects/FireBall/sprite_%d.png",
                6, 100,100, 100,
                AnimationLoopType.ONE_CYCLE
        );
    }

    public List<Skill> createSkills() {

        // FIXME: turn doesn't end when animation is finished
        FullExecuteConsumer fireBallLogic = (self, user, targets, onSkillComplete) -> {

            int calculateDamage = (int)(25 + 15 + (user.getLevel() * 1.2) + (25 * (user.getLevel() * 0.05)));
            Character target = targets.getFirst();
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);

        };

        FullExecuteConsumer fireCycloneLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(50 + 30 + (user.getLevel() * 1.2) + (50 * (user.getLevel() * 0.05)));
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            for (Character t : targets) {
                t.takeDamage(calculateDamage, user, self);
            }

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


        return List.of(fireball, fireCyclone);
    }

}