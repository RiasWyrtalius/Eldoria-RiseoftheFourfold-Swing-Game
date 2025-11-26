package Characters.Enemies;

import Abilities.*;
import Characters.Base.Enemy;
import Characters.Character;
import Core.Utils.Dice;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualAsset;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Slime extends Enemy {
    public Slime() {

        super("Demon Slime", 500, 10, 0, 1, "Slime", 10, "SLIME_IDLE", "Slimes around");
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
            LogManager.log(self.getActionLog(user, "uses", targets), LogColor.ENEMY_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("SLIME_ACIDIC-SLAM", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                VisualEffectsManager.getInstance().restoreCharacterVisual(user);
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

