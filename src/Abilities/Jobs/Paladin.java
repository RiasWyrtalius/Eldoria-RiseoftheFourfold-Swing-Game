package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
import Core.Utils.ScalingLogic;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Paladin extends JobClass {

    public static final String description = "A beacon of righteousness clad in radiant armor, the Paladin is both protector and purifier. Guided by unwavering faith, he channels divine power to heal allies and smite evil. With a shield in hand and conviction in heart, he stands firm against darkness, inspiring hope and courage in all who fight beside him.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Paladin/Idle/sprite_%d.png";
    private static boolean hasRevived = false;

    public Paladin() {
        super("Paladin", description, 30, 0);
        AssetManager.getInstance().registerAnimation(
                "PALADIN_IDLE",
                "Assets/Animations/Heroes/Paladin/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "PALADIN_REVIVE",
                "Assets/Animations/Heroes/Paladin/Effects/Revive/sprite_%d.png",
                9, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );


    }

    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic ReviveLogic = (defender, attacker, incomingSkill, incomingDamage) -> {
            double hp_percent = (double)defender.getHealth() / defender.getInitialHealth();
            if(defender.getHealth() - incomingDamage > 0){
                return -1;
            }
            int revive_health= (int)(defender.getInitialHealth() * 0.30);
            int reset_mana= (int)(defender.getMaxMana() * 0.50);
            if (hp_percent <= 0 && !hasRevived) {
                defender.setHealth(revive_health);
                defender.setMana(reset_mana);
                hasRevived = true;
                    VisualEffectsManager.getInstance().playAnimation("PALADIN_REVIVE", defender, () -> {
                    LogManager.log(defender.getName() + " REVIVES", LogColor.HERO_ACTION);
                    }, true);
                return 0;
            }
            return -1;
        };
        ReactionSkill Revive = new ReactionSkill("Revive", ReactionTrigger.ON_FATAL_DAMAGE, ReviveLogic);
        return List.of(Revive);
    }

    public List<Skill> createSkills() {
        SkillLogicConsumer healSelfLogic = (self, user, targets, onSkillComplete) -> {
            LogManager.log(self.getActionLog(user, " Heals", targets), LogColor.HERO_ACTION);

            int heal = ScalingLogic.calculateStat(user.getLevel(),30,10,0.05);
            int curr = user.getHealth();

            user.setHealth(heal + curr);

            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("PALADIN_REVIVE", user, () -> {
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


        return List.of(HealSelf);
    }

    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}