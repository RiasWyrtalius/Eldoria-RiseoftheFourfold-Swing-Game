package Characters.Enemies;

import Characters.Base.Enemy;
import Characters.Character;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;

public class Goblin extends Enemy {
    public Goblin() {

        super("Goblin Grunt", 20, 5, 0, 1, "Goblin", 10, "GOBLIN_IDLE");
        AssetManager.getInstance().registerAnimation(
                "GOBLIN_IDLE",
                "Assets/Animations/Goblin/Idle/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "GOBLIN_SWING-ATTACK",
                "Assets/Animations/Goblin/Effects/Swing_Attack/sprite_%d.png",
                4, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );
    }

    @Override
    public void makeAttack(List<Character> targets) {

            targets.getFirst().takeDamage(baseAtk, this);

    }
}
