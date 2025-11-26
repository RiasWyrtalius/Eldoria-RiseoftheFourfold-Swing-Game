package Abilities;

public enum SkillTarget {
    SINGLE_TARGET(1, "Single-Target"),    // Hits one enemy (default attack)
    AOE_TWO_TARGETS(2, "AOE MAX (2) Targets"), // Hits a maximum of two enemies
    AOE_THREE_TARGETS(3, "AOE MAX (3) Targets"), // Hits a maximum of three enemies
    AOE_FOUR_TARGETS(4, "AOE MAX (4) Targets"),
    AOE_ALL_TARGETS(4, "AOE ALL TARGETS"); // Hits the entire enemy party

    private final int maxTargets;
    private final String maxTargetString;

    SkillTarget(int maxTargets, String maxTargetString) {
        this.maxTargets = maxTargets;
        this.maxTargetString = maxTargetString;
    }

    // helper methods for UI validation
    public int getMaxTargets() {
        return maxTargets;
    }

    public boolean isMultiTarget() {
        return maxTargets > 1;
    }
}