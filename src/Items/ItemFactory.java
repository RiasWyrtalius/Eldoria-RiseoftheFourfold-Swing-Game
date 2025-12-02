package Items;

import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Visuals.VisualEffectsManager;

// TODO: Item should not be recreated many times. just once and use a copy, and the quantity
// TODO: make this into a flyweight
public class ItemFactory {
    public static Item smallHealthPotion() {
        return new ResourceItem(
                "Small Health Potion",
                "Restores 50 HP to a selected target",
                null,
                Rarity.COMMON,
                50, 0,
                TargetType.SINGLE_TARGET
        );
    }
    public static Item smallManaPotion() {
        return new ResourceItem(
                "Small Mana Potion",
                "Restores 25 MP to a selected target",
                null,
                Rarity.COMMON,
                0, 25,
                TargetType.SINGLE_TARGET
        );
    }
    public static Item splashSmallHealthPotion() {
        return new ResourceItem(
                "Splash Small Health Potion",
                "Restores 50 HP to a selected targets",
                null,
                Rarity.UNCOMMON,
                30, 0,
                TargetType.AOE_TWO_TARGETS
        );
    }
    public static Item splashSmallManaPotion() {
        return new ResourceItem(
                "Splash Small Mana Potion",
                "Restores 10 MP to a selected targets",
                null,
                Rarity.UNCOMMON,
                0, 10,
                TargetType.AOE_TWO_TARGETS
        );
    }

    public static Item mediumHealthPotion() {
        return new ResourceItem(
                "Medium Health Potion",
                "Restores 80 HP to a selected target",
                null,
                Rarity.RARE,
                80, 0,
                TargetType.SINGLE_TARGET
        );
    }
    public static Item mediumManaPotion() {
        return new ResourceItem(
                "Medium Mana Potion",
                "Restores 40 MP to a selected target",
                null,
                Rarity.RARE,
                0, 40,
                TargetType.SINGLE_TARGET
        );
    }

    public static Item revivePotion() {
        return new UtilityItem(
                "Revive Potion",
                "Revive and restore 20HP to a selected target",
                TargetType.SINGLE_TARGET,
                TargetCondition.DEAD,
                null,
                Rarity.EPIC,
                (item, user, targets, onItemComplete) -> {
                    VisualEffectsManager.getInstance().reviveEffect(targets.getFirst());
                    int hp = (int)(targets.getFirst().getInitialHealth() * .20);
                    targets.getFirst().setHealth(hp);
                }
        );
    }
}
