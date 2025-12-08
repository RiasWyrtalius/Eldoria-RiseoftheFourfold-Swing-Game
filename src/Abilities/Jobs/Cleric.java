package Abilities.Jobs;

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

public class Cleric extends JobClass {

    public static final String description = "A devoted healer and protector, the Cleric channels divine energy to restore allies and banish darkness. Her blessings strengthen companions, while her radiant light wards off evil. Compassionate yet formidable, she is the heart of the party.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Cleric/Idle/sprite_%d.png";

    public Cleric() {
        super("Cleric", description, "CLERIC_IDLE", 20, 40);
    }

    @Override
    public void registerAssets() {
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

    @Override
    public List<Skill> createSkills() {
        SkillLogicConsumer healSelfLogic = (_, self, user, targets, onSkillComplete) -> {
            LogManager.log(self.getActionLog(user, "heals", targets), LogFormat.HERO_ACTION);
            int heal = ScalingLogic.calculateStat(user.getLevel(), 30, 10, 0.1);

            VisualEffectsManager.getInstance().playAnimation("CLERIC_HEAL", user, () -> {
                // This runs AFTER the heal animation
                user.receiveHealing(heal, user);

                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };


        SkillLogicConsumer healGroupLogic = (_, self, user, targets, onSkillComplete) -> {
            LogManager.log(self.getActionLog(user, "heals", targets), LogFormat.HERO_ACTION);
            int heal = ScalingLogic.calculateStat(user.getLevel(), 20, 10, 0.1);

            // isn't there a helper function i made to replace this
            Runnable afterAllAnims = () -> {
                for (Character target : targets) {
                    target.receiveHealing(heal, user);
                }
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            };

            VisualEffectsManager.getInstance().playGroupAnimation("CLERIC_HEAL", targets, afterAllAnims, true);
        };

        SkillLogicConsumer reviveLogic = (_, self, user, targets, onSkillComplete) -> {
            Character target = targets.get(0);
            int revive_health = (int)(target.getMaxHealth() * 0.30);
            int reset_mana = (int)(target.getMaxMana() * 0.50);

            LogManager.log(self.getActionLog(user, "revives", targets), LogFormat.HERO_ACTION);

            VisualEffectsManager.getInstance().playAnimationOnCharacter("CLERIC_HEAL", user, () -> {
                // After the caster's animation, play the target's revive visual
                VisualEffectsManager.getInstance().reviveEffect(target);

                target.revive(revive_health, user);
                target.setMana(reset_mana);

                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        SkillLogicConsumer bashLogic = (_, self, user, targets, onSkillComplete) -> {
            Character target = targets.getFirst();
            int calculateDamage = ScalingLogic.calculatePhysicalDamage(user, 20, user.getBaseAtk(), 0.02);

            LogManager.log(self.getActionLog(user, "bashes", targets), LogFormat.HERO_ACTION);

            // fixed using async
            target.receiveDamage(calculateDamage, user, self, onSkillComplete);
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
                "Bash Staff", "Healing their teammate", 0, 20,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                bashLogic
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