package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;


public class CryoMancer extends JobClass {
    public CryoMancer(){super("Ice Mage","Wields snow and ice as its magic",0,0 );}
    public List<Skill> createSkills() {

        FullExecuteConsumer iceSpikeLogic = (skill, user, targets) -> {
            int calculateDamage = (user.getBaseAtk() * 3);
            Character target = targets.getFirst();

            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user);
        };


        FullExecuteConsumer frostBiteLogic = (skill, user, targets) -> {
            int calculateDamage = (int)(user.getBaseAtk() * 4);
            Character target = targets.getFirst();

            LogManager.log(skill.getActionLog(user, skill.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user);
        };


        Skill IceSpike = new Skill(
                "Ice Spike", "Unleashes spikes from the ground", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                iceSpikeLogic
        );

        Skill FrostBite = new Skill(
                "Frost Bite", "Unleashes cold Air to enemy", 10, 15,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                frostBiteLogic
        );

        return List.of(IceSpike,FrostBite);
    }
}


