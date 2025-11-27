package Items;

import Core.Utils.LogColor;
import Core.Utils.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private final Map<String, Item> itemRegistry = new HashMap<>();

    private final Map<String, Integer> itemCounts = new HashMap<>();

    public void addItem(Item item, int amount) {
        itemRegistry.putIfAbsent(item.getName(), item);
        // uses existing count or default of 0 and add the amount. super cool dawg
        itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + amount);
        LogManager.log("Added " + amount + " " + item.getName() + " to inventory", LogColor.TURN_INDICATOR);
    }

    /**
     * Checks if an Item exists and decrements count
     * @param itemName name of the item.. duh
     * @return true if item is successfully used
     */
    public boolean consumeItem(String itemName) {
        if (!itemCounts.containsKey(itemName) || itemCounts.get(itemName) <= 0) {
            return false;
        }

        int newCount = itemCounts.get(itemName) - 1;
        if (newCount == 0) {
            itemCounts.remove(itemName);
            itemRegistry.remove(itemName); // we comment this if we want to show (0x) quantity
        } else {
            itemCounts.put(itemName, newCount);
        }

        return true;
    }

    public boolean hasItem(String itemName) {
        return itemCounts.getOrDefault(itemName, 0) > 0;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(itemRegistry.values());
    }

    public int getItemCount(String item) {
        return itemCounts.get(item);
    }

}
