package Abilities;

public enum SkillTarget {
    SINGLE_TARGET(1),    // Hits one enemy (default attack)
    AOE_TWO_TARGETS(2), // Hits a maximum of two enemies
    AOE_THREE_TARGETS(3), // Hits a maximum of three enemies
    AOE_ALL_TARGETS(4); // Hits the entire enemy party

    private final int maxTargets;

    SkillTarget(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    // helper methods for UI validation
    public int getMaxTargets() {
        return maxTargets;
    }

    public boolean isMultiTarget() {
        return maxTargets > 1;
    }
}