package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;

public class Warrior extends JobClass {

    public Warrior() {
        super("Warrior", "Wields Sword and Shield", 10, 0);

    }

    public List<Skill> createSkills() {
        BiConsumer<Character, Character> shieldBashLogic = new BiConsumer<Character, Character>() {
            @Override
            public void accept(Character user, Character target) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Bashes Shield on " + target.getName() + " for " + calculateDamage + " damage!");
            }
        };

        BiConsumer<Character, Character> swordSlashLogic = new BiConsumer<Character, Character>() {
            @Override
            public void accept(Character user, Character target) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Slashes on " + target.getName() + " for " + calculateDamage + " damage!");
            }
        };

        Skill ShieldBash = new Skill(
                "Shield Bash", "Single-target fire spell", 15, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_ENEMY,
                shieldBashLogic
        );

        Skill SwordSlash = new Skill(
                "Sword Slash", "Single-target fire spell", 20, 30,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_ENEMY,
                swordSlashLogic
        );


        return List.of(ShieldBash,SwordSlash);
    }
}
