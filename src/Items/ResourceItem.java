package Items;

import Characters.Character;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogManager;
import java.util.List;

import java.util.List;

/**
 * Basically just a utility item but better character hp/mp handling
 */
public class ResourceItem extends Item {
    private final boolean restoresHP;
    private final boolean restoresMP;

    public ResourceItem(String name, String description, ItemConsumer executeLogic, Rarity rarity, boolean restoresHp, boolean restoresMp, TargetType targetType) {

        super(name, description, targetType, TargetCondition.ALIVE, executeLogic, rarity);
        this.restoresHP = restoresHp;
        this.restoresMP = restoresMp;
    }

    @Override
    public void use(Character user, List<Character> targets, Runnable onItemComplete) {
        double multiplier = getRarityMultiplier(this.getRarity());

        for (Character target : targets) {

            // --- HP LOGIC ---
            if (this.restoresHP) {
                // Calculate percentage of Max HP
                int amount = (int) (target.getMaxHealth() * multiplier);

                // Always heal at least 1 if the percentage is tiny
                if (amount < 1) amount = 1;

                target.receiveHealing(amount, user);

                // Optional: Log the percentage for debugging
                // LogManager.log("Healed " + amount + " (" + (int)(multiplier*100) + "%)");
            }

            // --- MP LOGIC ---
            if (this.restoresMP) {
                // Calculate percentage of Max MP
                int amount = (int) (target.getMaxMana() * multiplier);

                if (amount < 1) amount = 1;

                target.receiveMana(amount, user);
            }
        }

        if (onItemComplete != null) onItemComplete.run();
    }

    private double getRarityMultiplier(Rarity rarity) {
        switch (rarity) {
            case COMMON:    return 0.10; // 10%
            case UNCOMMON:  return 0.25; // 25%
            case RARE:      return 0.40; // 40%
            case SUPERIOR:  return 0.50; // 50%
            case EPIC:      return 0.75; // 75%
            case LEGENDARY: return 0.80; // 80%
            case MYTHIC:    return 1.00; // 100%
            default:        return 0.10; // Fallback
        }
    }
}

