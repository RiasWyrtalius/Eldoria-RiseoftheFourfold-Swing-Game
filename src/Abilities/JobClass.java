package Abilities;

import Resource.AssetManager;

import java.util.List;

public abstract class JobClass {
    private final String name;
    private final String description;
    private final List<Skill> skills;
    private final int hpBonus;
    private final int manaBonus;

    public JobClass(String name, String description, int hpBonus, int manaBonus) {
        this.name = name;
        this.description = description;
        this.hpBonus = hpBonus;
        this.manaBonus = manaBonus;
        this.skills = createSkills();
    }

    public JobClass(String name, String description) {
        this(name, description, 0, 0);
    }

    public abstract List<Skill> createSkills();

    // =============== PUBLIC GETTERS FOR UI ===============
    public String getName() { return this.name; }
    public List<Skill> getSkills() {
        return skills;
    }
    public int getHpBonus() {
        return hpBonus;
    }
    public int getManaBonus() {
        return manaBonus;
    }

    public String getDescription() {
        return description;
    }
}