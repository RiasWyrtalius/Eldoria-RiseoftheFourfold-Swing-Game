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

public class Spider extends Enemy {
    public Spider() {
        this(1);
    }

    public Spider(int level) {
        super(
                "Demon Spider",
                ScalingLogic.calculateStat(level,20, 20, 0.1),
                ScalingLogic.calculateStat(level,10, 10, 0.05),
                0,
                level,
                "Slime",
                50 * level,
                "Best web designer around",
                "SPIDER_IDLE");
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "SPIDER_IDLE",
                "Assets/Animations/Enemies/Spider/Idle/sprite_%d.png",
                3, 100, 100 , 280,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "SPIDER_VENOM",
                "Assets/Animations/Enemies/Spider/Effects/Spider_Venom/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    // TODO: fix the SINGLE_TARGETING logic on this
    @Override
    protected void initializeSkills() {
        SkillLogicConsumer spiderVenomLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

//            int index = Dice.getInstance().roll(0, targets.size());
            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, "uses", targets), LogFormat.ENEMY_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("SPIDER_VENOM", target, () -> {
                target.receiveDamage(calculateDamage, user, self, () -> {
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    if (onSkillComplete != null) onSkillComplete.run();
                });

            }, true);
        };

        Skill SpiderVenom = new Skill(
                "Spider Venom", "Spits out venom out of its mouth",0, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                spiderVenomLogic
        );

        skills.add(SpiderVenom);
    }

    //    TODO: replace this with randomly use skill function
    @Override
    public void makeAttack(BattleController controller, List<Character> targets, Runnable onSkillComplete) {
        // TODO: randomly select skill
        // TODO: add AI that checks if skill has enough mana or not
        Skill skill = Dice.getInstance().pickRandom(skills);
        skill.execute(controller, this, targets, onSkillComplete);
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
    }
}

