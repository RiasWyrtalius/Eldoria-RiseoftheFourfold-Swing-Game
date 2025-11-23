package Characters.Enemies;

import Characters.Base.Enemy;
import Characters.Character;
import Core.LogManager;

import java.util.List;

public class Boss extends Enemy {
    private double healthMultiplier;

    public Boss(String name, int initialHealth, int baseAtk, int maxMana, int level, String type, int rewardXP, double healthMultiplier) {
        super(name, initialHealth, baseAtk, maxMana, level, type, rewardXP, "/Assets/Images/bstudios.png");
        this.healthMultiplier = healthMultiplier;
        int buffedHP = (int) (initialHealth * this.healthMultiplier);
        setHealth(buffedHP);
    }

    public void devastatingStrike(List<Character> targets) {
        LogManager.log("!!! " + this.name + " unleashes a DEVASTATING STRIKE !!!");

        int AOE_DMG = (int) (this.getBaseAtk() * 1.5);

        for (Character target : targets) {
            if (target.getHealth() > 0) {
                target.takeDamage(AOE_DMG);
                LogManager.log(this.getName() + " hits " + target.getName() + " for " + AOE_DMG + " damage !");
            }
        }
    }

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

            if (weakTarget != null) {
                LogManager.log(this.getName() + " focuses on " + weakTarget.getName() + "!");

                int dmg = this.getBaseAtk();
                weakTarget.takeDamage(dmg);
                LogManager.log(this.getName() + " hits " + weakTarget.getName() + " for " + dmg + "damage!");
            }
        }
    }
}
