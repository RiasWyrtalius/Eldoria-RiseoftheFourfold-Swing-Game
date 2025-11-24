package Core;

import Resource.Animation;
import Resource.AssetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VisualEffectsManager {
    private static final VisualEffectsManager INSTANCE = new VisualEffectsManager();

    private VisualEffectsManager() {}

    public static VisualEffectsManager getInstance() {
        return INSTANCE;
    }

    public void startSpriteAnimation(String animationId, JLabel displayLabel) {
        Animation animation = AssetManager.getInstance().getAnimation(animationId);

        if (animation == null) {
            LogManager.log("Error: Animation ID not found: " + animationId, Color.RED);
        }

        Timer timer = new Timer(animation.getFrameDurationMs(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayLabel.setIcon(animation.getNextFrame());
                displayLabel.repaint();

                if (animation.isFinished()) {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
}
