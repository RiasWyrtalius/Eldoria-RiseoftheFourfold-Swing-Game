package Items;

import Characters.Character;

import java.util.List;

@FunctionalInterface
public interface ItemConsumer {
    void accept(Item item, List<Character> targets, Runnable onItemComplete);
}