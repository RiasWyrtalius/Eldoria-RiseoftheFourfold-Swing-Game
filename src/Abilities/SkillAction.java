package Abilities;

public enum SkillAction {
    PHYSICAL("attacks"),
    MAGICAL("casts"),
    ELEMENTAL("blasts"),
    TRUE_DAMAGE("strikes");

    private final String actionVerb;

    SkillAction(String actionVerb) {
        this.actionVerb = actionVerb;
    }

    public String getActionVerb() {
        return actionVerb;
    }
}
