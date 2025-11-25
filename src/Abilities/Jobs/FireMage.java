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
                "Assets/Animations/Mage/Idle/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "FIREBALL",
                "Assets/Animations/Effects/FireBall/sprite_%d.png",
                6, 100,100, 100,
                AnimationLoopType.ONE_CYCLE
        );
    }

    public List<Skill> createSkills() {

        // FIXME: turn doesn't end when animation is finished
        FullExecuteConsumer fireBallLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 2);
            Character target = targets.getFirst();
            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", target, () -> {
                target.takeDamage(calculateDamage, user);
            }, true);
        };

        FullExecuteConsumer fireCycloneLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 3);

            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            for (Character t : targets) {
                t.takeDamage(calculateDamage, user);
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