package Core.GameFlow;

import Characters.Base.Enemy;
import Characters.Party;
import Core.Utils.Dice;
import Items.Item;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public record Level(
        int levelNumber,
        String levelName,
        String introduction,
        String battleBackground,

        List<Function<Integer, Enemy>> enemyGenerators,
        int minEnemies, int maxEnemies,

        List<Item> possibleItemDrops,
        int xpReward,
        long levelSeed) {

        public Party createEnemyParty() {
            Party party = new Party(levelName + " Enemies");

            Random localRng = new Random(levelSeed);
            int count = Dice.getInstance().roll(minEnemies, maxEnemies);

            // TODO: deal with this cast
            for (int i = 0; i < count; i++) {
                Function<Integer, Enemy> spawner = Dice.getInstance().pickRandom(enemyGenerators);

                if (spawner != null) {
                    Enemy enemy = spawner.apply(this.levelNumber);
                    party.addPartyMember(enemy);
                }
            }

            return party;
        }
}
