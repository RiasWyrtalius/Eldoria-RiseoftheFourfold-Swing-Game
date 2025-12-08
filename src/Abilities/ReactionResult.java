package Abilities;

public record ReactionResult(
        boolean wasTriggered, // Did it succeed?
        int newDamageValue,   // What is the new damage? (0 for dodge)
        boolean isFinal       // Should the reaction chain stop? (true for dodge)
) {
    public static ReactionResult FAILED(int originalDamage) {
        return new ReactionResult(false, originalDamage, false);
    }
}