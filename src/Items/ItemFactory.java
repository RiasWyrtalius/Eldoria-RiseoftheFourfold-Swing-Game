package Items;

import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Visuals.VisualEffectsManager;

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

    public static Item revivePotion() {
        return new UtilityItem(
                "Revive Potion",
                "Revive and restore 20HP to a selected target",
                TargetType.SINGLE_TARGET,
                TargetCondition.DEAD,
                null,
                Rarity.RARE,
                (item, user, targets, onItemComplete) -> {
                    VisualEffectsManager.getInstance().reviveEffect(targets.get(0));
                    targets.get(0).setHealth(20);
                }
        );
    }
}
