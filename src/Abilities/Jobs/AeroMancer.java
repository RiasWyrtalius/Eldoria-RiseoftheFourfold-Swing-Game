package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
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
        super("AeroMancer", description,"MAGE_WIND-IDLE",0,30);
    }

    @Override
    public void registerAssets() {
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
        ReactionLogic reflectWindPierceLogic = (defender, attacker, incomingSkill, incomingDamage, onComplete) -> {
            double hp_percent = (double)defender.getHealth() / defender.getMaxHealth();
            int calculateDmg = ScalingLogic.calculateMagicalDamage(defender,20,15,1.2);
            int calculateDamage = (int)(calculateDmg * 0.4);
            if (Dice.getInstance().chance(0.25) && hp_percent < 0.40) {
                LogManager.log(defender.getName() + " Attacks them back", LogFormat.ENEMY_ACTION);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_PIERCE", attacker, () ->{
                    attacker.receiveDamage(calculateDamage, defender, incomingSkill, () -> {
                        onComplete.accept(0);
                    });
                }, true);
            } else {
                onComplete.accept(incomingDamage);
            }
        };

        ReactionSkill ReflectWindPierce= new ReactionSkill("Reflect Wind Pierce", ReactionTrigger.ON_RECEIVE_DAMAGE, reflectWindPierceLogic);
        return List.of(ReflectWindPierce);
    }

    @Override
    public List<Skill> createSkills() {
        SkillLogicConsumer windTornadoLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateMagicalDamage(user, 30, (int) 18.5, 1.2);

            LogManager.log(self.getActionLog(user, "summons a tornado against", targets), LogFormat.HERO_ACTION);

            Runnable afterAllAnimations = () -> {
                // Delegate group damage to the controller
                controller.applyGroupDamage(user, self, targets, calculateDamage, onSkillComplete);
            };

            // Delegate group animation to the VEM
            VisualEffectsManager.getInstance().playGroupAnimation("WIND_TORNADO", targets, afterAllAnimations, true);
        };

        SkillLogicConsumer windSlashLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateMagicalDamage(user, 20, (int)18.5, 1.2);
            Character target = targets.get(0);
            LogManager.log(self.getActionLog(user, "slashes at", targets), LogFormat.HERO_ACTION);

            Runnable afterAnimation = () -> {
                // Call the async receiveDamage, which will handle reactions and then call onSkillComplete
                target.receiveDamage(calculateDamage, user, self, onSkillComplete);
            };

            VisualEffectsManager.getInstance().playAnimationOnCharacter("WIND_SLASH", target, afterAnimation, true);
        };


        SkillLogicConsumer windPierceLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateMagicalDamage(user, 20,15, 1.2);

            LogManager.log(self.getActionLog(user, "pierces", targets), LogFormat.HERO_ACTION);

            Runnable afterAllAnimations = () -> {
                controller.applyGroupDamage(user, self, targets, calculateDamage, onSkillComplete);
            };

            VisualEffectsManager.getInstance().playGroupAnimation("WIND_PIERCE", targets, afterAllAnimations, true);
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
