package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.Skill;

import Abilities.SkillAction;
import Abilities.SkillType;
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

        Skill fireball = new Skill(
                "Fireball", "Single-target fire spell", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL,
                fireBallLogic
        );

        return List.of(fireball);
    }

}
