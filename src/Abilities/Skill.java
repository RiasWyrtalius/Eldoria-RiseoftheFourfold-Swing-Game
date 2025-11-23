package Abilities;

import Characters.Character;

import java.time.temporal.Temporal;
import java.util.function.BiConsumer;

public class Skill {
    private String name;
    private String effect;
    private int manaCost;
    private int power;
    private SkillType skillType;
    private SkillAction skillAction;
    private final BiConsumer<Character, Character> executeLogic;

    public Skill(String name, String effect, int manaCost, int power, SkillType skillType, SkillAction skillAction, BiConsumer<Character, Character> executeLogic) {
        this.name = name;
        this.effect = effect;
        this.manaCost = manaCost;
        this.power = power;
        this.skillType = skillType;
        this.skillAction = skillAction;
        this.executeLogic = executeLogic;
    }

    public void execute(Character user, Character target) {
        if (user.spendMana(this.manaCost)) {
            executeLogic.accept(user, target);
        }
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    public String getName() {
        return name;
    }
    public String getEffect() {
        return effect;
    }
    public int getManaCost() {
        return manaCost;
    }
    public int getPower() {
        return power;
    }
    public SkillType getSkillType() {
        return skillType;
    }
    public BiConsumer<Character, Character> getExecuteLogic() {
        return executeLogic;
    }
}
