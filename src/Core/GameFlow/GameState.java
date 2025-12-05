package Core.GameFlow;

import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;
import Items.Inventory;
import Items.Item;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public long seed;
    public int levelsCompleted;
    public List<HeroSaveData> partyMembers;
    public String partyName;
    public Map<String, Integer> inventoryCounts;

    public GameState(long seed, int levelsCompleted, Party heroParty) {
        this.seed = seed;
        this.levelsCompleted = levelsCompleted;

        this.partyName = heroParty.getPartyName();
        this.partyMembers = new ArrayList<>();
        for (Character c : heroParty.getPartyMembers()) {
            if (c instanceof Hero) {
                this.partyMembers.add(new HeroSaveData((Hero)c));
            }
        }

        // shared inventory
        this.inventoryCounts = new HashMap<>();
        Inventory inv = heroParty.getInventory();
        for (Item item : inv.getAllItems()) {
            inventoryCounts.put(item.getName(), inv.getItemCount(item.getName()));
        }
    }
}
