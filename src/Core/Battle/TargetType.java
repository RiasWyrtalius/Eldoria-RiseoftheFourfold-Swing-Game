package Core.Battle;

public enum TargetType {
    NO_TARGETS(0, "No Targets"), // Purely a "use" skill
    SINGLE_TARGET(1, "Single-Target"),    // Hits one enemy (default attack)
    AOE_TWO_TARGETS(2, "Area of Effect up to 2 Targets"), // Hits a maximum of two enemies
    AOE_THREE_TARGETS(3, "Area of Effect up to 3 Targets"), // Hits a maximum of three enemies
    AOE_FOUR_TARGETS(4, "Area of Effect up to 4 Targets"),
    AOE_ALL_TARGETS(4, "Area of Effect up to all Targets"); // Hits the entire enemy party

    private final int maxTargets;
    private final String maxTargetString;

    TargetType(int maxTargets, String maxTargetString) {
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

    public String getMaxTargetString() {
        return maxTargetString;
    }
}