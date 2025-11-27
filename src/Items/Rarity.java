package Items;

public enum Rarity {
    COMMON("COMMON"),
    UNCOMMON("UNCOMMON"),
    RARE("RARE"),
    SUPERIOR("SUPERIOR"),
    EPIC("EPIC"),
    LEGENDARY("LEGENDARY"),
    MYTHIC("MYTHIC");

    private final String rarity;

    Rarity(String rarity) {
        this.rarity = rarity;
    }

    public String getRarity() {
        return this.rarity;
    }
}