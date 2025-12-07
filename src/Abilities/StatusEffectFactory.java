package Abilities;

import Core.Utils.LogFormat;
import Core.Utils.LogManager;

import java.awt.*;

// flurger you can also make custom inline status effects
public class StatusEffectFactory {
    // DEBUFFS
    public static StatusEffect burn(int damage, int duration) {
        StatusEffect.TickEffect logic = (target) -> {
            // TODO: add special effects
            LogManager.log(target.getName() + " is scorched for " + damage + " damage!", LogFormat.HIGHLIGHT_DEBUFF);
            target.receiveDamage(damage, null, null);
        };
        return new StatusEffect("Burn", duration, logic, StatusEffectType.DEBUFF);
    }

    public static StatusEffect stun(int duration) {
        StatusEffect.TickEffect logic = (target) -> {
            LogManager.log(target.getName() + " is stunned and cannot act!", LogFormat.HIGHLIGHT_DEBUFF);
            // battle controller will check for stun and skip the turn
        };
        return new StatusEffect("Stun", duration, logic, StatusEffectType.DEBUFF);
    }

    // BUFFS
    public static StatusEffect regeneration(int amount, int duration) {
        StatusEffect.TickEffect logic = (target) -> {
            LogManager.log(target.getName() + " regenerates " + amount + " HP.", LogFormat.HIGHLIGHT_BUFF);
            target.receiveHealing(amount, null);
        };
        return new StatusEffect("Regeneration", duration, logic, StatusEffectType.BUFF);
    }
}
