package Characters.Enemies;

import Characters.Base.Enemy;
import Characters.Character;

import java.util.List;

public class Goblin extends Enemy {
    public Goblin() {
        super("Goblin Grunt", 20, 5, 0, 1, "Goblin", 10);
    }


    @Override
    public void makeAttack(List<Character> targets) {
        
    }
}
