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

public class Skull extends Enemy {
    public Skull() {
        this(1);
    }

    public Skull(int level) {
        super(
                "Skeletal Skull",
                ScalingLogic.calculateStat(level, 60, 20, 0.10),
                ScalingLogic.calculateStat(level, 20, 10, 0.05),
                0,
                level,
                "Skull",
                15 * level,
                "Cursed Bone",
                "SKULL_IDLE"
        );

        registerAssets();
        initializeSkills();
        initializeReactions();
    }

    @Override
    protected void registerAssets() {
        AssetManager.getInstance().registerAnimation(
                "SKULL_IDLE",
                "Assets/Animations/Enemies/Skull/Idle/sprite_%d.png",
                4,
                100, 100, 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "SKULL_ATTACK",
                "Assets/Animations/Enemies/Skull/Effects/Attack/sprite_%d.png",
                6,
                100, 100, 100,
                AnimationLoopType.ONE_CYCLE
        );

//        AssetManager.getInstance().registerAnimation(
//                "SKULL_DEATH",
//                "Assets/Animations/Enemies/Skull/Death/sprite_%d.png",
//                4,
//                100, 100, 300,
//                AnimationLoopType.ONE_CYCLE
//        );
    }

    @Override
    protected void initializeSkills() {
        SkillLogicConsumer fireBreath = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user, baseAtk, 0.2, 0.1);

            Character target = Dice.getInstance().pickRandom(targets);
            LogManager.log(self.getName() + " attacks " + target.getName() + "!", LogFormat.ENEMY_ACTION);

//            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimation("SKULL_ATTACK", user, () -> {
                // i like this
                try {
                    target.receiveDamage(calculateDamage, user, self);
                } finally {
//                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                    if (onSkillComplete != null) onSkillComplete.run();
                }
            }, true);
        };

//        SkillLogicConsumer throwCoinsLogic = (self, user, targets, onSkillComplete) -> {
//            int coins = Dice.getInstance().roll(1, 5);
//            int calculateDamage = ScalingLogic.calculateDamage(user, baseAtk, 0.2, 0.1) * coins;
//
//            Character target = Dice.getInstance().pickRandom(targets);
//            LogManager.log(self.getName() + " throws " + coins + " coins at " + target.getName() + "!", LogFormat.ENEMY_ACTION);
//
//            // no hide/play animation here, but always restore (safe)
//            target.receiveDamage(calculateDamage, user, self);
//            VisualEffectsManager.getInstance().restoreCharacterVisual(user);
//            if (onSkillComplete != null) onSkillComplete.run();
//        };

        Skill FireBreath = new Skill(
                "Fire Breath", "Breathes fire", 0, 15,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                fireBreath
        );

//        Skill throwCoin = new Skill(
//                "Coin Throw", "Fling Coins", 0, 10,
//                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
//                throwCoinsLogic
//        );

        if (skills == null) {
            skills = new java.util.ArrayList<>();
        }

        skills.clear();
        skills.add(FireBreath);
//        skills.add(throwCoin);
    }

    @Override
    public List<Skill> getSkills() {
        return skills == null ? List.of() : List.copyOf(skills);
    }

    //    TODO: replace this with randomly use skill function
    @Override
    public void makeAttack(List<Character> targets, Runnable onSkillComplete) {
        // TODO: randomly select skill
        // TODO: add AI that checks if skill has enough mana or not
        Skill skill = Dice.getInstance().pickRandom(skills);
        skill.execute(this, targets, onSkillComplete);
    }

    protected void initializeReactions() {
        return;
    }
}
