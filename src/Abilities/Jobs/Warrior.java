package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.ScalingLogic;
import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Warrior extends JobClass {

    public static final String description = "A battle-hardened fighter clad in steel, the Warrior thrives on the frontlines. With unmatched strength and resilience, he shields allies from harm while delivering crushing blows to enemies. His loyalty and courage make him the backbone of any party.";
    public static final String IDLE_PATH = "Assets/Animations/Heroes/Warrior/Idle/sprite_%d.png";
    //TODO: figure out a way to make it idle

    public Warrior() {
        super("Warrior", description, 10, 0);
        AssetManager.getInstance().registerAnimation(
                "WARRIOR_IDLE",
                IDLE_PATH,
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "WARRIOR_SWORD-SLASH",
                "Assets/Animations/Heroes/Warrior/Effects/Sword_Slash/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "WARRIOR_SHIELD-BASH",
                "Assets/Animations/Heroes/Warrior/Effects/Shield_Bash/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    @Override
    public List<ReactionSkill> createReactions() {
        return List.of();
    }

    public List<Skill> createSkills() {
        SkillLogicConsumer shieldBashLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,20,10,1.2,0.05);
            Character target = targets.get(0);
            target.takeDamage(calculateDamage, user, self);
            LogManager.log(self.getActionLog(user, " Bashes Shield on ", targets), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("WARRIOR_SHIELD-BASH", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
            }, true);
        };

        SkillLogicConsumer swordSlashLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = ScalingLogic.calculateDamage(user,30,15,1.2,0.05);
            Character target = targets.get(0);
            target.takeDamage(calculateDamage, user, self);
            LogManager.log(self.getActionLog(user, " Slashes on ", targets), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().hideCharacterVisual(user);
            VisualEffectsManager.getInstance().playAnimationOnCharacter("WARRIOR_SWORD-SLASH", target, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                    VisualEffectsManager.getInstance().restoreCharacterVisual(user);
                }
            }, true);
        };

        Skill ShieldBash = new Skill(
                "Shield Bash", "Single-target Shield attack", 15, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                shieldBashLogic
        );

        Skill SwordSlash = new Skill(
                "Sword Slash", "Single-target Sword Slash", 20, 30,
                SkillType.DAMAGE, SkillAction.PHYSICAL, TargetType.SINGLE_TARGET, TargetCondition.ALIVE,
                swordSlashLogic
        );
        return List.of(ShieldBash,SwordSlash);
    }
    @Override public String getPreviewImagePath() { return IDLE_PATH; }
}