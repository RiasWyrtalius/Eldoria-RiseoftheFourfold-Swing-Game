package Abilities;

import Characters.Character;
import java.util.function.Consumer;

@FunctionalInterface
public interface ReactionLogic {
    void tryReact(Character defender, Character attacker, Skill incomingSkill, int incomingDamage, Consumer<ReactionResult> onComplete);
}