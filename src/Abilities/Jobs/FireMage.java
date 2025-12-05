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


public class FireMage extends JobClass {

    public static final String description = "Fierce and unpredictable, the Mage Fire wields flames with destructive passion. She incinerates her foes with fireballs, engulfs battlefields in blazing infernos, and thrives on chaos. Her magic is as dangerous as it is mesmerizing.";
    public static final String IDLE_PATH = "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_%d.png";

    public FireMage() {

        super("Fire Mage", description, 0, 20,"MAGE_IDLE");


        AssetManager.getInstance().registerAnimation(
                "MAGE_IDLE",
                "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "FIREBALL",
                "Assets/Animations/Heroes/Mage-Fire/Effects/FireBall/sprite_%d.png",
                6, 100,100, 100,
                AnimationLoopType.ONE_CYCLE
        );

        AssetManager.getInstance().registerAnimation(
                "FIRE_CYCLONE",
                "Assets/Animations/Heroes/Mage-Fire/Effects/FireCyclone/sprite_%d.png",
                5, 100,100, 300,
                AnimationLoopType.TWO_CYCLES
        );
    }

    @Override
    public List<ReactionSkill> createReactions() {
        ReactionLogic reflectFireballLogic = (defender, attacker, incomingSkill, incomingDamage) -> {
            double hp_percent = (double)defender.getHealth() / defender.getInitialHealth();
            int calculateDmg = ScalingLogic.calculateDamage(defender,25,15,1.2,0.05);
            int calculateDamage = (int)(calculateDmg * 0.4);
            if (Dice.getInstance().chance(0.25) && hp_percent < 0.40) {
                LogManager.log(defender.getName() + " Attacks them back", LogFormat.ENEMY_ACTION);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", attacker, () ->{
                    attacker.receiveDamage(calculateDamage, defender, incomingSkill);
                }, true);
                return 0;
            }
            return -1;
        };

        ReactionSkill ReflectFireball = new ReactionSkill("Reflect Fireball", ReactionTrigger.ON_RECEIVE_DAMAGE, reflectFireballLogic);

        return List.of(ReflectFireball);
    }

    public List<Skill> createSkills() {

        // FIXME: turn doesn't end when animation is finished
        SkillLogicConsumer fireBallLogic = (self, user, targets, onSkillComplete) -> {

            int calculateDamage = ScalingLogic.calculateDamage(user,25,15,1.2,0.05);
            Character target = targets.get(0);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", target, () -> {
                target.receiveDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);

        };

        SkillLogicConsumer fireCycloneLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,50,30,1.2,0.05);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
            for(Character t : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("FIRE_CYCLONE", t, () -> {

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
        SkillLogicConsumer staffAttackLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,10,0,1.2,0.05);
            Character target = targets.get(0);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
            target.receiveDamage(calculateDamage, user, self);
            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        Skill fireball = new Skill(
                "Fireball", "Single-target fire spell", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                fireBallLogic
        );

        Skill fireCyclone = new Skill(
                "Fire Cyclone", "Multi-target fire spell", 50, 55,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                fireCycloneLogic
        );

        Skill staffAttack= new Skill(
                "Staff Attack", "Single-target spell", 0, 10,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                staffAttackLogic
        );


        return List.of(staffAttack,fireball, fireCyclone);
    }
    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}