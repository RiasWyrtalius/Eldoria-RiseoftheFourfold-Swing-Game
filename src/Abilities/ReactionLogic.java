package Abilities;

import Characters.Character;

import java.util.function.Consumer;

@FunctionalInterface
public interface ReactionLogic {
    void tryReact(Character user, Character target, Skill incomingSkill, int incomingDamage, Consumer<Integer> onComplete);
}
