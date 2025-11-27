package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.ScalingLogic;
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
                "MAGE_WIND-IDLE",
                "Assets/Animations/Heroes/Mage-Wind/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "WIND_TORNADO",
                "Assets/Animations/Heroes/Mage-Wind/Effects/Wind_Tornado/sprite_%d.png",
                11, 100, 100 , 200,
                AnimationLoopType.TWO_CYCLES
        );
        AssetManager.getInstance().registerAnimation(
                "WIND_PIERCE",
                "Assets/Animations/Heroes/Mage-Wind/Effects/Wind_Pierce/sprite_%d.png",
                4, 100, 100 , 200,
                AnimationLoopType.TWO_CYCLES
        );
        AssetManager.getInstance().registerAnimation(
                "WIND_SLASH",
                "Assets/Animations/Heroes/Mage-Wind/Effects/Wind_Slash/sprite_%d.png",
                4, 100, 100 , 200,
                AnimationLoopType.TWO_CYCLES
        );
    }
    public List<Skill> createSkills() {
        SkillLogicConsumer windTornadoLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,40,(int)18.5,1.2,0.05);
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
        SkillLogicConsumer windSlashLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,30,(int)18.5,1.2,0.05);
            Character target = targets.getFirst();

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_SLASH", target, () -> {

                target.takeDamage(calculateDamage, user, self);

                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);

        };


        SkillLogicConsumer windPierceLogic= (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,20,15,1.2,0.05);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);

            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_PIERCE", t, () -> {

                    t.takeDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }

        };


        Skill WindSlash = new Skill(
                "Wind Slash", "A slash compressed wind", 20, 25,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                windSlashLogic
        );

        Skill WindPierce = new Skill(
                "Wind Pierce", "Single-target Sharp Wind spell", 15, 20,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.AOE_TWO_TARGETS, TargetCondition.ALIVE,
                windPierceLogic
        );
        Skill WindTornado = new Skill(
                "Wind Tornado", "Multi-target Wind spell", 40, 40,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                windTornadoLogic
        );

        return List.of(WindTornado,WindSlash,WindPierce);
    }
}
