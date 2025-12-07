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


public class CryoMancer extends JobClass {

    public static final String description = "Cold and calculating, the CryoMancer freezes enemies in their tracks. She conjures blizzards, sharp ice shards, and chilling prisons to sap the strength of her foes. Her frosty power brings control and precision to the battlefield.";
    private static final String IDLE_PATH = "Assets/Animations/Heroes/Mage-Ice/Idle/sprite_%d.png";

    public CryoMancer(){
        super("CryoMancer", description, "MAGE_ICE-IDLE",0,30);
    }

    @Override
    public void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "MAGE_ICE-IDLE",
                "Assets/Animations/Heroes/Mage-Ice/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "ICE_SPIKE",
                "Assets/Animations/Heroes/Mage-Ice/Effects/Ice_Spike/sprite_%d.png",
                7, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "FROSTBITE",
                "Assets/Animations/Heroes/Mage-Ice/Effects/Frostbite/sprite_%d.png",
                5, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic reflectIceSpikeLogic = (defender, attacker, incomingSkill, incomingDamage, onComplete) -> {
            double hp_percent = (double)defender.getHealth() / defender.getMaxHealth();
            int calculateDmg = ScalingLogic.calculateDamage(defender,30,15,1.2,0.05);
            // TODO: should prolly scale on targets defense
            int counterDamage = (int)(calculateDmg * 0.4);
            if (hp_percent < 0.40 && Dice.getInstance().chance(0.25)) {
                LogManager.log(defender.getName() + " Attacks them back", LogFormat.ENEMY_ACTION);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("ICE_SPIKE", attacker, () -> {
                    attacker.receiveDamage(counterDamage, defender, null, () -> {
                        onComplete.accept(0);
                    });
                }, true);
            } else {
                // hehe it failed
                onComplete.accept(incomingDamage);
            }
        };

        ReactionSkill ReflectIceSpike= new ReactionSkill("Reflect Ice Spike", ReactionTrigger.ON_RECEIVE_DAMAGE, reflectIceSpikeLogic);
        return List.of(ReflectIceSpike);
    }

    @Override
    public List<Skill> createSkills() {

        SkillLogicConsumer iceSpikeLogic = (_, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user, 30, 15, 1.2, 0.05);
            Character target = targets.get(0);
            LogManager.log(self.getActionLog(user, "summons an Ice Spike under", targets), LogFormat.HERO_ACTION);

            VisualEffectsManager.getInstance().playAnimationOnCharacter("ICE_SPIKE", target, () -> {
                target.receiveDamage(calculateDamage, user, self, onSkillComplete);
            }, true);
        };

        SkillLogicConsumer frostBiteLogic = (_, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,15,15,1.2,0.05);
            Character target = targets.get(0);

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("FROSTBITE", target, () -> {
                target.receiveDamage(calculateDamage, user, self, onSkillComplete);
            }, true);
        };

        Skill IceSpike = new Skill(
                "Ice Spike", "Unleashes spikes from the ground", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                iceSpikeLogic
        );

        Skill FrostBite = new Skill(
                "Frost Bite", "Unleashes cold Air to enemy", 10, 15,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                frostBiteLogic
        );

        return List.of(IceSpike,FrostBite);
    }

    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}


