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

public class Slime extends Enemy {
    public Slime() {
        this(1);
    }

    public Slime(int level) {
        super(
                "Demon Slime",
                ScalingLogic.calculateStat(level,30, 15, 0.1),
                ScalingLogic.calculateStat(level,30, 2, 0.2),
                0,
                "Slime",
                10 * level,
                "Slimes around", "SLIME_IDLE");
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer acidicSlamLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, "uses", targets), LogFormat.ENEMY_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("SLIME_ACIDIC-SLAM", target, () -> {
                target.receiveDamage(calculateDamage, user, self);
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        Skill AcidicSlam = new Skill(
                "Acidic Slam", "Bashes its own body",0, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                acidicSlamLogic
        );

        skills.add(AcidicSlam);
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
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "SLIME_IDLE",
                "Assets/Animations/Enemies/Slime/Idle/sprite_%d.png",
                3, 100, 100 , 280,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "SLIME_ACIDIC-SLAM",
                "Assets/Animations/Enemies/Slime/Effects/Acidic_Slam/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
    }
}

