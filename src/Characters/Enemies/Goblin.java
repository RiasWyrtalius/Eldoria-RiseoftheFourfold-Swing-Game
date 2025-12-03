package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
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

public class Goblin extends Enemy {
    public Goblin() {
        this(1);
    }

    public Goblin(int level) {
        super(
                "Goblin Grunt",
                ScalingLogic.calculateStat(level, 60, 5, 0.10),
                ScalingLogic.calculateStat(level, 10, 2, 0.05),
                0,
                level,
                "Goblin",
                15 * level,
                "GOBLIN_IDLE",
                "Steals stuff and kills stuff."
                );

        registerAssets();
        initializeReactions();
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "GOBLIN_IDLE",
                "Assets/Animations/Enemies/Goblin/Idle/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "GOBLIN_SWING-ATTACK",
                "Assets/Animations/Enemies/Goblin/Effects/Swing_Attack/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer skirmishLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,baseAtk,0.2,0.1);

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", target, () -> {
                target.receiveDamage(calculateDamage, user, self);
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        SkillLogicConsumer throwCoinsLogic = (self, user, targets, onSkillComplete) -> {
            int coins = Dice.getInstance().roll(1,5);
            int calculateDamage = ScalingLogic.calculateDamage(user,baseAtk,0.2,0.1) * coins;

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogFormat.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
//            VisualEffectsManager.getInstance().hideCharacterVisual(user);
//            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", target, () -> {
            target.receiveDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
//            }, true);
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
    public void makeAttack(List<Character> targets, Runnable onSkillComplete) {
        // TODO: randomly select skill
        // TODO: add AI that checks if skill has enough mana or not
        Skill skill = Dice.getInstance().pickRandom(skills);
        skill.execute(this, targets, onSkillComplete);
    }

    protected void initializeReactions() {
        // panics at 50%
        ReactionLogic cowardiceLogic = (defender, attacker, skill, incomingDamage) -> {
            double healthPercent = (double) defender.getHealth() / defender.getInitialHealth();
            if (healthPercent > 0.50) {
                return -1;
            }

            if (Dice.getInstance().chance(0.30)) {
                LogManager.log(defender.getName() + " panics and dodges the attack!", LogFormat.ENEMY_ACTION);

//                TODO: Trigger pop up or sound or something
                return 0;
            }

            return -1;
        };

        this.addReaction(new ReactionSkill("Cowardice", ReactionTrigger.ON_RECEIVE_DAMAGE, cowardiceLogic));
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
    }
}
