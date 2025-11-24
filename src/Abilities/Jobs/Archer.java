package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;


public class Archer extends JobClass {
    public Archer(){super("Archer","Attacks from a range with their Bow and Arrow",0,0 );}
    public List<Skill> createSkills() {
        BiConsumer<Character, List<Character>> rapidFireLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 3);
                Character target = targets.getFirst();
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Unleashes array of Arrows at " + target.getName() + " for " + calculateDamage + " damage!");
            }
        };

        BiConsumer<Character, List<Character>> heavyArrowLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 4);
                Character target = targets.getFirst();
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Pulls their Bow the hardest they can to release at " + target.getName() + " for " + calculateDamage + " damage!");
            }
        };


        Skill RapidFire = new Skill(
                "Rapid Fire", "Single-target multi-hit spell", 10, 15,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                rapidFireLogic
        );

        Skill HeavyArrow = new Skill(
                "Heavy Arrow", "Single-target fire spell", 30, 40,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                heavyArrowLogic
        );

        return List.of(RapidFire,HeavyArrow);
    }
}
