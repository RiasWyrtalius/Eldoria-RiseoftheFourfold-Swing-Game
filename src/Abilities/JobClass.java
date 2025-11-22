package Abilities;

import Characters.Base.Hero;

public abstract class JobClass {
    private final String name;
    private final String description;
    private final List<Skill> skills;
    private final int hpBonus;
    private final int manaBonus;

    public JobClass(String name, String description, List<Skill> skills, int hpBonus, int manaBonus) {
        this.name = name;
        this.description = description;
        this.skills = skills;
        this.hpBonus = hpBonus;
        this.manaBonus = manaBonus;
    }

    public JobClass(String name, String description, List<Skill> skills) {
        this(name, description, skills, 0, 0);
    }

    public abstract void applyStatModifiers(Hero hero);

    // =============== PUBLIC GETTERS FOR UI ===============
    public String getName() { return this.name; }
    public List<Skill> getSkills() {
        return skills;
    }
}
