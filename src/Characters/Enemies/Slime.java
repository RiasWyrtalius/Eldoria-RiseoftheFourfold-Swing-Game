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

public class Slime extends Enemy {
    public Slime() {

        super("Demon Slime", 500, 10, 0, 1, "Slime", 10, "SLIME_IDLE");
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
//        initializeReactions();
    }

    @Override
    protected void initializeSkills() {
        FullExecuteConsumer acidicSlamLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = user.getBaseAtk();

            Character target = Dice.pickRandom(targets);
            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.ENEMY_ACTION);
            // TODO: panel should be empty during the swinging
            VisualEffectsManager.getInstance().playAnimationOnCharacter("SLIME_ACIDIC-SLAM", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        Skill AcidicSlam = new Skill(
                "Acidic Slam", "Bashes its own body",0, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                acidicSlamLogic
        );

        skills.add(AcidicSlam);
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

