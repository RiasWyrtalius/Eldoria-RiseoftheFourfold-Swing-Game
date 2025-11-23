package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;

public class FireMage extends JobClass {

    public FireMage() {
        super("Fire Mage", "Wields fire", 0, 0);

    }

    public List<Skill> createSkills() {
        BiConsumer<Character, Character> fireBallLogic = new BiConsumer<Character, Character>() {
            @Override
            public void accept(Character user, Character target) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " casts Fireball on " + target.getName() + " for " + calculateDamage + " damage!");
            }
        };

        BiConsumer<Character, List<Character>> fireCycloneLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                for(Character t : targets) {
                    t.takeDamage(calculateDamage, user);
                }

                String targetNames = "";
                for (int i = 0; i < targets.size(); i++) {
                    targetNames += targets.get(i).getName();
                    if (i < targets.size() - 1) {
                        targetNames += ", ";
                    }
                }
                LogManager.log(user.getName() + " casts Fire Cyclone on " + targetNames + " for " + calculateDamage + " each!");
            }
        };

        Skill fireball = new Skill(
                "Fireball", "Single-target fire spell", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_ENEMY,
                fireBallLogic
        );

        Skill fireCyclone = new Skill(
                "Fire Cyclone", "Multi-target fire spell", 50, 55,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_ALL_ENEMIES,
                fireCycloneLogic
        );

        return List.of(fireball,fireCyclone);
    }

}
