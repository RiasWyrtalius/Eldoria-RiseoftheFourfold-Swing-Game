package Abilities.Jobs;

import Abilities.*;
import Characters.Character;
import Core.Battle.BattleController;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;
import Core.Visuals.VisualEffectsManager;
import Resource.Animation.AnimationLoopType;
import Resource.Animation.AssetManager;

import java.util.List;

public class Rogue extends JobClass{
        //TODO: add to Class: CharacterSelection, IDLE_PATH, DESC, WAITING ON ANIMATIONS
        public static final String description = "A shadow in the night, the Rogue moves with silent precision and deadly intent. Master of stealth and deception, she strikes from the shadows before vanishing without a trace. Agile and cunning, she excels at disabling traps, picking locks, and exploiting enemy weaknesses. Her loyalty lies with the mission—and her own code.";
        private static final String IDLE_PATH = "Assets/Animations/Heroes/Rogue/Idle/sprite_%d.png";
        public Rogue() {
            super("Rogue", description, "ROGUE_IDLE", 0, 30);
        }

        @Override
        public void registerAssets() {
            AssetManager.getInstance().registerAnimation(
                    "ROGUE_IDLE",
                    IDLE_PATH,
                    3, 100, 100 , 300,
                    AnimationLoopType.INFINITE
            );
            AssetManager.getInstance().registerAnimation(
                    "ROGUE_ATTACK",
                    "Assets/Animations/Heroes/Rogue/Effects/Attack/sprite_%d.png",
                    6, 100, 100 , 200,
                    AnimationLoopType.ONE_CYCLE
            );
            AssetManager.getInstance().registerAnimation(
                    "ROGUE_DODGE",
                    "Assets/Animations/Heroes/Rogue/Effects/Dodge/sprite_%d.png",
                    6, 100, 100 , 200,
                    AnimationLoopType.ONE_CYCLE
            );
        }

        @Override
        public List<ReactionSkill> createReactions() {
            ReactionLogic dodgeLogic = (defender, _, _, incomingDamage, onComplete) -> {
                double hp_percent = (double)defender.getHealth() / defender.getMaxHealth();
                if (hp_percent >= 0.60) {
                    onComplete.accept(incomingDamage);
                    return;
                }

                // Scale chance from 10% at 60% HP up to 40% at 0% HP
                double dodgeChance = 0.10 + (0.60 - hp_percent) * 0.50;
                // Clamp so it never goes above 40%
                if (dodgeChance > 0.40) {
                    dodgeChance = 0.40;
                }

                if (Dice.getInstance().chance(dodgeChance)) {
                    LogManager.log(defender.getName() + " skillfully dodges the attack!", LogFormat.ENEMY_ACTION);
                    VisualEffectsManager.getInstance().playAnimation("ROGUE_DODGE", defender, () -> {
                        onComplete.accept(0);
                    }, true);
                } else {
                    onComplete.accept(incomingDamage);
                }
            };

//            ReactionLogic receiveManaLogic = (_,_,_,_) -> {
//                LogManager.log("Recieved mana");
//                return -1;
//            };

//            ReactionSkill receiveMana = new ReactionSkill("receiveMana", ReactionTrigger.ON_RECEIVE_MANA, receiveManaLogic);
            ReactionSkill dodge = new ReactionSkill("Dodge", ReactionTrigger.ON_RECEIVE_DAMAGE, dodgeLogic);

            return List.of(dodge);
        }

        @Override
        public List<Skill> createSkills() {
            // FIXME: turn doesn't end when animation is finished
            SkillLogicConsumer assassinateLogic = (_,self, user, targets, onSkillComplete) -> {
                int calculateDamage = ScalingLogic.calculateDamage(user,10,40,1.3,0.05);
                Characters.Character target = targets.getFirst();
                LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);

                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("ROGUE_ATTACK", target, () -> {
                    target.receiveDamage(calculateDamage, user, self, () -> {
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                        if (onSkillComplete != null) onSkillComplete.run();
                    });
                }, true);
            };

            SkillLogicConsumer cloneAttackLogic = (controller, self, user, targets, onSkillComplete) -> {
                int calculateDamage = ScalingLogic.calculateDamage(user,10,30,1.2,0.05);
                LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);

                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playGroupAnimation("ROGUE_ATTACK", targets, () -> {
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    controller.applyGroupDamage(user, self, targets, calculateDamage, onSkillComplete);
                }, true);
            };

            Skill Assassinate = new Skill(
                    "Assassinate", "Single-target physical", 20, 40,
                    SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                    assassinateLogic
            );

            Skill CloneAttack = new Skill(
                    "Clone Attack", "Multi-target clone spell", 50, 30,
                    SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.AOE_ALL_TARGETS,TargetCondition.ALIVE,
                    cloneAttackLogic
            );



            return List.of(Assassinate,CloneAttack);
        }

}
