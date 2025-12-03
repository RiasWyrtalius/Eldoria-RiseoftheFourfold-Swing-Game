package Abilities.Jobs;

import Abilities.*;
import Characters.Character;
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
            super("Rogue", "Wields Knife and the shadows", -20, 0, "ROGUE_IDLE");
            
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
            ReactionLogic dodgeLogic = (defender, attacker, incomingSkill, incomingDamage) -> {
                double hp_percent = (double)defender.getHealth() / defender.getInitialHealth();
                if (Dice.getInstance().chance(0.50) && hp_percent < 0.60) {
                    LogManager.log(defender.getName() + " skillfully dodges the attack!", LogFormat.ENEMY_ACTION);
                    VisualEffectsManager.getInstance().playAnimation("ROGUE_DODGE", defender, null, true);
                    return 0;
                }
                return -1;
            };

//            ReactionLogic receiveManaLogic = (_,_,_,_) -> {
//                LogManager.log("Recieved mana");
//                return -1;
//            };


//            ReactionSkill receiveMana = new ReactionSkill("receiveMana", ReactionTrigger.ON_RECEIVE_MANA, receiveManaLogic);
            ReactionSkill dodge = new ReactionSkill("Dodge", ReactionTrigger.ON_RECEIVE_DAMAGE, dodgeLogic);

            return List.of(dodge);
        }

        public List<Skill> createSkills() {

            // FIXME: turn doesn't end when animation is finished
            SkillLogicConsumer assassinateLogic = (self, user, targets, onSkillComplete) -> {

                int calculateDamage = ScalingLogic.calculateDamage(user,30,40,1.3,0.05);
                Characters.Character target = targets.getFirst();
                LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("ROGUE_ATTACK", target, () -> {
                    target.receiveDamage(calculateDamage, user, self);
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    }
                }, true);

            };

            SkillLogicConsumer cloneAttackLogic = (self, user, targets, onSkillComplete) -> {
                int calculateDamage = ScalingLogic.calculateDamage(user,20,50,1.2,0.05);
                LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.HERO_ACTION);
                for(Character t : targets) {
                VisualEffectsManager.getInstance().hideCharacterVisual(user);
                VisualEffectsManager.getInstance().playAnimationOnCharacter("ROGUE_ATTACK", t, () -> {
                    t.receiveDamage(calculateDamage, user, self);
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    }
                }, true);
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
