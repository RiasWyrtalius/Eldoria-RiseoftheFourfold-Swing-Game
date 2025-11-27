package Core.Utils;

import Characters.Character;

/**
 * This is some genius stuff sam O_o
 */
public class ScalingLogic {
    /**
     * Calculates user damage based on flurgerburger's standard formula:
     * Dmg = Sb + Sp + (L * Sf) + (Sb * (L * Lm))
     *
     * See scaling graph here: https://www.desmos.com/calculator/0i9agtdgsk
     *
     * @param user            The character using the skill (provides Level).
     * @param skillBase       (Sb) Inherent base damage to the skill.
     * @param skillPower      (Sp) Added power (Specific value passed manually).
     * @param scalingFactor   (Sf) Flat bonus damage per level.
     * @param levelMultiplier (Lm) Percentage growth of Base Damage per level.
     * @return                  The final calculated integer damage
     */
    public static int calculateDamage(Character user, int skillBase, int skillPower, double scalingFactor, double levelMultiplier) {
        int level = user.getLevel();

        // (L * Sf) -> Flat bonus based on level
        double flatLevelBonus = level * scalingFactor;

        // (Sb * (L * Lm)) percentage scaling based on dat level
        // base 20 * (Level 5 * 0.05) = 20 * 0.25 = 5 extra damage
        double percentageLevelBonus = skillBase * (level * levelMultiplier);

        double totalDamage = skillBase + skillPower + flatLevelBonus + percentageLevelBonus;

        return (int) totalDamage;
    }

    /**
     * Uses the Character's own Base Attack (getBaseAtk) as the Skill Power (Sp)
     * @param user
     * @param skillBase
     * @param scalingFactor
     * @param levelMultiplier
     * @return
     */
    public static int calculateDamage(Character user, int skillBase, double scalingFactor, double levelMultiplier) {
        return calculateDamage(user, skillBase, user.getBaseAtk(), scalingFactor, levelMultiplier);
    }

    /**
     * Calculates Max HP or Max MP based on:
     * Stat = Base + ((L - 1) * Flat) + (Base * Growth * (L - 1))
     *
     * @param level      (L) Current Level
     * @param baseVal    (Bh or Bm) Base Stat at Level 1
     * @param flatGain   (Hf or Mf) Flat gain per level
     * @param growthRate (Hg or Mg) Percent growth per level (e.g., 0.10 for 10%)
     */
    public static int calculateStat(int level, int baseVal, int flatGain, double growthRate) {
        if (level <= 1) return baseVal;

        int levelIndex = level - 1; // The formula relies on (L - 1)

        // (L - 1) * Flat
        double flatBonus = levelIndex * flatGain;

        // Base * Growth * (L - 1)
        double growthBonus = baseVal * growthRate * levelIndex;

        return (int) (baseVal + flatBonus + growthBonus);
    }
}
