package Core.GameFlow;

import javax.management.QueryEval;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Loads and saves levels
 */
public class GameLoader {
    private Queue<Level> campaignQueue;
    private long currentSeed;
    private int levelsCompleted;

    // TODO: hybrid procedural generation of levels according
    // according to seed
    public void GenerateLevels() {
        this.campaignQueue = new LinkedList<>();
    }

    public void saveGame() {
        // write to json or object buffer
        // save level completed
        // save current seed
    }

    public void loadGame() {
        // read seed
        // generateCampaign(seed)
        // pool
    }

    public Level loadNextLevel() {
        return campaignQueue.poll();
    }

    // TODO: end level
}
