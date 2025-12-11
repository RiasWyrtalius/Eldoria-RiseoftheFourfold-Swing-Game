package Core.Utils;

import java.awt.*;

public class LogFormat {
    /*
        Color Coding for Logs

        System/Initialization     - Gray
        Player Join/Party Events  - Green
        Enemy Join/Spawn          - Orange
        Battle Start/Phase Header - Deep Crimson
        Turn Indicators           - Cyan
        Hero Actions              - Blue
        Enemy Actions             - Red
        Damage Taken              - Light Red
        XP Gain / Level Up        - Purple
        Enemy Death               - Dark Gray
        Victory                   - Gold
        Defeat                    - Charcoal Gray
        TIE                       - Silver
    */

    // SYSTEM LOGS
    public static final Color SYSTEM = new Color(99, 99, 99);
    public static final Color SYSTEM_ERROR   = new Color(220, 20, 60);   // Crimson Red
    public static final Color SYSTEM_WARNING = new Color(255, 140, 0);   // Dark Orange
    public static final Color DEBUG_INFO     = new Color(138, 43, 226);  // Blue Violet

    public static final Color PLAYER_JOIN = new Color(76, 175, 80); // #4CAF50
    public static final Color ENEMY_JOIN = new Color(255, 152, 0); // #FF9800
    public static final Color BATTLE_HEADER = new Color(183, 28, 28); // #B71C1C
    public static final Color TURN_INDICATOR = new Color(0, 0, 139); // #00BCD4
    public static final Color HERO_ACTION = new Color(33, 150, 243); // #2196F3
    public static final Color ENEMY_ACTION = new Color(244, 67, 54); // #F44336
    public static final Color DAMAGE_TAKEN = new Color(255, 205, 210); // #FFCDD2
    public static final Color XP_GAIN = new Color(156, 39, 176); // #9C27B0
    public static final Color ENEMY_DEATH = new Color(97, 97, 97); // #616161
    public static final Color VICTORY = new Color(255, 215, 0); // #FFD700
    public static final Color DEFEAT = new Color(66, 66, 66);
    public static final Color TIE = new Color(192, 192, 192);

    public static final Color HP = new Color(0, 255, 0);
    public static final Color MP = new Color(138, 216, 244);
    public static final Color DEAD = Color.GRAY;

    public static final int SIZE_NOTIFICATION = 20; // Default (Items found, minor alerts)
    public static final int SIZE_ACTION       = 28; // Attacks, Skills, Buffs
    public static final int SIZE_IMPACT       = 40; // Critical Hits, Executions
    public static final int SIZE_HEADER       = 60; // "PLAYER TURN", "VICTORY"

    public static final Color HIGHLIGHT_CRIT    = new Color(255, 30, 30);   // Bright Red/Crimson
    public static final Color HIGHLIGHT_BLOCK   = new Color(200, 200, 200); // White/Silver
    public static final Color HIGHLIGHT_DODGE   = new Color(100, 255, 255); // Cyan
    public static final Color HIGHLIGHT_HEAL    = new Color(50, 255, 50);   // Neon Green

    // TURN & PHASE
    public static final Color HIGHLIGHT_PLAYER_TURN = new Color(0, 150, 255); // Bright Blue
    public static final Color HIGHLIGHT_ENEMY_TURN  = new Color(255, 100, 0); // Bright Orange

    // STATUS & ITEMS
    public static final Color HIGHLIGHT_BUFF    = new Color(145, 123, 0);   // Gold
    public static final Color HIGHLIGHT_DEBUFF  = new Color(148, 0, 211);   // Violet
    public static final Color LOOT = new Color(255, 223, 0);

    // UI & SELECTION STATES
    public static final Color UI_SKILL_SELECT = new Color(0, 255, 255);
    public static final Color UI_ITEM_SELECT  = new Color(139, 69, 19);
    public static final Color UI_TARGET_SELECT = new Color(178, 34, 34);
    public static final Color UI_ACTIVE_HERO = new Color(0, 0, 205);

    // GAME STATES
    public static final Color HIGHLIGHT_LEVELUP = new Color(255, 0, 255);   // Magenta
    public static final Color HIGHLIGHT_VICTORY = new Color(159, 132, 10);   // Pure Gold
}
