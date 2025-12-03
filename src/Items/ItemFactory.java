package Items;

import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Visuals.VisualEffectsManager;

// flyweight class
public class ItemFactory {
    private static final Item SMALL_HEALTH_POTION = new ResourceItem(
            "Small Health Potion",
            "Restores 50 HP to a selected target",
            null,
            Rarity.COMMON,
            50, 0,
            TargetType.SINGLE_TARGET
    );

    private static final Item SMALL_MANA_POTION = new ResourceItem(
            "Small Mana Potion",
            "Restores 25 MP to a selected target",
            null,
            Rarity.COMMON,
            0, 25,
            TargetType.SINGLE_TARGET
    );

    private static final Item SPLASH_HEALTH_POTION = new ResourceItem(
            "Splash Small Health Potion",
            "Restores 30 HP to a selected targets",
            null,
            Rarity.UNCOMMON,
            30, 0,
            TargetType.AOE_TWO_TARGETS
        );

    private static final Item SPLASH_MANA_POTION = new ResourceItem(
                "Splash Small Mana Potion",
                "Restores 10 MP to a selected targets",
                null,
                Rarity.UNCOMMON,
                0, 10,
                TargetType.AOE_TWO_TARGETS
        );

    private static final Item MEDIUM_HEALTH_POTION = new ResourceItem(
                "Medium Health Potion",
                "Restores 80 HP to a selected target",
                null,
                Rarity.RARE,
                80, 0,
                TargetType.SINGLE_TARGET
        );

    private static final Item MEDIUM_MANA_POTION = new ResourceItem(
                "Medium Mana Potion",
                "Restores 40 MP to a selected target",
                null,
                Rarity.RARE,
                0, 40,
                TargetType.SINGLE_TARGET
        );

    private static final Item REVIVE_POTION = new UtilityItem(
                "Revive Potion",
                "Revive and restore 20HP to a selected target",
                TargetType.SINGLE_TARGET,
                TargetCondition.DEAD,
                null,
                Rarity.EPIC,
                (item, user, targets, onItemComplete) -> {
                    VisualEffectsManager.getInstance().reviveEffect(targets.getFirst());
                    int hp = (int)(targets.getFirst().getInitialHealth() * .20);
                    targets.getFirst().revive(hp, user);
                }
        );

    public static Item smallHealthPotion() {
        return SMALL_HEALTH_POTION;
    }

    public static Item smallManaPotion() {
        return SMALL_MANA_POTION;
    }

    public static Item splashHealthPotion() {
        return SPLASH_HEALTH_POTION;
    }

    public static Item splashManaPotion() {
        return SPLASH_MANA_POTION;
    }

    public static Item mediumHealthPotion() {
        return MEDIUM_HEALTH_POTION;
    }

    public static Item getMediumManaPotion() {
        return MEDIUM_MANA_POTION;
    }

    public static Item revivePotion() {
        return REVIVE_POTION;
    }

}
