package UI.Components;

public enum BattleUIMode {
    IDLE,                   // Waiting for turn to begin
    HERO_SELECT,            // Player needs to select hero to act
    SKILL_SELECT,           // Player has chosen a hero, now selecting skill
    ITEM_SELECT,            // Player has chosen an item and is now selecting a target
    TARGET_SELECT,          // Player has chosen a skill, now selecting target
}
