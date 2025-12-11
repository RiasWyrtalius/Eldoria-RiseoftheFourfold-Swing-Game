package Items;

import Characters.Base.Hero;
import Characters.Party;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.GameFlow.CharacterSelectionMode;
import Core.GameManager;
import Core.Utils.LogManager;
import Core.Visuals.VisualEffectsManager;
import UI.SceneManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

// flyweight class
public class ItemFactory {
    private static final Map<String, Item> itemRegistry = new HashMap<>();

    static {
        // --- COMMON (10%) ---
        register(new ResourceItem(
                "Small Health Potion",
                "Restores 10% HP to a selected target",
                null,
                Rarity.COMMON,
                true, false,
                TargetType.SINGLE_TARGET
        ));

        register(new ResourceItem(
                "Small Mana Potion",
                "Restores 10% MP to a selected target",
                null,
                Rarity.COMMON,
                false, true,
                TargetType.SINGLE_TARGET
        ));

        // --- UNCOMMON (25%) ---
        register(new ResourceItem(
                "Splash Small Health Potion",
                "Restores 25% HP to selected targets",
                null,
                Rarity.UNCOMMON,
                true, false,
                TargetType.AOE_TWO_TARGETS
        ));

        register(new ResourceItem(
                "Splash Small Mana Potion",
                "Restores 25% MP to selected targets",
                null,
                Rarity.UNCOMMON,
                false, true,
                TargetType.AOE_TWO_TARGETS
        ));

        // --- RARE (40%) ---
        register(new ResourceItem(
                "Medium Health Potion",
                "Restores 40% HP to a selected target",
                null,
                Rarity.RARE,
                true, false,
                TargetType.SINGLE_TARGET
        ));

        register(new ResourceItem(
                "Medium Mana Potion",
                "Restores 40% MP to a selected target",
                null,
                Rarity.RARE,
                false, true,
                TargetType.SINGLE_TARGET
        ));

        register(new ResourceItem(
                "Splash Medium Health Potion",
                "Restores 40% HP to a selected target",
                null,
                Rarity.RARE,
                true, false,
                TargetType.AOE_TWO_TARGETS
        ));

        register(new ResourceItem(
                "Spalsh Medium Mana Potion",
                "Restores 40% MP to a selected target",
                null,
                Rarity.RARE,
                false, true,
                TargetType.AOE_TWO_TARGETS
        ));

        // --- UTILITY ITEMS ---
        register(new UtilityItem(
                "Revive Potion",
                "Revive and restore 20% HP to a selected target",
                TargetType.SINGLE_TARGET,
                TargetCondition.DEAD,
                null,
                Rarity.EPIC,
                (item, user, targets, onItemComplete) -> {
                    if (!targets.isEmpty()) {
                        VisualEffectsManager.getInstance().reviveEffect(targets.getFirst());
                        int hp = (int)(targets.getFirst().getMaxHealth() * .20);
                        targets.getFirst().revive(hp, user);
                    }
                    if (onItemComplete != null) onItemComplete.run();
                }
        ));

        register(new UtilityItem(
                "Summoning Scroll",
                "Summon a party member of your choice",
                TargetType.NO_TARGETS,
                TargetCondition.ANY,
                null,
                Rarity.LEGENDARY,
                (item, user, targets, onItemComplete) -> {
                    BiConsumer<Hero, String> onCharacterPicked = (selectedHero, partyName) -> {
                        Party heroParty = GameManager.getInstance().getHeroParty();
                        if (heroParty.getPartyMembers().size() < 4) {
                            heroParty.addPartyMember(selectedHero);
                        } else {
                            LogManager.log("Party is full! " + selectedHero.getName() + " could not join.");
                        }
                        SceneManager.getInstance().closeAllOverlays();
                        // Signal item completion after selection is done
                        if (onItemComplete != null) onItemComplete.run();
                    };
                    GameManager.getInstance().showCharacterSelectionScreen(CharacterSelectionMode.ADD_TO_EXISTING_PARTY, onCharacterPicked);
                }
        ));
    }

    public static void register(Item item) {
        itemRegistry.put(item.getName(), item);
    }

    public static Item getItemByName(String name) {
        if (name == null) return null;
        return itemRegistry.get(name);
    }

    // --- HELPER FUNCTIONS ---

    public static Item smallHealthPotion() { return itemRegistry.get("Small Health Potion"); }
    public static Item smallManaPotion() { return itemRegistry.get("Small Mana Potion"); }
    public static Item splashHealthPotion() { return itemRegistry.get("Splash Small Health Potion"); }
    public static Item splashManaPotion() { return itemRegistry.get("Splash Small Mana Potion"); }
    public static Item mediumHealthPotion() { return itemRegistry.get("Medium Health Potion"); }
    public static Item mediumManaPotion() { return itemRegistry.get("Medium Mana Potion"); }
    public static Item revivePotion() { return itemRegistry.get("Revive Potion"); }
    public static Item summoningScroll() { return itemRegistry.get("Summoning Scroll"); }
}