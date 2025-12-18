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

    //TODO: add defense field to Boss class
    public Varoth(int level){
        this(   // HP / ATK / DEF / MANA
                "Varoth",
                ScalingLogic.calculateStat(level,500,20,0.1),
                ScalingLogic.calculateStat(level,25,15,0.05),
                ScalingLogic.calculateStat(level,25,5,0.1),
                ScalingLogic.calculateStat(level,400,15,0.1),
                level,"Boss"
                ,ScalingLogic.calculateStat(level,200,30,0.09),
                1.2
        );
    }
    public Varoth(String name, int initialHealth, int baseAtk, int baseDefense, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(
                name, initialHealth, baseAtk, baseDefense, maxMana, level, type, rewardXP,  healthMultiplier, "VAROTH-IDLE", "Demon King");
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "VAROTH-IDLE",
                "Assets/Animations/Enemies/Boss/Varoth/Idle/sprite_%d.png",
                3, 250, 250 , 320,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "VOID_SLASH",
                "Assets/Animations/Enemies/Boss/Varoth/Effects/Basic_Attack/sprite_%d.png",
                4, 200, 200 , 200,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "CONSUME",
                "Assets/Animations/Enemies/Boss/Varoth/Effects/Ultimate/sprite_%d.png",
                7, 200, 200 , 200,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "VAROTH_AOE",
                "Assets/Animations/Enemies/Boss/Varoth/Effects/AOE_Attack/sprite_%d.png",
                5, 200, 200 , 200,
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
                VisualEffectsManager.getInstance().hideCharacterVisual(user);
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

            LogManager.log(self.getActionLog(user, "unleashes a Void Slash on", targets), LogFormat.ENEMY_ACTION);
            Character target = targets.getFirst();
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("VOID_SLASH", target, () -> {
                        target.receiveDamage(calculateDamage, user, self, onSkillComplete);
                        if (onSkillComplete != null) onSkillComplete.run();
                        VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                        }, true);
        };

        Skill devastatingStrike = new Skill(
                "Void Slash", "Attacks using the void damage", 50, 0,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                devastatingStrikeLogic
        );
        SkillLogicConsumer aoeLogic = (controller, self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculatePhysicalDamage(user,(int)(baseAtk * 1.25),0.2,0.2);

            LogManager.log(self.getActionLog(user, "unleashes a Void Blast on", targets), LogFormat.ENEMY_ACTION);
            for(Character target : targets) {
                VisualEffectsManager.getInstance().playAnimationOnCharacter("VAROTH_AOE", target, () -> {
                    target.receiveDamage(calculateDamage, user, self, onSkillComplete);
                    if (onSkillComplete != null) onSkillComplete.run();
                }, true);
            }
        };

        Skill AOE = new Skill(
                "Void Blast", "Attacks multiple enemies", 50, 0,
                SkillType.DAMAGE, SkillAction.MAGICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                aoeLogic
        );

        SkillLogicConsumer doNothingLogic = (controller, self, user, targets, onSkillComplete) -> {

        };

        Skill DoNothing = new Skill(
                "Nothing", "Does nothing", 0, 0,
                SkillType.UTILITY, SkillAction.PHYSICAL, TargetType.NO_TARGETS, TargetCondition.ALIVE,
                doNothingLogic
        );

        skills.add(basicAttack);
        skills.add(devastatingStrike);
        skills.add(AOE);
        skills.add(DoNothing);
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
        Skill AOE = skills.get(2);

        boolean useAOE = (this.getMana() >= devastatingStrike.getManaCost()) && (Math.random() < 0.4);

        if (this.getMana() >= devastatingStrike.getManaCost()) {
            devastatingStrike.execute(controller, this, targets, onSkillComplete);
        } else if (useAOE) {
            AOE.execute(controller, this, targets, onSkillComplete);
        }else {
            basicAttack.execute(controller, this, targets, onSkillComplete);
        }
    }
}