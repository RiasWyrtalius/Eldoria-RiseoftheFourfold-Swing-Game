package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.Utils.CombatMath;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;


public class AeroMancer extends JobClass {
    public AeroMancer(){
        super("Wind Mage","Wields atmosphere and wind as its magic",0,0 );
        AssetManager.getInstance().registerAnimation(
                "MAGE_IDLE",
                "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "WIND_TORNADO",
                "Assets/Animations/Heroes/Mage-Wind/Effects/Wind_Tornado/sprite_%d.png",
                11, 100, 100 , 200,
                AnimationLoopType.TWO_CYCLES
        );
    }
    public List<Skill> createSkills() {
        FullExecuteConsumer windTornadoLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = CombatMath.calculateDamage(user,40,(int)18.5,1.2,0.05);
            Character target = targets.getFirst();

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);

            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_TORNADO", t, () -> {

                    t.takeDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }
//            if (onSkillComplete != null) {
//                onSkillComplete.run();
//            }
        };
        FullExecuteConsumer windBurstLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = CombatMath.calculateDamage(user,30,(int)18.5,1.2,0.05);
            Character target = targets.getFirst();

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user, self);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };


        FullExecuteConsumer windPierceLogic= (self, user, targets, onSkillComplete) -> {
            int calculateDamage = CombatMath.calculateDamage(user,20,15,1.2,0.05);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);

            for(Character t : targets) {
                t.takeDamage(calculateDamage, user, self);
            }

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };


        Skill WindBurst = new Skill(
                "Wind Burst", "Unleashes compressed wind", 20, 25,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                windBurstLogic
        );

        Skill WindPierce = new Skill(
                "Wind Pierce", "Single-target Sharp Wind spell", 15, 20,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_TWO_TARGETS,
                windPierceLogic
        );
        Skill WindTornado = new Skill(
                "Wind Tornado", "Multi-target Wind spell", 40, 40,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_ALL_TARGETS,
                windTornadoLogic
        );

        return List.of(WindTornado,WindBurst,WindPierce);
    }
}
