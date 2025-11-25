package Core;

import Resource.Animation;
import Resource.AssetManager;
import UI.Components.CharacterStatusPanel;
import UI.MainInterface;

import Characters.Character;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Acts as a controller bridge between game state and the view's initialization logic
 */
public class VisualEffectsManager {
    private static final VisualEffectsManager INSTANCE = new VisualEffectsManager();
    private final Map<JLabel, Timer> activeAnimationTimers = new HashMap<>();
    private final Map<JLabel, String> runningAnimationIDs = new HashMap<>();
    private final List<JLabel> lockedLabels = new ArrayList<>();
    private MainInterface mainView;

    private VisualEffectsManager() {}

    public static VisualEffectsManager getInstance() {
        return INSTANCE;
    }

    // TODO: when animation is finished, delete the frames and refresh
    public void startSpriteAnimation(String animationId, JLabel displayLabel, Runnable onFinish, boolean isTemporary) {
        // check if any existing animations are playing on displayLabel then stop it
        if (activeAnimationTimers.containsKey(displayLabel)) {
            Timer oldTimer = activeAnimationTimers.get(displayLabel);
            if (oldTimer.isRunning()) {
                oldTimer.stop();
            }
            // Clean up the maps so we start fresh
            activeAnimationTimers.remove(displayLabel);
            runningAnimationIDs.remove(displayLabel);
        }

        Animation animation = AssetManager.getInstance().getAnimation(animationId);

        if (animation == null) {
            LogManager.log("Error: Animation ID not found: " + animationId, Color.RED);
            // Ensure callback runs even if error, to prevent soft-lock
            if (onFinish != null) onFinish.run();
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
                        runningAnimationIDs.remove(displayLabel);

                        displayLabel.setIcon(null);
                        LogManager.log("animation finished: " + animationId);

                        if (isTemporary) {
                            lockedLabels.remove(displayLabel);
                            if (mainView != null) mainView.refreshUI();
                        }

                        if (onFinish != null) {
                            onFinish.run();
                            if (mainView != null) mainView.refreshUI();
                        }
                        return;
                    }
                    displayLabel.setIcon(animation.getNextFrame());
                    displayLabel.revalidate();
                    displayLabel.repaint();
                }
            });

        activeAnimationTimers.put(displayLabel, timer);
        runningAnimationIDs.put(displayLabel, animationId);

        if (isTemporary) {
            lockedLabels.add(displayLabel);
        }

        LogManager.log("Starting animation: " + animationId);
        timer.start();
    }

    public void stopAnimationById(String animationId) {
        List<JLabel> labelsToStop = new ArrayList<>();
        for (Map.Entry<JLabel, String> entry : runningAnimationIDs.entrySet()) {
            if (entry.getValue().equals(animationId)) {
                labelsToStop.add(entry.getKey());
            }
        }
        for (JLabel label : labelsToStop) {
            Timer timer = activeAnimationTimers.get(label);
            if (timer != null) timer.stop();
            label.setIcon(null);
            label.repaint();
            activeAnimationTimers.remove(label);
            runningAnimationIDs.remove(label);
            lockedLabels.remove(label);
        }
    }

    /**
     * Public API: Finds the visual component, runs the animation, and executes a callback.
     * @param animationId The ID of the animation.
     * @param target The Character whose component should receive the animation.
     * @param onFinish The Runnable to execute upon animation completion. <<< NEW PARAMETER
     */
    public void playAnimationOnCharacter(String animationId, Character target, Runnable onFinish, boolean isTemporary) {
        JLabel overlay = getOverlayComponent(target);

        if (overlay != null) {
            startSpriteAnimation(animationId, overlay, onFinish, isTemporary);
        } else {
            if (onFinish != null) {
                onFinish.run();
            }
            LogManager.log("Warning: Visual component not found. Executing callback immediately.", java.awt.Color.ORANGE);
        }
    }

    public void playAnimation(String animationId, Character target, Runnable onFinish, boolean isTemporary) {
        JLabel overlay = getDisplayComponent(target);

        if (overlay != null) {
            startSpriteAnimation(animationId, overlay, onFinish, isTemporary);
        } else {
            if (onFinish != null) {
                onFinish.run();
            }
            LogManager.log("Warning: Visual component not found. Executing callback immediately.", java.awt.Color.ORANGE);
        }
    }

    public JLabel getDisplayComponent(Character character) {
        if (mainView == null) {
            LogManager.log("ERROR: VEM is not linked to MainInterface (mainView is null).", java.awt.Color.RED);
            return null;
        }

        CharacterStatusPanel panel = mainView.getCharacterPanel(character);
        if (panel != null) {
            return panel.getIconDisplayLabel();
        }

        LogManager.log("Warning: UI Panel not found for character: " + character.getName(), java.awt.Color.ORANGE);
        return null;
    }

    public JLabel getOverlayComponent(Character character) {
        if (mainView == null) {
            LogManager.log("ERROR: VEM is not linked to MainInterface (mainView is null).", java.awt.Color.RED);
            return null;
        }

        CharacterStatusPanel panel = mainView.getCharacterPanel(character);
        if (panel != null) {
            return panel.getOverlayDisplayLabel();
        }

        LogManager.log("Warning: UI Panel not found for character: " + character.getName(), java.awt.Color.ORANGE);
        return null;
    }

    // tells if animation is static or not
    public void applyVisual(VisualAsset asset, JLabel displayLabel, boolean isTemporary) {
        if (lockedLabels.contains(displayLabel) && !isTemporary) {
            return;
        }


        // check if the right animation is already running
        if (asset.isAnimation() && runningAnimationIDs.containsKey(displayLabel)) {
            String currentId = runningAnimationIDs.get(displayLabel);

            if (currentId != null && currentId.equals(asset.key())) {
                return;
            }
        }

        // stop old timer
        if (activeAnimationTimers.containsKey(displayLabel)) {
            Timer oldTimer = activeAnimationTimers.get(displayLabel);
            oldTimer.stop();
            activeAnimationTimers.remove(displayLabel);
            runningAnimationIDs.remove(displayLabel);
        }

        if (asset.isAnimation()) {
            startSpriteAnimation(asset.key(), displayLabel, null, isTemporary);
        } else {
            // only set static images if not locked
            if (!lockedLabels.contains(displayLabel)) {
                int w = 100;
                int h = 100;
                ImageIcon staticIcon = AssetManager.getInstance().getImage(asset.key(), w, h);
                displayLabel.setIcon(staticIcon);
            }
        }
    }

    public void stopAllTimers() {
        for (Timer timer : activeAnimationTimers.values()) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }

        activeAnimationTimers.clear();
        runningAnimationIDs.clear();


        LogManager.log("All active animation timers have been stopped.", java.awt.Color.BLUE);
    }

    public void setMainView(MainInterface mainView) {
        this.mainView = mainView;
    }
}
