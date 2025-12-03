package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.ScalingLogic;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Animation.AnimationLoopType;
import Resource.Animation.AssetManager;

import java.util.List;


public class AeroMancer extends JobClass {
    private static final String description = "Master of the skies, the AeroMancer bends the wind to her will. She summons gales to scatter enemies, rides currents to evade danger, and unleashes razor-sharp blasts of air. Her magic is swift, elusive, and devastating.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Mage-Wind/Idle/sprite_%d.png";

    public AeroMancer(){
        super("Wind Mage", description,0,0,"MAGE_WIND-IDLE");
        AssetManager.getInstance().registerAnimation(
                "MAGE_WIND-IDLE",
                IDLE_PATH,
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

    @Override
    public List<ReactionSkill> createReactions() {
        return List.of();
    }

    public List<Skill> createSkills() {
        SkillLogicConsumer windTornadoLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,40,(int)18.5,1.2,0.05);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);

            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_TORNADO", t, () -> {

                    t.receiveDamage(calculateDamage, user, self);

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
            Character target = targets.get(0);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_SLASH", target, () -> {

                target.receiveDamage(calculateDamage, user, self);

                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);

        };


        SkillLogicConsumer windPierceLogic= (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,20,15,1.2,0.05);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);

            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_PIERCE", t, () -> {

                    t.receiveDamage(calculateDamage, user, self);

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

    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}
