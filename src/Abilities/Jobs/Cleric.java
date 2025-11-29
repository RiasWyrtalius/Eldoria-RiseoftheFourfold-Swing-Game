package Abilities.Jobs;

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

public class Cleric extends JobClass {

    public static final String description = "A devoted healer and protector, the Cleric channels divine energy to restore allies and banish darkness. Her blessings strengthen companions, while her radiant light wards off evil. Compassionate yet formidable, she is the heart of the party.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Cleric/Idle/sprite_%d.png";

    public Cleric() {
        super("Cleric", description, 10, 0);
        AssetManager.getInstance().registerAnimation(
                "CLERIC_IDLE",
                "Assets/Animations/Heroes/Cleric/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "CLERIC_HEAL",
                "Assets/Animations/Heroes/Cleric/Effects/Heal/sprite_%d.png",
                4, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );

    }

    @Override
    public List<ReactionSkill> createReactions() {
        return List.of();
    }

    public List<Skill> createSkills() {
        SkillLogicConsumer healSelfLogic = (self, user, targets, onSkillComplete) -> {
            Character target = user;
            LogManager.log(self.getActionLog(user, " Heals", targets), LogColor.HERO_ACTION);
            int heal = ScalingLogic.calculateStat(user.getLevel(),30,10,0.05);
            int curr = user.getHealth();

            user.setHealth(heal + curr);

            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("CLERIC_HEAL", target, () -> {
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
            }, true);
        };

        SkillLogicConsumer healGroupLogic = (self, user, targets, onSkillComplete) -> {


            LogManager.log(self.getActionLog(user, " Heals", targets), LogColor.HERO_ACTION);

            int heal = ScalingLogic.calculateStat(user.getLevel(),20,10,0.05);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("CLERIC_HEAL", user, () -> {
                if (onSkillComplete != null) {
                    for(Character target : targets) {
                        int curr = target.getHealth();
                        target.setHealth(heal + curr);
                    }
                    onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
            }, true);
        };

        SkillLogicConsumer BashLogic = (self, user, targets, onSkillComplete) -> {
            Character target = targets.get(0);

            int calculateDamage = ScalingLogic.calculateDamage(user,20,20,0.02,0.005);

                    target.takeDamage(calculateDamage, user, self);
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }

        };

        SkillLogicConsumer reviveLogic = (self, user, targets, onSkillComplete) -> {
            Character target = targets.getFirst();

            int revive_health= (int)(target.getInitialHealth() * 0.20);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("CLERIC_HEAL", user, () -> {
                target.setHealth(revive_health);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
            }, true);

        };




        Skill HealSelf = new Skill(
                "Self Heal", "Healing themself", 15, 20,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                healSelfLogic
        );
        Skill HealGroup = new Skill(
                "Group Heal", "Healing their teammate", 40, 20,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                healGroupLogic
        );
        Skill BashStaff = new Skill(
                "Bash Staff", "Healing their teammate", 10, 20,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                BashLogic
        );
        Skill Revive = new Skill(
                "Revive", "Revive their teammate", 40, 0,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.DEAD,
                reviveLogic
        );


        return List.of(HealSelf,HealGroup,BashStaff,Revive);
    }

    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}