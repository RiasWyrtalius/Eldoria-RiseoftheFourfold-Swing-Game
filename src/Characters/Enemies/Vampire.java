package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.Dice;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Vampire extends Enemy {
    public Vampire() {

        super("Vampire", 500, 30, 0, 1, "Vampire", 10, "VAMPIRE_IDLE", "Blood Thirsty");
        AssetManager.getInstance().registerAnimation(
                "VAMPIRE_IDLE",
                "Assets/Animations/Enemies/Vampire/Idle/sprite_%d.png",
                3, 100, 100 , 280,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "VAMPIRE_ATTACK",
                "Assets/Animations/Enemies/Vampire/Effects/Attack/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
//        initializeReactions();
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer vampAttackLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.pickRandom(targets);
            LogManager.log(self.getActionLog(user, "Attacks", targets), LogColor.ENEMY_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("VAMPIRE_ATTACK", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        Skill VampAttack = new Skill(
                "Vampire Attack", "Attacks enemy",0, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                vampAttackLogic
        );

        skills.add(VampAttack);
    }

    //    TODO: replace this with randomly use skill function
    @Override
    public void makeAttack(List<Character> targets, Runnable onSkillComplete) {
        // TODO: randomly select skill
        // TODO: add AI that checks if skill has enough mana or not
        Skill skill = Dice.pickRandom(skills);
        skill.execute(this, targets, onSkillComplete);
    }

}

