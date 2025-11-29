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

public class Spider extends Enemy {
    public Spider() {
        this(1);
    }

    public Spider(int level) {
        super("Demon Spider", 500, 30, 0, 1, "Slime", 10, "SPIDER_IDLE", "Best web designer around");
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

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer spiderVenomLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getActionLog(user, "uses", targets), LogColor.ENEMY_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("SPIDER_VENOM", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
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
    public void makeAttack(List<Character> targets, Runnable onSkillComplete) {
        // TODO: randomly select skill
        // TODO: add AI that checks if skill has enough mana or not
        Skill skill = Dice.getInstance().pickRandom(skills);
        skill.execute(this, targets, onSkillComplete);
    }

}

