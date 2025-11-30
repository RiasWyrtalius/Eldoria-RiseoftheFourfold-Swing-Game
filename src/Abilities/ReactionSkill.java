package Abilities;

public record ReactionSkill(String name, ReactionTrigger trigger, ReactionLogic logic) {
    public String getName() { return name; }
}
