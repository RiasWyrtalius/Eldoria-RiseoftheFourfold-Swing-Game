package Characters.Base;

import Characters.Character;

// TODO: refine
public class Enemy extends Character {
    public Enemy(String name, int initialHealth, int baseAtk, int maxMana, int level) {
        super(name, initialHealth, baseAtk, maxMana, level);
    }

    public Enemy(String name, int health, int baseAtk, int maxMana) {
        super(name, health, baseAtk, maxMana);
    }

    @Override
    protected void onDeath() {

    }

    @Override
    protected void onDefeat(Character defeatedTarget) {

    }
}
