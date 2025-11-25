package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
import Characters.Character;
import Core.Dice;
import Core.LogColor;
import Core.LogManager;
import Core.VisualEffectsManager;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Goblin extends Enemy {
    public Goblin() {

        super("Goblin Grunt", 300, 5, 0, 1, "Goblin", 10, "GOBLIN_IDLE");
        AssetManager.getInstance().registerAnimation(
                "GOBLIN_IDLE",
                "Assets/Animations/Goblin/Idle/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "GOBLIN_SWING-ATTACK",
                "Assets/Animations/Goblin/Effects/Swing_Attack/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.TWO_CYCLES
        );
        initializeReactions();
    }

    @Override
    protected void initializeSkills() {
        FullExecuteConsumer skirmishLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
            VisualEffectsManager.getInstance().playAnimationOnCharacter("GOBLIN_SWING-ATTACK", target, () -> {
                VisualEffectsManager.getInstance().playAnimation("GOBLIN_SWING-ATTACK", user, () -> {
                target.takeDamage(calculateDamage, user, self);
                    if (onSkillComplete != null) {
                        onSkillComplete.run();
                    }
            }, true);
            }, true);
        };

        Skill skirmish = new Skill(
                "Skirmish", "something something",0, 15,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                skirmishLogic
        );

        skills.add(skirmish);
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
