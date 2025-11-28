package Abilities;

import Resource.AssetManager;

import java.util.ArrayList;
import java.util.List;

public abstract class JobClass {
    private final String name;
    private final String description;
    private final List<Skill> skills;
    private List<ReactionSkill> reactions;
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

    public abstract List<ReactionSkill> createReactions();
    public abstract List<Skill> createSkills();

    // =============== PUBLIC GETTERS FOR UI ===============
    public String getName() { return this.name; }
    public List<Skill> getSkills() {
        return skills;
    }

    public Skill getSkill(String skillName) {
        return this.skills.stream()
                .filter(skill -> skill.getName().equalsIgnoreCase(skillName))
                .findFirst()
                .orElse(null);
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

    //TEMPORARY GETTERS FOR CHARACTER DISPLAY
    public String getPreviewImagePath() { return ""; }
}