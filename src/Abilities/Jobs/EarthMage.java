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


        FullExecuteConsumer earthquakeSpellLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 3);
            Character target = targets.getFirst();

            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            for (Character t : targets) {
                t.takeDamage(calculateDamage, user);
            }
        };


        FullExecuteConsumer stoneHailLogic= (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 2);
            Character target = targets.getFirst();

            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user);
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
