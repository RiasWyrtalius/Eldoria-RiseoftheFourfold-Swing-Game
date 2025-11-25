package Abilities;

import Characters.Character;

public interface ReactionLogic {
    int tryReact(Character user, Character target, Skill incomingSkill, int incomingDamage);
}
