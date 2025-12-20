package Core.Battle;

public enum BattlePhase {
    IDLE,
    HERO_ACTION_WAIT,
    ENEMY_ACTION,
    BATTLE_ENDED,
    RECUPERATION        // Battle won, looting done, player managing inventory
}
