package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;


public class Archer extends JobClass {
    public Archer(){super("Archer","Attacks from a range with their Bow and Arrow",0,0 );}
    public List<Skill> createSkills() {
        FullExecuteConsumer rapidFireLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 3);
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user);
            LogManager.log(skill.getActionLog(user, "Unleashes array of Arrows at", targets, calculateDamage), LogColor.HERO_ACTION);
        };

        FullExecuteConsumer heavyArrowLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 4);
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user);
            LogManager.log(skill.getActionLog(user, " Pulls their bow the hardest they can to release at ", targets, calculateDamage), LogColor.HERO_ACTION);
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
