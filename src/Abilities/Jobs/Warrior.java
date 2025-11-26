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
                "Assets/Animations/Heroes/Warrior/Idle/sprite_%d.png",
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

    public List<Skill> createSkills() {
        FullExecuteConsumer shieldBashLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(20 + 10 + (user.getLevel() * 1.2) + (20 * (user.getLevel() * 0.05)));
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user, self);
            LogManager.log(self.getActionLog(user, " Bashes Shield on ", targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("WARRIOR_SHIELD-BASH", user, () -> {
                target.takeDamage(calculateDamage, user, self);
                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);
        };

        FullExecuteConsumer swordSlashLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(30 + 15 + (user.getLevel() * 1.2) + (30 * (user.getLevel() * 0.05)));
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user, self);
            LogManager.log(self.getActionLog(user, " Slashes on ", targets, calculateDamage), LogColor.HERO_ACTION);
            VisualEffectsManager.getInstance().playAnimation("WARRIOR_SWORD-SLASH", target, () -> {
                target.takeDamage(calculateDamage, user, self);
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