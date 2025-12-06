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

public class Vampire extends Enemy {
    public Vampire() {
        this(1);
    }

    public Vampire(int level) {
        super(
                "Vampire",
                ScalingLogic.calculateStat(level,100, 10, 0.05),
                ScalingLogic.calculateStat(level,20, 10, 0.1),
                0,
                level,
                "Vampire",
                50 * level,
                "Blood Thirsty",
                "VAMPIRE_IDLE");
    }

    @Override
    protected void registerAssets() {
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
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer vampAttackLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, "Attacks", targets), LogFormat.ENEMY_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("VAMPIRE_ATTACK", target, () -> {
                target.receiveDamage(calculateDamage, user, self);
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
        Skill skill = Dice.getInstance().pickRandom(skills);
        skill.execute(this, targets, onSkillComplete);
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
    }
}

