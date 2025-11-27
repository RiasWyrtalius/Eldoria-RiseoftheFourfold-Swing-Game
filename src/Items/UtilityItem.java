package Items;

import Abilities.SkillTarget;
import Characters.Character;

import java.util.List;

/**
 * YOU CAN PUT ANY LOGIC HERE HEHEHEHE
 */
public class UtilityItem extends Item{
    private final ItemConsumer logic;

    public UtilityItem(String name, String description, SkillTarget targetType, ItemConsumer executeLogic, Rarity rarity, ItemConsumer logic) {
        super(name, description, targetType, executeLogic, rarity);
        this.logic = logic;
    }

    @Override
    public void use(Character user, List<Character> targets, Runnable onItemComplete) {
        logic.accept(this, user, targets, onItemComplete);
    }
}
