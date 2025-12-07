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
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Paladin/Idle/sprite_%d.png";
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
                "Assets/Animations/Heroes/Paladin/Effects/Revive/sprite_%d.png",
                9, 100, 100 , 500,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "PALADIN_HOLY-STRIKE",
                "Assets/Animations/Heroes/Paladin/Effects/Holy_Strike/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic ReviveLogic = (defender, attacker, incomingSkill, incomingDamage) -> {
            double hp_percent = (double)defender.getHealth() / defender.getMaxHealth();
            if(defender.getHealth() - incomingDamage > 0){
                return -1;
            }
            int revive_health= (int)(defender.getMaxHealth() * 0.30);
            int reset_mana= (int)(defender.getMaxMana() * 0.50);
            if (Dice.getInstance().chance(0.75) && hp_percent <= 0 && !hasRevived) {
                defender.setHealth(revive_health, defender);
                defender.setMana(reset_mana);
                hasRevived = true;
                VisualEffectsManager.getInstance().playAnimation("PALADIN_REVIVE", defender, () -> {
                    LogManager.log(defender.getName() + " REVIVES", LogFormat.HERO_ACTION);
                    }, true);
                return 0;
            }
            return -1;
        };

        ReactionSkill Revive = new ReactionSkill("Revive", ReactionTrigger.ON_FATAL_DAMAGE, ReviveLogic);

        return List.of(Revive);
    }

    @Override
    public List<Skill> createSkills() {
        SkillLogicConsumer healSelfLogic = (self, user, targets, onSkillComplete) -> {
            LogManager.log(self.getActionLog(user, " Heals", targets), LogFormat.HERO_ACTION);
            int heal = ScalingLogic.calculateStat(user.getLevel(),20,10,0.05);
            int curr = user.getHealth();


                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("PALADIN_REVIVE", targets.getFirst(), () -> {
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                        user.setHealth(heal + curr, user);
                    }
                }, true);
        };

        SkillLogicConsumer holyStrikeLogic = (self, user, targets, onSkillComplete) -> {
            int dmg = ScalingLogic.calculateStat(user.getLevel(),30,20,0.05);
            LogManager.log(self.getActionLog(user, "Strikes", targets), LogFormat.HERO_ACTION);

            Character target = targets.getFirst();

                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("PALADIN_HOLY-STRIKE", target, () -> {
                    target.receiveDamage(dmg, user, self);
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    }
                }, true);
        };

        Skill HealSelf = new Skill(
                "Self Heal", "Selfish Healing for themself", 10, 20,
                SkillType.HEAL, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                healSelfLogic
        );
        Skill HolyStrike = new Skill(
                "Holy Strike", "Righteousness as a Sword Strike", 30, 30,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                holyStrikeLogic
        );

        return List.of(HolyStrike,HealSelf);

    }

    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}