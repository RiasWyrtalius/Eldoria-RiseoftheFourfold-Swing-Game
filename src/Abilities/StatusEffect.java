package Abilities;

import Characters.Character;

public class StatusEffect {
    @FunctionalInterface
    public interface TickEffect {
        void onTick(Character target);
    }

    private final String name;
    private final TickEffect tickLogic;
    private int duration;
    private final StatusEffectType type;

    public StatusEffect(String name, int duration, TickEffect tickLogic, StatusEffectType type) {
        this.name = name;
        this.tickLogic = tickLogic;
        this.duration = duration;
        this.type = type;
    }

    public void tick(Character target) {
        if (duration <= 0 ) return;

        if (tickLogic != null) {
            tickLogic.onTick(target);
        }

        duration--;
    }

    public String getName() {
        return name;
    }

    public TickEffect getTickLogic() {
        return tickLogic;
    }

    public int getDuration() {
        return duration;
    }

    public StatusEffectType getType() {
        return type;
    }
}
