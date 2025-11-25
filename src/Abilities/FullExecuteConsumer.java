package Abilities;

import Characters.Character;

import java.util.List;

@FunctionalInterface
public interface FullExecuteConsumer {
    void accept(Skill self, Characters.Character user, List<Character> targets, Runnable onSkillComplete);
}
