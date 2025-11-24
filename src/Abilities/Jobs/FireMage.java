package Abilities.Jobs;

import Abilities.*;

import Characters.Base.Hero;
import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;


public class FireMage extends JobClass {

    public FireMage() {
        super("Fire Mage", "Wields fire", 0, 0);

    }

    public List<Skill> createSkills() {

        FullExecuteConsumer fireBallLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 2);
            Character target = targets.getFirst();

            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user);
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
