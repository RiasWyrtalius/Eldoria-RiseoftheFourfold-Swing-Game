package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
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

public class Goblin extends Enemy {
    public Goblin() {
        this(1);
    }

    public Goblin(int level) {
        super(
                "Goblin Grunt",
                ScalingLogic.calculateStat(level, 30, 10, 0.15),
                ScalingLogic.calculateStat(level, 10, 5, 0.05),
                0,
                level,
                "Goblin",
                15 * level,
                "Steals stuff and kills stuff.",
                "GOBLIN_IDLE"
                );

        registerAssets();
        initializeReactions();
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "GOBLIN_IDLE",
                "Assets/Animations/Enemies/Goblin/Idle/sprite_%d.png",
                4, 100, 100 , 150,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "GOBLIN_SWING-ATTACK",
                "Assets/Animations/Enemies/Goblin/Effects/Swing_Attack/sprite_%d.png",
                5, 100, 100 , 150,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer skirmishLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculatePhysicalDamage(user,baseAtk,0.2,0.1);

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", target, () -> {
                target.receiveDamage(calculateDamage, user, self, () -> {
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    if (onSkillComplete != null)  onSkillComplete.run();
                });
            }, true);
        };

        SkillLogicConsumer throwCoinsLogic = (controller, self, user, targets, onSkillComplete) -> {
            int coins = Dice.getInstance().roll(1, 3);
            int baseDamage = ScalingLogic.calculatePhysicalDamage(user, baseAtk, 0.2, 0.1);
            int calculateDamage = (int)(baseDamage * Math.sqrt(coins));

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
//            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimation("GOBLIN_SWING-ATTACK", user, () -> {
            target.receiveDamage(calculateDamage, user, self, () -> {
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                if (onSkillComplete != null) onSkillComplete.run();
            });
            }, true);
        };

        Skill skirmish = new Skill(
                "Skirmish", "something something",0, 15,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                skirmishLogic
        );
        Skill ThrowCoin = new Skill(
                "Coin Throw", "Fling Coins",0, 10,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                throwCoinsLogic
        );

        skills.add(skirmish);
        skills.add(ThrowCoin);
    }

//    TODO: replace this with randomly use skill function
    @Override
    public void makeAttack(BattleController controller, List<Character> targets, Runnable onSkillComplete) {
        // TODO: randomly select skill
        // TODO: add AI that checks if skill has enough mana or not
        Skill skill = Dice.getInstance().pickRandom(skills);
        skill.execute(controller, this, targets, onSkillComplete);
    }

    protected void initializeReactions() {
        // panics at 50%
        ReactionLogic cowardiceLogic = (defender, attacker, skill, incomingDamage, onComplete) -> {
            double healthPercent = (double) defender.getHealth() / defender.getMaxHealth();
            if (healthPercent > 0.50) {
                onComplete.accept(ReactionResult.FAILED(incomingDamage));
                return;
            }

            if (Dice.getInstance().chance(0.30)) {
                LogManager.log(defender.getName() + " panics and dodges the attack!", LogFormat.ENEMY_ACTION);

//                TODO: Trigger pop up or sound or something
                onComplete.accept(new ReactionResult(true, 0, true));
                return;
            }

            onComplete.accept(ReactionResult.FAILED(incomingDamage));
        };

//        ReactionLogic DeathLogic = (user,_,_,_, onComplete) -> {
//            VisualEffectsManager.getInstance().hideCharacterVisual(user);
    //            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", user, () -> {
    ////                // TODO: it seems redundant but pause character animation is called before this so..
    //                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
    //                VisualEffectsManager.getInstance().pauseCharacterAnimation(user);
    //            }, true);
    //            onComplete.accept(0);
//                return; // should die, 0 and 1 is dont die
//        };

        ReactionSkill cowardice = new ReactionSkill("Cowardice", ReactionTrigger.ON_RECEIVE_DAMAGE, cowardiceLogic);
//        ReactionSkill death = new ReactionSkill("death", ReactionTrigger.ON_FATAL_DAMAGE, DeathLogic);

        this.addReaction(cowardice);
//        this.addReaction(death);
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
    }
}
