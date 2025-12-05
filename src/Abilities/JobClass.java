package Abilities;

import java.util.List;

public abstract class JobClass {
    private final String name;
    private final String description;

    // Core Abilities
    private final List<Skill> skills;
    private List<ReactionSkill> reactions;

    // --- BASE STAT MODIFIERS ---
    private final int hpBonus;      // One-time boost at creation
    private final int manaBonus;    // One-time boost at creation

    // --- PER-LEVEL SCALING CONSTANTS ---
    private final int hpFlat;       // Flat HP per level
    private final double hpGrowth;  // % HP per level
    private final int mpFlat;       // Flat MP per level
    private final double mpGrowth;  // % MP per level

    // Visuals
    private final String idleImageKey;

    public JobClass(
            String name, String description,
            String idleImageKey,
            int hpBonus, int manaBonus,
            int hpFlat, double hpGrowth,
            int mpFlat, double mpGrowth) {
        this.name = name;
        this.description = description;
        this.idleImageKey = idleImageKey;

        // Base Stats
        this.hpBonus = hpBonus;
        this.manaBonus = manaBonus;

        // Scaling Stats
        this.hpFlat = hpFlat;
        this.hpGrowth = hpGrowth;
        this.mpFlat = mpFlat;
        this.mpGrowth = mpGrowth;

        // Initialize Abilities
        this.skills = createSkills();
        this.reactions = createReactions();

        // Load assets after everything is defined
        registerAssets();
    }

    public JobClass(String name, String description, String idleImageKey, int hpBonus, int manaBonus) {
        this(name, description, idleImageKey, hpBonus, manaBonus, 0, 0.0, 0, 0.0);
    }
    
    public JobClass(String name, String description, String idleImageKey) {
        this(name, description, idleImageKey, 0, 0, 0, 0.0, 0, 0.0);
    }

    public abstract List<ReactionSkill> createReactions();
    public abstract List<Skill> createSkills();

    public abstract void registerAssets();

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
    public List<ReactionSkill> getReactions() {
        return reactions;
    }

    public void setReactions(List<ReactionSkill> reactions) {
        this.reactions = reactions;
    }

    public int getHpFlat() {
        return hpFlat;
    }

    public double getHpGrowth() {
        return hpGrowth;
    }

    public int getMpFlat() {
        return mpFlat;
    }

    public double getMpGrowth() {
        return mpGrowth;
    }

    //TEMPORARY GETTERS FOR CHARACTER DISPLAY
    public String getPreviewImagePath() { return ""; }
    public String getIdleImageKey() { return this.idleImageKey; }

}