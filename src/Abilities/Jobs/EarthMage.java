package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;


public class EarthMage extends JobClass {
    public EarthMage(){super("Earth Mage","Wields stone and earth as its magic",0,0 );}
    public List<Skill> createSkills() {
        BiConsumer<Character, List<Character>> earthquakeSpellLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 4);
                for(Character t : targets) {
                    t.takeDamage(calculateDamage, user);
                }

                String targetNames = "";
                for (int i = 0; i < targets.size()-1; i++) {
                    targetNames += targets.get(i).getName();
                    if (i < targets.size() - 1) {
                        targetNames += ", ";
                    }
                }
                LogManager.log(user.getName() + " Shakes the earth under " + targetNames + " and " + targets.getLast().getName() + " for " + calculateDamage + " damage!", LogColor.HERO_ACTION);
            }
        };

        BiConsumer<Character, List<Character>> stoneHailLogic = new BiConsumer<Character, List<Character>>() {
            @Override
            public void accept(Character user, List<Character> targets) {
                // TODO: Damage calculations and logging
                int calculateDamage = (user.getBaseAtk() * 2);
                Character target = targets.getFirst();
                target.takeDamage(calculateDamage, user);
                LogManager.log(user.getName() + " Multitudes of earth crumbles down on " + target.getName() + " for " + calculateDamage + " damage!", LogColor.HERO_ACTION);
            }
        };


        Skill EarthquakeSpell = new Skill(
                "Earthquake Spell", "Multi-targeted Earth spell", 50, 55,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.AOE_ALL_TARGETS,
                earthquakeSpellLogic
        );

        Skill StoneHail = new Skill(
                "Stone Hail", "Single-target Earth spell", 15, 20,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                stoneHailLogic
        );

        return List.of(EarthquakeSpell,StoneHail);
    }
}
