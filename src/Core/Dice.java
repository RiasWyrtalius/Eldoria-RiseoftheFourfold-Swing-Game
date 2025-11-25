package Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.List;

public class Dice {
    private static final Random random = new Random();

    private Dice() {} // prevent instantiation

    public static Random getInstance() {
        return random;
    }

    public static int roll(int min, int max) {
        if (min >= max) return min;
        return random.nextInt((max - min) + 1) + min;
    }

    public static boolean chance(double probability) {
        return random.nextDouble() < probability;
    }

    // picks any random element from any list
    public static <T> T pickRandom(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    // pick random n amount of elements without replacement
    public static <T> List<T> pickRandomN(List<T> list, int n) {
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
}
