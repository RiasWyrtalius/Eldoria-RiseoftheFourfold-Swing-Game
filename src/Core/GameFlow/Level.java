package Core.GameFlow;

import Characters.Base.Enemy;
import Characters.Party;
import Core.Story.StorySlide;
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
        long levelSeed,
        boolean isFixedRoster,

        List<StorySlide> preLevelCutscene,
        List<StorySlide> postLevelCutscene
) {

    public Party buildEnemyParty() {
        Party party = new Party(levelName + " Enemies");

        if (this.isFixedRoster) {
            for (Function<Integer, Enemy> spawner : enemyGenerators) {
                party.addPartyMember(spawner.apply(this.levelNumber));
            }
        }
        else {
            Random localRng = new Random(this.levelSeed);

            int count = Dice.getInstance().roll(minEnemies, maxEnemies, localRng);

            for (int i = 0; i < count; i++) {
                Function<Integer, Enemy> spawner = Dice.getInstance().pickRandom(enemyGenerators, localRng);

                if (spawner != null) {
                    party.addPartyMember(spawner.apply(this.levelNumber));
                }
            }
        }

        return party;
    }
}
