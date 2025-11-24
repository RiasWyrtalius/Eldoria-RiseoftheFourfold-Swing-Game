package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;

public class Warrior extends JobClass {

    public Warrior() {
        super("Warrior", "Wields Sword and Shield", 10, 0);
    }

    public List<Skill> createSkills() {
        BiConsumer<Character, List<Character>> shieldBashLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                Character target = targets.getFirst();
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Bashes Shield on " + target.getName() + " for " + calculateDamage + " damage!", LogColor.HERO_ACTION);
            }
        };

        BiConsumer<Character, List<Character>> swordSlashLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                Character target = targets.getFirst();
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Slashes on " + target.getName() + " for " + calculateDamage + " damage!", LogColor.HERO_ACTION);
            }
        };

        Skill ShieldBash = new Skill(
                "Shield Bash", "Single-target fire spell", 15, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                shieldBashLogic
        );

        Skill SwordSlash = new Skill(
                "Sword Slash", "Single-target fire spell", 20, 30,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                swordSlashLogic
        );
        return List.of(ShieldBash,SwordSlash);
    }
}