package Items;

import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Characters.Character;

import java.util.List;

public class Item {
    private final String name;
    private final String description;
    private final TargetType targetType;
    private final TargetCondition targetCondition;
    private final Rarity rarity;
    private final ItemConsumer executeLogic;

    public Item(String name, String description, TargetType targetType, TargetCondition targetCondition, ItemConsumer executeLogic, Rarity rarity) {
        this.name = name;
        this.description = description;
        this.targetType = targetType;
        this.targetCondition = targetCondition;
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

    public TargetType getTargetType() {
        return targetType;
    }

    public TargetCondition getTargetCondition() {
        return targetCondition;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
