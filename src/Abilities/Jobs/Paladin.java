package Abilities.Jobs;

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

public class Paladin extends JobClass {

    public static final String description = "A beacon of righteousness clad in radiant armor, the Paladin is both protector and purifier. Guided by unwavering faith, he channels divine power to heal allies and smite evil. With a shield in hand and conviction in heart, he stands firm against darkness, inspiring hope and courage in all who fight beside him.";
    private static final String IDLE_PATH = "/Assets/Animations/Heroes/Paladin/Idle/sprite_%d.png";
    private static boolean hasRevived = false;

    public Paladin() {
        super("Paladin", description, "PALADIN_IDLE", 30, 20);
    }

    @Override
    public void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "PALADIN_IDLE",
                IDLE_PATH,
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "PALADIN_REVIVE",
                "/Assets/Animations/Heroes/Paladin/Effects/Revive/sprite_%d.png",
                9, 100, 100 , 500,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "PALADIN_HOLY-STRIKE",
                "/Assets/Animations/Heroes/Paladin/Effects/Holy_Strike/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic ReviveLogic = (defender, _, _, incomingDamage, onComplete) -> {
            if (hasRevived) {
                onComplete.accept(ReactionResult.FAILED(incomingDamage));
                return;
            }

            if (Dice.getInstance().chance(0.75)) {
                LogManager.log(defender.getName() + "'s Divine Intervention activates!", LogFormat.HIGHLIGHT_BUFF);
                Runnable afterReviveAnim = () -> {
                    int revive_health = (int)(defender.getMaxHealth() * 0.30);
                    int reset_mana = (int)(defender.getMaxMana() * 0.50);

                    defender.setHealth(revive_health, null); // Source is the skill itself, not another char
                    defender.setMana(reset_mana);
                    hasRevived = true;
                    onComplete.accept(new ReactionResult(true, 0, true));
                };

                VisualEffectsManager.getInstance().playAnimation("PALADIN_REVIVE", defender, afterReviveAnim, true);
            } else {
                LogManager.log("Divine Intervention failed to trigger...");
                onComplete.accept(ReactionResult.FAILED(incomingDamage));
            }
        };

        ReactionSkill Revive = new ReactionSkill("Revive", ReactionTrigger.ON_FATAL_DAMAGE, ReviveLogic);

        return List.of(Revive);
    }

    @Override
    public List<Skill> createSkills() {
        SkillLogicConsumer healSelfLogic = (_, self, target, _, onSkillComplete) -> {
            LogManager.log(self.getActionLog(target, "heals themself", List.of(target)), LogFormat.HERO_ACTION);
            int healAmount = ScalingLogic.calculateStat(target.getLevel(), 30, 15, 0.03);
            Runnable afterAnimation = () -> {
                target.receiveHealing(healAmount, target);
                VisualEffectsManager.getInstance().restoreCharacterVisual(target);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            };
            VisualEffectsManager.getInstance().hideCharacterVisual(target);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("PALADIN_HEAL", target, afterAnimation, true);
        };

        SkillLogicConsumer holyStrikeLogic = (controller, self, user, targets, onSkillComplete) -> {
            int dmg = ScalingLogic.calculatePhysicalDamage(user,300,2.5,0.04);
            LogManager.log(self.getActionLog(user, "Strikes", targets), LogFormat.HERO_ACTION);
            Character target = targets.get(0);
            Runnable afterAnimation = () -> {
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                target.receiveDamage(dmg, user, self, onSkillComplete);
            };
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("PALADIN_HOLY-STRIKE", target, afterAnimation, true);
        };

        Skill HealSelf = new Skill(
                "Self Heal", "Selfish Healing for themself", 20, 20,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                healSelfLogic
        );
        Skill HolyStrike = new Skill(
                "Holy Strike", "Righteousness as a Sword Strike", 25, 30,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                holyStrikeLogic
        );

        return List.of(HolyStrike,HealSelf);

    }

    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}