package Core;

import java.awt.*;

public class LogColor {
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

    public static final Color SYSTEM = new Color(99, 99, 99); // #A0A0A0
    public static final Color PLAYER_JOIN = new Color(76, 175, 80); // #4CAF50
    public static final Color ENEMY_JOIN = new Color(255, 152, 0); // #FF9800
    public static final Color BATTLE_HEADER = new Color(183, 28, 28); // #B71C1C
    public static final Color TURN_INDICATOR = new Color(0, 188, 212); // #00BCD4
    public static final Color HERO_ACTION = new Color(33, 150, 243); // #2196F3
    public static final Color ENEMY_ACTION = new Color(244, 67, 54); // #F44336
    public static final Color DAMAGE_TAKEN = new Color(255, 205, 210); // #FFCDD2
    public static final Color XP_GAIN = new Color(156, 39, 176); // #9C27B0
    public static final Color ENEMY_DEATH = new Color(97, 97, 97); // #616161
    public static final Color VICTORY = new Color(255, 215, 0); // #FFD700
    public static final Color DEFEAT = new Color(66, 66, 66);
    public static final Color TIE = new Color(192, 192, 192);
}
