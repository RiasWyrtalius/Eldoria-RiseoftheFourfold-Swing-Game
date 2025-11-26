package Core.Utils;

import Characters.Character;

public class CombatMath {
    /**
     * Calculates user damage based on flurgerburger's standard formula:
     * Dmg = Sb + Sp + (L * Sf) + (Sb * (L * Lm))
     *
     * See scaling graph here: https://www.desmos.com/calculator/0i9agtdgsk
     *
     * @param user
     * @param skillBase         (Sb) inherit damage of skill
     * @param skillPower        (Sp) Added power (usually derived from stats like Atk
     * @param scalingFactor     (Sf) Flat damage bonus per level
     * @param levelMultiplier   (Lm) Percentage growth of Base Damage per level
     * @return                  The final calculated integer damage
     */
    public static int calculateDamage(Character user, int skillBase, int skillPower, double scalingFactor, double levelMultiplier) {
        int level = user.getLevel();
        // (L * Sf)
        double flatLevelBonus = level * scalingFactor;

        // (Sb * (L * Lm)) - e.g., 20 * (5 * 0.05)
        double percentageLevelBonus = skillBase * (level * levelMultiplier);

        double totalDamage = skillBase + skillPower + flatLevelBonus + percentageLevelBonus;

        return (int) totalDamage;
    }

    /**
     * same as above but for cases where skill power is just base attack.
     * @param user
     * @param skillBase
     * @param scalingFactor
     * @param levelMultiplier
     * @return
     */
    public static int calculateDamage(Character user, int skillBase, double scalingFactor, double levelMultiplier) {
        return calculateDamage(user, skillBase, user.getBaseAtk(), scalingFactor, levelMultiplier);
    }
}
