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
        register(ItemFactory::smallHealthPotion);
        register(ItemFactory::smallManaPotion);
        register(ItemFactory::revivePotion);
    }

    public List<Item> generateLoot(Random rng, int level) {
        List<Item> drops = new ArrayList<>();
        // Always guarantee at least one drop
        Rarity selectedRarity = rollRarity(rng, level);
        List<Supplier<Item>> pool = masterLootList.get(selectedRarity);
        if (pool != null && !pool.isEmpty()) {
            Item item = pool.get(rng.nextInt(pool.size())).get();
            drops.add(item);
        } else {
            // fallback to common if pool is empty
            Supplier<Item> fallback = masterLootList.get(Rarity.COMMON).getFirst();
            drops.add(fallback.get());
        }
        // Bonus rolls: +1 every 5 levels (up to 4 at level 20)
        int bonusRolls = level / 5;
        for (int i = 0; i < bonusRolls; i++) {
            if (rng.nextDouble() < 0.35) { // 35% chance per bonus roll
                Rarity extraRarity = rollRarity(rng, level);
                List<Supplier<Item>> extraPool = masterLootList.get(extraRarity);
                if (extraPool != null && !extraPool.isEmpty()) {
                    Item extraItem = extraPool.get(rng.nextInt(extraPool.size())).get();
                    drops.add(extraItem);
                }
            }
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

        // Adjusted Weights
        // COMMON starts high but decreases as level rises
        // UNCOMMON and higher scale faster to catch up

        weights.put(Rarity.COMMON,    Math.max(1000 - (level * 10), 100)); // starts 1000, fades to 100
        weights.put(Rarity.UNCOMMON,  200 + (level * 50));                 // grows quickly
        weights.put(Rarity.RARE,      100 + (level * 25));                 // steady growth
        weights.put(Rarity.SUPERIOR,  50  + (level * 15));                 // slower but consistent
        weights.put(Rarity.EPIC,      (level > 10) ? (level * 10) : 0);    // unlocks after lvl 10
        weights.put(Rarity.LEGENDARY, (level > 20) ? (level * 5)  : 0);    // unlocks after lvl 20
        weights.put(Rarity.MYTHIC,    (level > 50) ? (level * 3)  : 0);    // unlocks after lvl 50

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

        return Rarity.COMMON; // fallback
    }

}
