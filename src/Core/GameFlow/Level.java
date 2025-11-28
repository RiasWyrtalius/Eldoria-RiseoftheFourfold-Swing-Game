package Core.GameFlow;

import Characters.Party;
import Items.Item;

import java.util.List;

public record Level(int levelNumber, Party enemyParty, List<Item> itemDrops, int XPDrop, String battleBackground) {
    public Party createEnemyParty() {
        // random create enemy party function
        return null;
    }
}

//public class Level {
//    private int levelNumber;
//    private Party enemyParty;
//    private List<Item> itemDrops;
//    private int XPDrop;
//    private String battleBackground;
//
//    public Level(int levelNumber, Party enemyParty, List<Item> itemDrops, int XPDrop, String battleBackground) {
//        this.levelNumber = levelNumber;
//        this.enemyParty = enemyParty;
//        this.itemDrops = itemDrops;
//        this.XPDrop = XPDrop;
//        this.battleBackground = battleBackground;
//    }
//
//    public int getLevelNumber() {
//        return levelNumber;
//    }
//
//    public void setLevelNumber(int levelNumber) {
//        this.levelNumber = levelNumber;
//    }
//
//    public Party getEnemyParty() {
//        return enemyParty;
//    }
//
//    public void setEnemyParty(Party enemyParty) {
//        this.enemyParty = enemyParty;
//    }
//
//    public List<Item> getItemDrops() {
//        return itemDrops;
//    }
//
//    public void setItemDrops(List<Item> itemDrops) {
//        this.itemDrops = itemDrops;
//    }
//
//    public int getXPDrop() {
//        return XPDrop;
//    }
//
//    public void setXPDrop(int XPDrop) {
//        this.XPDrop = XPDrop;
//    }
//}
