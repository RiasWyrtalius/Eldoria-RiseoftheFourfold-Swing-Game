package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;
import java.util.function.BiConsumer;

public class Warrior extends JobClass {

    public Warrior() {
        super("Warrior", "Wields Sword and Shield", 10, 0);
        AssetManager.getInstance().registerAnimation(
                "WARRIOR_IDLE",
                "Assets/Animations/Warrior/Idle/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
        AssetManager.getInstance().registerAnimation(
                "WARRIOR_SWORD-SLASH",
                "Assets/Animations/Warrior/Effects/Sword_Slash/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
        AssetManager.getInstance().registerAnimation(
                "WARRIOR_SHIELD-BASH",
                "Assets/Animations/Warrior/Effects/Shield_Bash/sprite_%d.png",
                3, 100, 100 , 300,
                AnimationLoopType.ONE_CYCLE
        );
    }

    public List<Skill> createSkills() {
        FullExecuteConsumer shieldBashLogic = (skill, user, targets, onSkillComplete) -> {
            int calculateDamage = (user.getBaseAtk() * 3);
            Character target = targets.getFirst();
            LogManager.log(skill.getActionLog(user, "Bashes Shield at", targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("WARRIOR_SHIELD-BASH", user, () -> {
                target.takeDamage(calculateDamage, user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        FullExecuteConsumer swordSlashLogic = (skill, user, targets, onSkillComplete) -> {
            int calculateDamage = (user.getBaseAtk() * 3);
            Character target = targets.getFirst();
            LogManager.log(skill.getActionLog(user, "Slashes Sword at", targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("WARRIOR_SWORD-SLASH", user, () -> {
                target.takeDamage(calculateDamage, user);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        Skill ShieldBash = new Skill(
                "Shield Bash", "Single-target Shield attack", 15, 20,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                shieldBashLogic
        );

        Skill SwordSlash = new Skill(
                "Sword Slash", "Single-target Sword Slash", 20, 30,
                SkillType.DAMAGE, SkillAction.PHYSICAL, SkillTarget.SINGLE_TARGET,
                swordSlashLogic
        );
        return List.of(ShieldBash,SwordSlash);
    }
}