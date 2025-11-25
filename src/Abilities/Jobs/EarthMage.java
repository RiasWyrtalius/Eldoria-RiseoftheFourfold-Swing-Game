package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;
import java.util.function.BiConsumer;


public class EarthMage extends JobClass {
    public EarthMage(){
        super("Earth Mage","Wields stone and earth as its magic",0,0 );

        AssetManager.getInstance().registerAnimation(
                "MAGE_IDLE",
                "Assets/Animations/Mage-Fire/Idle/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "EARTH_ATTACK",
                "Assets/Animations/Mage-Earth/Effects/Earth_Attack/sprite_%d.png",
                8, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "EARTHQUAKE",
                "Assets/Animations/Mage-Earth/Effects/Earthquake/sprite_%d.png",
                13, 100, 100 , 100,
                AnimationLoopType.TWO_CYCLES
        );

    }
    public List<Skill> createSkills() {
        FullExecuteConsumer earthAttackLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(50 + 28 + (user.getLevel() * 1.2) + (50 * (user.getLevel() * 0.05)));

            LogManager.log(self.getActionLog(user, "Shakes the earth", targets, calculateDamage), LogColor.HERO_ACTION);
            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("EARTH_ATTACK", t, () -> {

                    t.takeDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }


            if (onSkillComplete != null) {
                onSkillComplete.run();
            }

        };

        FullExecuteConsumer earthquakeSpellLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(55 + 23.5 + (user.getLevel() * 1.2) + (55 * (user.getLevel() * 0.05)));


            LogManager.log(self.getActionLog(user, "Shakes the earth", targets, calculateDamage), LogColor.HERO_ACTION);
            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("EARTHQUAKE", t, () -> {

                    t.takeDamage(calculateDamage, user, self);

                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
                }, true);
            }
            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        FullExecuteConsumer stoneHailLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(20 + 10 + (user.getLevel() * 1.2) + (20 * (user.getLevel() * 0.05)));
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user, self);
            LogManager.log(self.getActionLog(user, "Multitudes of earth crumbles down", targets, calculateDamage), LogColor.HERO_ACTION);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        Skill EarthAttack = new Skill(
                "Earth Attack", "Multi-target Earth spell", 60, 50,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.AOE_ALL_TARGETS,
                earthAttackLogic
        );

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

        return List.of(EarthquakeSpell,EarthAttack,StoneHail);
    }
}
