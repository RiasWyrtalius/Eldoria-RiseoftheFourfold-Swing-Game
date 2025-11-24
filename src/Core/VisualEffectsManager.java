package Core;

import Resource.Animation;
import Resource.AssetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acts as a controller bridge between game state and the view's initialization logic
 */
public class VisualEffectsManager {
    private static final VisualEffectsManager INSTANCE = new VisualEffectsManager();
    private final Map<JLabel, Timer> activeAnimationTimers = new HashMap<>();

    private VisualEffectsManager() {}

    public static VisualEffectsManager getInstance() {
        return INSTANCE;
    }

    public void startSpriteAnimation(String animationId, JLabel displayLabel) {
        Animation animation = AssetManager.getInstance().getAnimation(animationId);

        if (animation == null) {
            LogManager.log("Error: Animation ID not found: " + animationId, Color.RED);
            return;
        }

        animation.reset();

        displayLabel.setIcon(animation.getCurrentFrame());

        Timer timer = new Timer(animation.getFrameDurationMs(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (animation.isFinished()) {
                    Timer finishedTimer = (Timer)e.getSource();
                    finishedTimer.stop();
                    activeAnimationTimers.entrySet().removeIf(entry -> entry.getValue() == finishedTimer);

                    LogManager.log("animation finished");
                    return;
                }
                displayLabel.setIcon(animation.getNextFrame());
                displayLabel.revalidate();
                displayLabel.repaint();
            }
        });

        activeAnimationTimers.put(displayLabel, timer);
        LogManager.log("start timer");
        timer.start();
    }

    // tells if animation is static or not
    public void applyVisual(VisualAsset asset, JLabel displayLabel) {
        if (activeAnimationTimers.containsKey(displayLabel)) {
            LogManager.log("stop remove timer");
            activeAnimationTimers.get(displayLabel).stop();
            activeAnimationTimers.remove(displayLabel);
        }

        if (asset.isAnimation()) {
            startSpriteAnimation(asset.key(), displayLabel);
        } else {
            int w = 100;
            int h = 100;

            ImageIcon staticIcon = AssetManager.getInstance().getImage(asset.key(), w, h);
            displayLabel.setIcon(staticIcon);
        }
    }

    public void stopAllTimers() {
        for (Timer timer : activeAnimationTimers.values()) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }

        activeAnimationTimers.clear();

        LogManager.log("All active animation timers have been stopped.", java.awt.Color.BLUE);
    }
}
