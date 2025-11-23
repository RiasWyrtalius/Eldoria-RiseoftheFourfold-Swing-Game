package Abilities;

public enum SkillTarget {
    SINGLE_ENEMY,    // Hits one enemy (default attack)
    SINGLE_ALLY,     // Hits one ally (single heal)
    AOE_ALL_ENEMIES, // Hits the entire enemy party
    AOE_ALL_ALLIES,  // Hits the entire hero party (party-wide heal/buff)
    SELF             // Targets only the caster
}