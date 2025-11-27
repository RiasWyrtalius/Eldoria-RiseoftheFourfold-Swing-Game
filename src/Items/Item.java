package Items;

import Abilities.SkillTarget;
import Characters.Character;

import java.util.List;

public class Item {
    private final String name;
    private final String description;
    private final SkillTarget targetType;
    private final Rarity rarity;
    private final ItemConsumer executeLogic;

    public Item(String name, String description, SkillTarget targetType, ItemConsumer executeLogic, Rarity rarity) {
        this.name = name;
        this.description = description;
        this.targetType = targetType;
        this.executeLogic = executeLogic;
        this.rarity = rarity;
    }

    public void use(Character user, List<Character> targets, Runnable onItemComplete) {
        executeLogic.accept(this, user, targets, onItemComplete);
    }

    // =============== PUBLIC GETTERS FOR UI ===============

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public SkillTarget getTargetType() {
        return targetType;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
