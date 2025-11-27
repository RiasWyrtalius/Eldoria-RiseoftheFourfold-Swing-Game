package Abilities;

import Characters.Character;

import java.util.List;

@FunctionalInterface
public interface ItemConsumer {
    void accept(Item item, Character character, List<Character> targets, Runnable onItemComplete);
}