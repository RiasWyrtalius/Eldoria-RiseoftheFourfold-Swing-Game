package Items;

import Core.Utils.LogManager;

import java.util.*;
import java.util.function.Supplier;

// TODO: do the basic optimization wherein we only keep one instance of
// an available item because they're all just integers
public class LootManager {
    private static final LootManager INSTANCE = new LootManager();

    private final Map<Rarity, List<Supplier<Item>>> masterLootList = new HashMap<>();

    private LootManager() {
        for (Rarity r : Rarity.values()) {
            masterLootList.put(r, new ArrayList<>());
        }

        registerAllItems();
    }

    public static LootManager getInstance() { return INSTANCE; }

    private void register(Supplier<Item> itemSupplier) {
        Item itemInstance = itemSupplier.get();
        Rarity rarity = itemInstance.getRarity();
        if (masterLootList.containsKey(rarity)) {
            masterLootList.get(rarity).add(itemSupplier);
        } else {
            LogManager.log("Error: Unknown rarity for " + itemInstance.getName());
        }
    }

    /**
     * TODO: this is where flurgerburger  shines
     */
    private void registerAllItems() {
        register(ItemFactory::smallPotion);
        register(ItemFactory::revivePotion);
    }

    public List<Item> generateLoot(Random rng, int level) {
        List<Item> drops = new ArrayList<>();

        // 40% chance to get any loot
        if (rng.nextDouble() > 0.40) {
            return drops;
        }

        // determine rarity based on level
        Rarity selectedRarity = rollRarity(rng, level);
        //  matched rarities will be put into the pool yay
        List<Supplier<Item>> pool = masterLootList.get(selectedRarity);
        if (pool != null && !pool.isEmpty()) {
            Item item = pool.get(rng.nextInt(pool.size())).get();
            drops.add(item);
        } else {
            Supplier<Item> fallback = masterLootList.get(Rarity.COMMON).get(0);
            drops.add(fallback.get());
        }

        return drops;
    }


    /**
     * THE MATH: Calculates which rarity to pick based on Level.
     * TODO: fine tune this
     * I do not pretend i am bright enough to understand this...
     * I'll just leave it to flurger
     */
    private Rarity rollRarity(Random rng, int level) {
            Map<Rarity, Integer> weights = new HashMap<>();

        // Base Weights (Level 1)
        // Common: 1000, Uncommon: 100, Rare: 10, Epic: 1...

        // Base + (Level * Multiplier)

        weights.put(Rarity.COMMON,      1000); // Always constant, becomes relatively smaller as others grow
        weights.put(Rarity.UNCOMMON,    10 + (level * 20));
        weights.put(Rarity.RARE,        0  + (level * 10));
        weights.put(Rarity.SUPERIOR,    0  + (level * 5));
        weights.put(Rarity.EPIC,        (level > 10) ? (level * 3) : 0); // Only after lvl 10
        weights.put(Rarity.LEGENDARY,   (level > 20) ? (level * 2) : 0); // Only after lvl 20
        weights.put(Rarity.MYTHIC,      (level > 50) ? (level * 2) : 0); // Only after lvl 50

        // weighted random selection
        int totalWeight = 0;
        for (int w : weights.values()) totalWeight += w;

        int randomValue = rng.nextInt(totalWeight);
        int currentWeight = 0;

        for (Map.Entry<Rarity, Integer> entry : weights.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue < currentWeight) {
                return entry.getKey();
            }
        }

        return Rarity.COMMON; // default
    }
}
