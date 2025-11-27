package Resource;

import javax.swing.*;
import java.util.List;

/**
 * TODO: revamp the SOC because some methods that may belong on the visualeffects manager belongs in here
 */
public class Animation {
    private final List<ImageIcon> frames;
    private int currentFrameIndex;
    private final int totalFrames;

    private final AnimationLoopType loopType;
    private int loopsCompleted = 0;
    private final int frameDurationMs;
    private boolean isFinishedFlag = false;
    private boolean isPausedFlag = false;

    public Animation(List<ImageIcon> frames, int frameDurationMs, AnimationLoopType loopType) {
        this.frames = frames;
        this.totalFrames = frames.size();
        this.frameDurationMs = frameDurationMs;
        this.loopType = loopType;
    }


    /**
     * resets da animation state so it can be re-executed
     */
    public void reset() {
//        LogManager.log("Reset Animation");
        this.currentFrameIndex = 0;
        this.loopsCompleted = 0;
        this.isFinishedFlag = false;
    }

    // =============== PUBLIC GETTERS ===============
    /**
     * Advances frame and returns the next frame to draw
     * @return the next ImageIcon sequence
     */
    public ImageIcon getNextFrame() {
        if (isFinishedFlag) {
            return frames.get(totalFrames - 1);
        }

        if (isPausedFlag) return frames.get(currentFrameIndex);

        currentFrameIndex++;

        // loop cycle completed
        if (currentFrameIndex >= totalFrames) {
            currentFrameIndex = 0;
            loopsCompleted++;

            int requiredLoops = loopType.getLoopCountValue();

            if (requiredLoops != AnimationLoopType.INFINITE.getLoopCountValue() && loopsCompleted >= requiredLoops) {
                isFinishedFlag = true;
                // Ensure that the last frame is shown lol
                return frames.get(totalFrames - 1);
            }
        }

        return frames.get(currentFrameIndex);
    }

    public ImageIcon getCurrentFrame() {
        return frames.get(currentFrameIndex);
    }

    public int getFrameDurationMs() {
        return frameDurationMs;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    public List<ImageIcon> getFrames() {
        return frames;
    }

    public boolean isFinished() {
        return isFinishedFlag;
    }

    public boolean isPausedFlag() {
        return isPausedFlag;
    }

    public void pauseAnimation() {
        this.isPausedFlag = true;
    }

    public void continueAnimation() {
        this.isPausedFlag = false;
    }

    // TODO: add logic to check if animation is finished
}
