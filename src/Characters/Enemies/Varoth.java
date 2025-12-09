package Characters.Enemies;

import Abilities.*;
import Characters.Base.Boss;
import Characters.Character;
import Core.Battle.BattleController;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Utils.ScalingLogic;
import Core.Visuals.VisualEffectsManager;
import Resource.Animation.AnimationLoopType;
import Resource.Animation.AssetManager;

import java.util.List;

public class Varoth extends Boss {
    public Varoth(){this(1);}

    public Varoth(int level){
        this(
                "Baby Dragon",
                ScalingLogic.calculateStat(level,500,20,0.1),
                ScalingLogic.calculateStat(level,50,15,0.05),
                ScalingLogic.calculateStat(level,400,50,0.2),
                level,"Boss"
                ,ScalingLogic.calculateStat(level,500,30,0.09),
                (int)(1.6));
    }
    public Varoth(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(
                name, initialHealth, baseAtk, maxMana, level, type, rewardXP,  healthMultiplier, "VAROTH-IDLE", "Demon King");
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "VAROTH-IDLE",
                "Assets/Animations/Enemies/Boss/Varoth/Idlle/sprite_%d.png",
                3, 100, 100 , 320,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "VOID-SLASH",
                "Assets/Animations/Enemies/Boss/Varoth/Effects/Basic_Attack/sprite_%d.png",
                4, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "CONSUME",
                "Assets/Animations/Enemies/Boss/Varoth/Effects/Ultimate/sprite_%d.png",
                7, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer basicAttackLogic = (controller,self, user, targets, onSkillComplete) -> {
            Character weakTarget = null;
            int lowHP = Integer.MAX_VALUE;

            for (Character c : targets) {
                if (c.getHealth() > 0 && c.getHealth() < lowHP) {
                    lowHP = c.getHealth();
                    weakTarget = c;
                }
            }

            if (weakTarget == null && !targets.isEmpty()) {
                weakTarget = targets.getFirst();
            }

            if (weakTarget != null) {
                int calculateDamage = ScalingLogic.calculatePhysicalDamage(user,(int)(baseAtk * 1.5),0.4,0.1);
                LogManager.log(self.getActionLog(user, "focuses on and strikes", List.of(weakTarget)), LogFormat.ENEMY_ACTION);
                Character target = weakTarget;
                VisualEffectsManager.getInstance().playAnimationOnCharacter("CONSUME", target, () -> {
                    target.receiveDamage(calculateDamage, user, self, onSkillComplete);
                    if (onSkillComplete != null) onSkillComplete.run();
                }, true);
            }

        };

        Skill basicAttack = new Skill(
                "CONSUME", "Attacks the weakest target", 0, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                basicAttackLogic
        );

        SkillLogicConsumer devastatingStrikeLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculatePhysicalDamage(user,(int)(baseAtk * 1.25),0.2,0.2);

            LogManager.log(self.getActionLog(user, "unleashes a DEVASTATING STRIKE on", targets), LogFormat.ENEMY_ACTION);

            for (Character target : targets) {
                if (target.getHealth() > 0) {
                    VisualEffectsManager.getInstance().playAnimationOnCharacter("VOID_SLASH", target, () -> {
                        target.receiveDamage(calculateDamage, user, self, onSkillComplete);
                        if (onSkillComplete != null) onSkillComplete.run();
                    }, true);
                }
            }
        };

        Skill devastatingStrike = new Skill(
                "Void Slash", "Attacks using the void damage", 50, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.AOE_ALL_TARGETS, TargetCondition.ALIVE,
                devastatingStrikeLogic
        );

        skills.add(basicAttack);
        skills.add(devastatingStrike);
    }

    // TODO: character death animation
    @Override
    protected void onDeath() {
        super.onDeath();
//        VisualEffectsManager.getInstance().pauseAnimation("DRAGON_BOSS-IDLE");
//        VisualEffectsManager.getInstance().hideCharacterVisual(this);
    }

    @Override
    public List<Skill> getSkills() {
        return List.of();
    }

    @Override
    public void makeAttack(BattleController controller, List<Character> targets, Runnable onSkillComplete) {
        Skill basicAttack = skills.get(0);
        Skill devastatingStrike = skills.get(1);

        if (this.getMana() >= devastatingStrike.getManaCost()) {
            devastatingStrike.execute(controller, this, targets, onSkillComplete);
        } else {
            basicAttack.execute(controller, this, targets, onSkillComplete);
        }
    }
}