package Items;

import Characters.Character;

import Abilities.SkillTarget;
import Core.Utils.LogManager;

import java.util.List;

/**
 * Basically just a utility item but better character hp/mp handling
 */
public class ResourceItem extends Item {
    private final int hpAmount;
    private final int mpAmount;

    public ResourceItem(String name, String description, ItemConsumer executeLogic, Rarity rarity, int hpAmount, int mpAmount, SkillTarget targetType) {

        super(name, description, targetType, executeLogic, rarity);
        this.hpAmount = hpAmount;
        this.mpAmount = mpAmount;
    }

    @Override
    public void use(Character user, List<Character> targets, Runnable onItemComplete) {
        for (Character target : targets) {
            if (this.hpAmount > 0) {
                target.setHealth(target.getHealth() + this.hpAmount);
                LogManager.log(target.getName() + " healed for " + this.hpAmount);
            }
            if (this.mpAmount > 0) {
                target.setMana(target.getHealth() + this.mpAmount);
                LogManager.log(target.getName() + " restored " + this.mpAmount);
            }
        }

        if (onItemComplete != null) onItemComplete.run();
    }
}

