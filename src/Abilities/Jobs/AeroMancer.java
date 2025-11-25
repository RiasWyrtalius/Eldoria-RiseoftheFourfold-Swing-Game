package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;


public class AeroMancer extends JobClass {
    public AeroMancer(){super("Wind Mage","Wields atmosphere and wind as its magic",0,0 );}
    public List<Skill> createSkills() {

        FullExecuteConsumer windBurstLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (user.getBaseAtk() * 3);
            Character target = targets.getFirst();

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user, self);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };


        FullExecuteConsumer windPierceLogic= (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(user.getBaseAtk() * 2.5);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            for(Character t : targets) {
                t.takeDamage(calculateDamage, user, self);
            }

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };


        Skill WindBurst = new Skill(
                "Wind Burst", "Unleashes compressed wind", 20, 25,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_ALL_TARGETS,
                windBurstLogic
        );

        Skill WindPierce = new Skill(
                "Stone Hail", "Single-target Earth spell", 15, 20,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_TWO_TARGETS,
                windPierceLogic
        );

        return List.of(WindBurst,WindPierce);
    }
}
