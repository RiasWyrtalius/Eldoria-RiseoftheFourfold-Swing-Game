package Core;

import Resource.Animation;
import Resource.AssetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Acts as a controller bridge between game state and the view's initialization logic
 */
public class VisualEffectsManager {
    private static final VisualEffectsManager INSTANCE = new VisualEffectsManager();
    private final List<Timer> runningTimers = new ArrayList<>();

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

        Timer timer = new Timer(animation.getFrameDurationMs(), e -> {
            if (animation.isFinished()) {
                ((Timer)e.getSource()).stop();
                runningTimers.remove(e.getSource());
                return;
            }
            LogManager.log("nextframe");
            displayLabel.setIcon(animation.getNextFrame());
            displayLabel.repaint();
        });
        runningTimers.add(timer);
        timer.start();
    }

    // tells if animation is static or not
    public void applyVisual(VisualAsset asset, JLabel displayLabel) {
        // mapping is better for complex apps
        stopAllTimers();

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
        for (Timer timer : runningTimers) {
            timer.stop();
        }
        runningTimers.clear();
    }
}
