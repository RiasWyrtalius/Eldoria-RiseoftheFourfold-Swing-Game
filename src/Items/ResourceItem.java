package Items;

import Characters.Character;

import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogManager;

import java.util.List;

/**
 * Basically just a utility item but better character hp/mp handling
 */
public class ResourceItem extends Item {
    private final int hpAmount;
    private final int mpAmount;

    public ResourceItem(String name, String description, ItemConsumer executeLogic, Rarity rarity, int hpAmount, int mpAmount, TargetType targetType) {

        super(name, description, targetType, TargetCondition.ALIVE, executeLogic, rarity);
        this.hpAmount = hpAmount;
        this.mpAmount = mpAmount;
    }

    @Override
    public void use(Character user, List<Character> targets, Runnable onItemComplete) {
        for (Character target : targets) {
            if (this.hpAmount > 0) {
                target.receiveHealing(this.hpAmount, user);
            }
            if (this.mpAmount > 0) {
                target.receiveMana(this.mpAmount, user);
            }
        }

        if (onItemComplete != null) onItemComplete.run();
    }
}

