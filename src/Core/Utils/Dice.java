package Core.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.List;

/**
 * pure math utility for probabilities and stufffff
 */
public class Dice {
    private static Dice instance;
    private final Random random;
    private final long seed;

    private Dice() {
        this.seed = new Random().nextLong();
        this.random = new Random(seed);

        LogManager.log("Dice initialized with Seed: " + this.seed);
    }

    private Dice(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        LogManager.log("Dice initialized with Loaded Seed: " + this.seed);
    }

    public static Dice getInstance() {
        if (instance == null) {
            instance = new Dice();
        }
        return instance;
    }

    public static void setSeed(long newSeed) {
        instance = new Dice(newSeed);
    }

    public int roll(int min, int max) {
        if (min >= max) return min;
        return random.nextInt((max - min) + 1) + min;
    }

    public boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    // picks any random element from any list
    public <T> T pickRandom(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    // pick random n amount of elements without replacement
    public <T> List<T> pickRandomN(List<T> list, int n) {
        if (list == null || list.isEmpty() || n <= 0) {
            return new ArrayList<>();
        }

        if (n >= list.size()) {
            return new ArrayList<>(list);
        }

        List<T> copy = new ArrayList<>(list);

        Collections.shuffle(copy, random);

        return copy.subList(0, n);
    }

    public long getSeed() {
        return seed;
    }

    public Random getRandom() {
        return random;
    }
}
