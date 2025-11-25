package Abilities.Jobs;

import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;

import java.util.List;
import java.util.function.BiConsumer;

public class Warrior extends JobClass {

    public Warrior() {
        super("Warrior", "Wields Sword and Shield", 10, 0);
    }

    public List<Skill> createSkills() {
        FullExecuteConsumer shieldBashLogic = (skill, user, targets, onSkillComplete) -> {
            int calculateDamage = (user.getBaseAtk() * 2);
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user);
            LogManager.log(skill.getActionLog(user, " Bashes Shield on ", targets, calculateDamage), LogColor.HERO_ACTION);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };

        FullExecuteConsumer swordSlashLogic = (skill, user, targets, onSkillComplete) -> {
            int calculateDamage = (user.getBaseAtk() * 2);
            Character target = targets.getFirst();
            target.takeDamage(calculateDamage, user);
            LogManager.log(skill.getActionLog(user, " Slashes on ", targets, calculateDamage), LogColor.HERO_ACTION);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
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