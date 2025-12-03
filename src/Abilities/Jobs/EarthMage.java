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


public class EarthMage extends JobClass {

    public static final String description = "A stalwart spellcaster who channels the raw power of stone and soil. The Mage Earth conjures barriers, summons tremors, and hardens allies’ defenses. Steadfast and immovable, she is the embodiment of endurance and stability.";
    public static final String IDLE_PATH = "Assets/Animations/Heroes/Mage-Earth/Idle/sprite_%d.png";

    public EarthMage(){
        super("Earth Mage", description,0,0,"MAGE_EARTH-IDLE");


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
        return List.of();
    }

    public List<Skill> createSkills() {
        SkillLogicConsumer earthAttackLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,50,28,1.2,0.05);

            LogManager.log(self.getActionLog(user, "The earth unleashes itself", targets), LogFormat.HERO_ACTION);
            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("EARTH_ATTACK", t, () -> {

                    t.receiveDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }


            if (onSkillComplete != null) {
                onSkillComplete.run();
            }

        };

        SkillLogicConsumer earthquakeSpellLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,55,(int)23.5,1.2,0.05);

            LogManager.log(self.getActionLog(user, "Shakes the earth", targets), LogFormat.HERO_ACTION);
            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("EARTHQUAKE", t, () -> {

                    t.receiveDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }
            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        SkillLogicConsumer stoneHailLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,20,10,1.2,0.05);
            Character target = targets.get(0);
            target.receiveDamage(calculateDamage, user, self);
            LogManager.log(self.getActionLog(user, "Multitudes of earth crumbles down", targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("STONE_HAIL", target, () -> {
                target.receiveDamage(calculateDamage, user, self);

                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
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
