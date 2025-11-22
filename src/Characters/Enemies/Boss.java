package Characters.Enemies;

import Characters.Base.Enemy;
import Characters.Character;
import java.util.List;

public class Boss extends Enemy {
    private double healthMultiplier;

    public Boss(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(name, initialHealth, baseAtk, maxMana, level, type, rewardXP);
        this.healthMultiplier = healthMultiplier;
        int buffedHP = (int) (initialHealth * this.healthMultiplier);
        setHealth(buffedHP);
    }

//        TODO: move to frontend logger
    public void devastatingStrike(List<Character> targets) {
        System.out.println("!!! " + this.name + " unleashes a DEVASTATING STRIKE !!!");

        int AOE_DMG = (int) (this.getBaseAtk() * 1.5);

        for (Character target : targets) {
            if (target.getHealth() > 0) {
                target.takeDamage(AOE_DMG);
                System.out.println(this.getName() + " hits " + target.getName() + " for " + AOE_DMG + " damage !");
            }
        }
    }

//        TODO: move to frontend logger
    @Override
    public void makeAttack(List<Character> targets) {
        int manaCost = 50; // Dev. Strike mana cost

        if(this.spendMana(manaCost)) {
            devastatingStrike(targets);
        } else {
            Character weakTarget = null;
            int lowHP = Integer.MAX_VALUE;

            for (Character c : targets) {
                if (c.getHealth() > 0 && c.getHealth() < lowHP) {
                    lowHP = c.getHealth();
                    weakTarget = c;         //lowest hp Hero
                }
            }

            // Might be good to have this on a separate "Turn"
            if (weakTarget != null) {
                System.out.println(this.getName() + " focuses on " + weakTarget.getName() + "!");

                int dmg = this.getBaseAtk();
                weakTarget.takeDamage(dmg);
                System.out.println(this.getName() + " hits " + weakTarget.getName() + " for " + dmg + "damage!");
            }
        }
    }
}
