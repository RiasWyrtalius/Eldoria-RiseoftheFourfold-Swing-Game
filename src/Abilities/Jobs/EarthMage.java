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


public class EarthMage extends JobClass {

    public static final String description = "A stalwart spellcaster who channels the raw power of stone and soil. The Mage Earth conjures barriers, summons tremors, and hardens allies’ defenses. Steadfast and immovable, she is the embodiment of endurance and stability.";
    public static final String IDLE_PATH = "Assets/Animations/Heroes/Mage-Earth/Idle/sprite_%d.png";

    public EarthMage(){
        super("EarthMage", description,"MAGE_EARTH-IDLE",0,30);
    }

    @Override
    public void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "MAGE_EARTH-IDLE",
                "Assets/Animations/Heroes/Mage-Earth/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "EARTH_ATTACK",
                "Assets/Animations/Heroes/Mage-Earth/Effects/Earth_Attack/sprite_%d.png",
                8, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "EARTHQUAKE",
                "Assets/Animations/Heroes/Mage-Earth/Effects/Earthquake/sprite_%d.png",
                13, 100, 100 , 100,
                AnimationLoopType.TWO_CYCLES
        );
        AssetManager.getInstance().registerAnimation(
                "STONE_HAIL",
                "Assets/Animations/Heroes/Mage-Earth/Effects/Stone_Hail/sprite_%d.png",
                7, 100, 100 , 100,
                AnimationLoopType.TWO_CYCLES
        );
    }

    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic reflectStoneHailLogic = (defender, attacker, incomingSkill, incomingDamage, onComplete) -> {
            double hp_percent = (double)defender.getHealth() / defender.getMaxHealth();
            int calculateDmg = ScalingLogic.calculateDamage(defender,20,10,1.2,0.05);
            int calculateDamage = (int)(calculateDmg * 0.4);
            if (Dice.getInstance().chance(0.25) && hp_percent < 0.40) {
                LogManager.log(defender.getName() + " Attacks them back", LogFormat.ENEMY_ACTION);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("STONE_HAIL", attacker, () ->{
                    attacker.receiveDamage(calculateDamage, defender, incomingSkill, () -> {
                        onComplete.accept(0);
                    });
                }, true);
            } else {
                onComplete.accept(incomingDamage);
            }
        };

        ReactionSkill ReflectStoneHail= new ReactionSkill("Reflect Stone Hail", ReactionTrigger.ON_RECEIVE_DAMAGE, reflectStoneHailLogic);

        return List.of(ReflectStoneHail);
    }

    @Override
    public List<Skill> createSkills() {
        SkillLogicConsumer earthAttackLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user, 50, 28, 1.2, 0.05);

            LogManager.log(self.getActionLog(user, "unleashes the earth's fury upon", targets), LogFormat.HERO_ACTION);

            Runnable afterAllAnimations = () -> {
                controller.applyGroupDamage(user, self, targets, calculateDamage, onSkillComplete);
            };
            VisualEffectsManager.getInstance().playGroupAnimation(
                    "EARTH_ATTACK",
                    targets,
                    afterAllAnimations,
                    true
            );
        };

        SkillLogicConsumer earthquakeSpellLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,55,(int)23.5,1.2,0.05);
            LogManager.log(self.getActionLog(user, "Shakes the earth", targets), LogFormat.HERO_ACTION);
            Runnable afterAllAnimations = () -> {
                controller.applyGroupDamage(user, self, targets, calculateDamage, onSkillComplete);
            };
            VisualEffectsManager.getInstance().playGroupAnimation(
                    "EARTHQUAKE",
                    targets,
                    afterAllAnimations,
                    true
            );
        };

        SkillLogicConsumer stoneHailLogic = (_, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,20,10,1.2,0.05);
            Character target = targets.get(0);
            LogManager.log(self.getActionLog(user, "Multitudes of earth crumbles down", targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("STONE_HAIL", target, () -> {
                target.receiveDamage(calculateDamage, user, self, onSkillComplete);
            }, true);

        };

        Skill EarthAttack = new Skill(
                "Earth Attack", "Multi-target Earth spell", 60, 50,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                earthAttackLogic
        );

        Skill EarthquakeSpell = new Skill(
                "Earthquake Spell", "Multi-targeted Earth spell", 50, 55,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                earthquakeSpellLogic
        );

        Skill StoneHail = new Skill(
                "Stone Hail", "Single-target Earth spell", 15, 20,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                stoneHailLogic
        );

        return List.of(EarthquakeSpell,EarthAttack,StoneHail);
    }
    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}
