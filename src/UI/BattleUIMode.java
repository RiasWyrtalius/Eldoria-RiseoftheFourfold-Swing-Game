package UI;

public enum BattleUIMode {
    IDLE,                   // Waiting for turn to begin
    HERO_SELECT,            // Player needs to select hero to act
    SKILL_SELECT,           // Player has chosen a hero, now selecting skill
    TARGET_SELECT,          // Player has chosen a skill, now selecting target
}
