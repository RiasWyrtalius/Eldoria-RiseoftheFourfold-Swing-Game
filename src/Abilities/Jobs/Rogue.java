package Abilities.Jobs;

import Abilities.*;
import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Rogue extends JobClass{


        public Rogue() {
            super("Rogue", "Wields Knife and the shadows", -20, 0);

            AssetManager.getInstance().registerAnimation(
                    "MAGE_IDLE",
                    "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_%d.png",
                    3, 100, 100 , 300,
                    AnimationLoopType.INFINITE
            );

        }
        @Override
        public List<ReactionSkill> createReactions() {
            ReactionLogic dodgeLogic = (defender, attacker, incomingSkill, incomingDamage) -> {
                double hp_percent = (double)defender.getHealth() / defender.getInitialHealth();
                if (Dice.chance(0.55) && hp_percent < 0.98) {
//                    VisualEffectsManager.getInstance().playAnimation("ARCHER_DODGE", defender, () -> {
                        LogManager.log(defender.getName() + " Skillfully dodges the attack!", LogColor.ENEMY_ACTION);

//                    }, true);
                    return 0;
                }
                return -1;
            };

            ReactionSkill dodge = new ReactionSkill("Dodge", dodgeLogic);

            return List.of(dodge);
        }

        public List<Skill> createSkills() {

            // FIXME: turn doesn't end when animation is finished
            SkillLogicConsumer assassinateLogic = (self, user, targets, onSkillComplete) -> {

                int calculateDamage = ScalingLogic.calculateDamage(user,30,40,1.3,0.05);
                Characters.Character target = targets.getFirst();
                LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);
//                VisualEffectsManager.getInstance().playAnimationOnCharacter("FIREBALL", target, () -> {
                    target.takeDamage(calculateDamage, user, self);
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
//                }, true);

            };

            SkillLogicConsumer cloneAttackLogic = (self, user, targets, onSkillComplete) -> {
                int calculateDamage = ScalingLogic.calculateDamage(user,20,50,1.2,0.05);
                LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.HERO_ACTION);
                for(Character t : targets) {
//                    VisualEffectsManager.getInstance().playAnimationOnCharacter("FIRE_CYCLONE", t, () -> {

                        t.takeDamage(calculateDamage, user, self);

                        if (onSkillComplete != null) {
                            onSkillComplete.run();
                        }
//                    }, true);
                }

            };

            Skill Assassiante = new Skill(
                    "Assassinate", "Single-target physical", 20, 40,
                    SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                    assassinateLogic
            );

            Skill CloneAttack = new Skill(
                    "Clone Attack", "Multi-target clone spell", 50, 55,
                    SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.AOE_ALL_TARGETS,TargetCondition.ALIVE,
                    cloneAttackLogic
            );



            return List.of(Assassiante,CloneAttack);
        }

}
