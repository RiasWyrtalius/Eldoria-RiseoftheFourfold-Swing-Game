package Abilities;

import Characters.Character;
import Core.Battle.BattleController;

import java.util.List;

@FunctionalInterface
public interface SkillLogicConsumer {
    void accept(BattleController controller, Skill skill, Character user, List<Character> targets, Runnable onComplete);
}
