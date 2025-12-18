package Abilities;

import java.util.List;

public abstract class JobClass {
    private final String name;
    private final String description;

    // Core Abilities
    private final List<Skill> skills;
    private List<ReactionSkill> reactions;

    // --- BASE STAT MODIFIERS --- // One-time boost at creation
    private final int hpBonus;
    private final int mpBonus;
    private final int defBonus;

    // --- PER-LEVEL SCALING CONSTANTS ---
    private final int hpFlat;       // Flat HP per level
    private final double hpGrowth;  // % HP per level

    private final int mpFlat;       // Flat MP per level
    private final double mpGrowth;  // % MP per level

    private final int defFlat;
    private final double defGrowth;

    // Visuals
    private final String idleImageKey;

    public JobClass(
            String name, String description,
            String idleImageKey,
            int hpBonus, int mpBonus, int defBonus,
            int hpFlat, double hpGrowth,
            int mpFlat, double mpGrowth,
            int defFlat, double defGrowth) {

        this.name = name;
        this.description = description;
        this.idleImageKey = idleImageKey;

        // Base Stats
        this.hpBonus = hpBonus;
        this.mpBonus = mpBonus;
        this.defBonus = defBonus;

        // Scaling Stats
        this.hpFlat = hpFlat;
        this.hpGrowth = hpGrowth;

        this.mpFlat = mpFlat;
        this.mpGrowth = mpGrowth;

        this.defFlat = defFlat;
        this.defGrowth = defGrowth;

        // Initialize Abilities
        this.skills = createSkills();
        this.reactions = createReactions();

        // Load assets after everything is defined
        registerAssets();
    }

    public JobClass(String name, String description, String idleImageKey, int hpBonus, int mpBonus, int defBonus) {
        this(name, description, idleImageKey,
                hpBonus, mpBonus, defBonus,
                4, 0.05,
                2, 0.009,
                1, 0.02);

        //HP: +4 Flat, +5%
        //MP: +2 Flat, +0.9%
        //DEF: +1 Flat, +2%
    }
    
    public JobClass(String name, String description, String idleImageKey) {
        this(name, description, idleImageKey, 0, 0, 0);
    }

    public abstract List<ReactionSkill> createReactions();
    public abstract List<Skill> createSkills();
    public abstract void registerAssets();

    public void setReactions(List<ReactionSkill> reactions) {
        this.reactions = reactions;
    }

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
    public int getMpBonus() {
        return mpBonus;
    }
    public int getDefBonus() { return defBonus; }
    public String getDescription() {
        return description;
    }
    public List<ReactionSkill> getReactions() {
        return reactions;
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

    public int getDefFlat() { return defFlat; }
    public double getDefGrowth() { return defGrowth; }

    public String getPreviewImagePath() { return ""; }
    public String getIdleImageKey() { return this.idleImageKey; }

}