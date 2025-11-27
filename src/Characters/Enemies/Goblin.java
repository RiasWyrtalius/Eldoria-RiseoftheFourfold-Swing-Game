package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
import Characters.Character;
import Core.Utils.Dice;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Goblin extends Enemy {
    public Goblin() {

        super("Goblin Grunt", 500, 10, 0, 1, "Goblin", 10, "GOBLIN_IDLE", "Steals stuff and kills stuff.");
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
        initializeReactions();
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer skirmishLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        SkillLogicConsumer throwCoinsLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets), LogColor.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
//            VisualEffectsManager.getInstance().hideCharacterVisual(user);
//            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", target, () -> {
            target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
//            }, true);
        };

        Skill skirmish = new Skill(
                "Skirmish", "something something",0, 15,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                skirmishLogic
        );
        Skill ThrowCoin = new Skill(
                "Coin Throw", "Fling Coins",0, 10,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
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
        Skill skill = Dice.pickRandom(skills);
        skill.execute(this, targets, onSkillComplete);
    }

    protected void initializeReactions() {
        // panics at 50%
        ReactionLogic cowardiceLogic = (defender, attacker, skill, incomingDamage) -> {
            double healthPercent = (double) defender.getHealth() / defender.getInitialHealth();
            if (healthPercent > 0.50) {
                return -1;
            }

            if (Dice.chance(0.30)) {
                LogManager.log(defender.getName() + " panics and dodges the attack!", LogColor.ENEMY_ACTION);

//                TODO: Trigger pop up or sound or something
                return 0;
            }

            return -1;
        };

        this.addReaction(new ReactionSkill("Cowardice", 0.30, cowardiceLogic));
    }
}
