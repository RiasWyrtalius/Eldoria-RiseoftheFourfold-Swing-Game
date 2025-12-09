package Core.Visuals;

import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Resource.Animation.Animation;
import Resource.Animation.AssetManager;
import UI.Components.CharacterStatusPanel;
import UI.Components.OutlinedLabel;
import UI.Views.BattleInterface;

import Characters.Character;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static Core.Utils.ImageUtils.tintImage;

/**
 * Acts as a controller bridge between game state and the view's initialization logic
 */
public class VisualEffectsManager {
    private static final VisualEffectsManager INSTANCE = new VisualEffectsManager();

    private final Map<JLabel, Timer> activeAnimationTimers = new HashMap<>();
    private final Map<JLabel, String> runningAnimationIDs = new HashMap<>();
    private final List<JLabel> lockedLabels = new ArrayList<>();
    private final List<JLabel> hiddenLabels = new ArrayList<>();

    private boolean isGlobalPaused = false;

    private BattleInterface mainView;

    private VisualEffectsManager() {}

    public static VisualEffectsManager getInstance() {
        return INSTANCE;
    }

    // TODO: when animation is finished, delete the frames and refresh
    public void startSpriteAnimation(String animationId, JLabel displayLabel, Runnable onFinish, boolean isTemporary) {
        if (hiddenLabels.contains(displayLabel)) return;
        stopAnimationOnComponent(displayLabel);

        Animation animation = AssetManager.getInstance().getAnimation(animationId);
        if (animation == null) {
            LogManager.log("Error: Animation ID not found: " + animationId, LogFormat.SYSTEM_WARNING);
            // Ensure callback runs even if error, to prevent soft-lock
            if (onFinish != null) onFinish.run();
            return;
        }

        animation.reset();
        displayLabel.setIcon(animation.getCurrentFrame());

        Timer timer = new Timer(animation.getFrameDurationMs(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // assume if animation is paused then animation is not finished
                if (isGlobalPaused) return;

                if (animation.isFinished()) {
                    Timer finishedTimer = (Timer)e.getSource();
                    finishedTimer.stop();
                    activeAnimationTimers.entrySet().removeIf(entry -> entry.getValue() == finishedTimer);
                    runningAnimationIDs.remove(displayLabel);

                    displayLabel.setIcon(null);
//                  LogManager.log("animation finished: " + animationId);

                    // only unclock if its temporary and dont touch hidden labels
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
                if (animation.isPausedFlag()) return;
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

        if (isGlobalPaused) {
            LogManager.log("Animation queued (System Paused): " + animationId, LogFormat.SYSTEM);
        } else {
            timer.start();
        }
    }

    /**
     * Stops every instance of an animation
     * @param animationId
     */
    public void stopAllAnimationsById(String animationId) {
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
     * Stops any active animation timer running on the specific JLabel
     * and cleans up da maps and temp locks and stuff idk
     * @param displayLabel
     */
    public void stopAnimationOnComponent(JLabel displayLabel) {
        if (activeAnimationTimers.containsKey(displayLabel)) {
            Timer timer = activeAnimationTimers.get(displayLabel);

            if (timer != null && timer.isRunning()) {
                timer.stop();
            }

            activeAnimationTimers.remove(displayLabel);
            runningAnimationIDs.remove(displayLabel);
        }

        // release so updates can take over
        lockedLabels.remove(displayLabel);
    }

    /**
     * Finds the visual component, runs the animation, and executes a callback.
     * @param animationId The ID of the animation.
     * @param target The Character whose component should receive the animation.
     * @param onFinish The Runnable to execute upon animation completion.
     */
    public void playAnimationOnCharacter(String animationId, Character target, Runnable onFinish, boolean isTemporary) {
        JLabel overlay = getOverlayComponent(target);

        if (overlay != null) {
            startSpriteAnimation(animationId, overlay, onFinish, isTemporary);
        } else {
            if (onFinish != null) {
                onFinish.run();
            }
            LogManager.log("Warning: Visual component not found. Executing callback immediately.", LogFormat.SYSTEM_WARNING);
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
            LogManager.log("Warning: Visual component not found. Executing callback immediately.", LogFormat.SYSTEM_WARNING);
        }
    }

    public void pauseAnimation(String animationId) {
        // check if animation is playing
        Animation animation = AssetManager.getInstance().getAnimation(animationId);
        if (animation != null) {
            animation.pauseAnimation();
        } else {
            LogManager.log("Animation " + animationId + " does not exist!", LogFormat.SYSTEM);
        }
    }

    public void resumeAnimation(String animationId) {
        // check if animation is playing
        Animation animation = AssetManager.getInstance().getAnimation(animationId);
        if (animation != null) {
            animation.continueAnimation();
        } else {
            LogManager.log("Animation " + animationId + " does not exist!", LogFormat.SYSTEM);
        }
    }

    public void pauseAllAnimations() {
        isGlobalPaused = true;

        for (Timer timer : activeAnimationTimers.values()) {
            if (timer.isRunning()) {
                timer.stop();
            }
        }
        LogManager.log("System: All visuals PAUSED.", LogFormat.SYSTEM);
    }

    public void resumeAllAnimations() {
        isGlobalPaused = false;

        for (Timer timer : activeAnimationTimers.values()) {
            // Restart the timer (it continues from current state)
            timer.start();
        }
        LogManager.log("System: All visuals RESUMED.", LogFormat.DEBUG_INFO);
    }

    public void pauseCharacterAnimation(Character character) {
        JLabel displayLabel = getDisplayComponent(character);

        if (displayLabel == null) {
            LogManager.log("Cannot pause: UI component not found for " + character.getName(), LogFormat.SYSTEM);
            return;
        }

        if (activeAnimationTimers.containsKey(displayLabel)) {
            Timer timer = activeAnimationTimers.get(displayLabel);

            if (timer.isRunning()) {
                timer.stop();
                LogManager.log("Paused visual for " + character.getName(), LogFormat.DEBUG_INFO);
            }
        }
    }

    public void resumeCharacterAnimation(Character character) {
        JLabel displayLabel = getDisplayComponent(character);

        if (displayLabel == null) {
            LogManager.log("Cannot resume: UI component not found for " + character.getName(), LogFormat.SYSTEM);
            return;
        }

        if (activeAnimationTimers.containsKey(displayLabel)) {
            Timer timer = activeAnimationTimers.get(displayLabel);

            if (!timer.isRunning()) {
                timer.start();
                LogManager.log("Resumed visual for " + character.getName(), LogFormat.DEBUG_INFO);
            }
        }
    }

    public void hideCharacterVisual(Character character) {
//        LogManager.log("Hiding character: " + character.getName(), LogColor.SYSTEM);
        JLabel display = getDisplayComponent(character);
        hideVisual(display);
    }

    public void restoreCharacterVisual(Character character) {
//        LogManager.log("Showing character: " + character.getName(), LogColor.SYSTEM);
        JLabel display = getDisplayComponent(character);
        restoreVisual(display);
    }



    public JLabel getDisplayComponent(Character character) {
        if (mainView == null) {
            LogManager.log("ERROR: VEM is not linked to BattleInterface (mainView is null).", LogFormat.SYSTEM_ERROR);
            return null;
        }

        CharacterStatusPanel panel = mainView.getCharacterPanel(character);
        if (panel != null) {
            return panel.getIconDisplayLabel();
        }

        LogManager.log("Warning: UI Panel not found for character: " + character.getName(), LogFormat.SYSTEM_WARNING);
        return null;
    }

    public JLabel getOverlayComponent(Character character) {
        if (mainView == null) {
            LogManager.log("ERROR: VEM is not linked to BattleInterface (mainView is null).", LogFormat.SYSTEM_ERROR);
            return null;
        }

        CharacterStatusPanel panel = mainView.getCharacterPanel(character);
        if (panel != null) {
            return panel.getOverlayDisplayLabel();
        }

        LogManager.log("Warning: UI Panel not found for character: " + character.getName(), LogFormat.SYSTEM_WARNING);
        return null;
    }

    // TODO: due to the usage, i should prolly rename this to apply idle or something
    // tells if animation is static or not
    public void applyVisual(VisualAsset asset, JLabel displayLabel, boolean isTemporary) {
        // check if its hidden
        if (hiddenLabels.contains(displayLabel)) {
            displayLabel.setIcon(null);
            return;
        }

        // checking for temporary lock
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

        // clean up old timers
        stopAnimationOnComponent(displayLabel);

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

    public void hideVisual(JLabel displayLabel) {
        if (displayLabel  == null) return;

        stopAnimationOnComponent(displayLabel);

        displayLabel.setIcon(null);
        displayLabel.repaint();

        if (!hiddenLabels.contains(displayLabel)) {
            hiddenLabels.add(displayLabel);
        }
    }

    public void restoreVisual(JLabel displayLabel) {
        if (displayLabel == null) return;
        hiddenLabels.remove(displayLabel);
        if (mainView != null) mainView.refreshUI();
    }

    public void stopAllTimers() {
        for (Timer timer : activeAnimationTimers.values()) {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
        }

        activeAnimationTimers.clear();
        runningAnimationIDs.clear();


        LogManager.log("All active animation timers have been stopped.", LogFormat.SYSTEM);
    }


    /**
     * Plays the same animation on a list of targets and executes a single callback
     * only after ALL animations in the group have finished.
     *
     * @param animationId   The ID of the animation to play.
     * @param targets       The list of characters to play the animation on.
     * @param onAllComplete The final callback to run once every animation is done.
     * @param isTemporary   If the animation is a temporary effect.
     */
    public void playGroupAnimation(String animationId, List<Character> targets, Runnable onAllComplete, boolean isTemporary) {
        if (targets == null || targets.isEmpty()) {
            if (onAllComplete != null) onAllComplete.run();
            return;
        }

        final int totalTargets = targets.size();
        final int[] animationsFinished = {0};

        for (Character target : targets) {
            Runnable onSingleFinish = () -> {
                animationsFinished[0]++;

                // check if this was the LAST one
                if (animationsFinished[0] >= totalTargets) {
                    if (onAllComplete != null) {
                        onAllComplete.run(); // Trigger the final callback
                    }
                }
            };

            playAnimationOnCharacter(animationId, target, onSingleFinish, isTemporary);
        }
    }

    // --- STATUS EFFECTS ---

    public void applyStatusTint(JLabel displayLabel, String animationId, Color tintColor) {
        Icon currentIcon = displayLabel.getIcon();

        if (currentIcon instanceof ImageIcon) {
            ImageIcon original = (ImageIcon) currentIcon;

            Color transparentTint = new Color(
                    tintColor.getRed(),
                    tintColor.getGreen(),
                    tintColor.getBlue(),
                    120
            );

            ImageIcon tinted = tintImage(original, transparentTint);

            displayLabel.setIcon(tinted);
            displayLabel.repaint();
        }
    }

    public void flashDamage(Character character) {
        if (character == null) return;

        JLabel displayLabel = getDisplayComponent(character);
        if (displayLabel == null) return;

        Icon original = displayLabel.getIcon();
        if (!(original instanceof ImageIcon)) return;

        Color damageColor = new Color(255, 0, 0, 200);
        ImageIcon redIcon = Core.Utils.ImageUtils.tintImage((ImageIcon) original, damageColor);

        int delayMs = 100;

        final int[] tick = {0};

        displayLabel.setIcon(redIcon);
        displayLabel.repaint();

        Timer t = new Timer(delayMs, null);
        t.addActionListener(e -> {
            tick[0]++;

            if (tick[0] == 1) {
                displayLabel.setIcon((ImageIcon) original);
            }
            else if (tick[0] == 2) {
                displayLabel.setIcon(redIcon);
            }
            else if (tick[0] >= 3) {
                displayLabel.setIcon((ImageIcon) original);
                displayLabel.repaint();
                ((Timer)e.getSource()).stop();
                return;
            }

            displayLabel.repaint();
        });

        t.start();
    }

    public void reviveEffect(Character character) {
        if (character == null) return;

        JLabel displayLabel = getDisplayComponent(character);
        if (displayLabel == null) return;

        Icon original = displayLabel.getIcon();
        if (original == null || !(original instanceof ImageIcon)) {
            return;
        }

        int r = 255;
        int g = 255;
        int b = 200;

        int startAlpha = 200;
        int fadeSpeed = 10;
        int delayMs = 40;

        final int[] currentAlpha = {startAlpha};

        Timer timer = new Timer(delayMs, null);
        timer.addActionListener(e -> {
            currentAlpha[0] -= fadeSpeed;

            if (currentAlpha[0] <= 0) {
                displayLabel.setIcon(original);
                displayLabel.repaint();
                ((Timer)e.getSource()).stop();
            } else {
                Color tintColor = new Color(r, g, b, currentAlpha[0]);

                ImageIcon tintedIcon = Core.Utils.ImageUtils.tintImage((ImageIcon) original, tintColor);
                displayLabel.setIcon(tintedIcon);
                displayLabel.repaint();
            }
        });

        timer.start();
    }

    public void showFloatingText(Character target, String text, Color color) {
        JPanel targetPanel = mainView.getCharacterPanel(target);
        if (targetPanel == null) return;

        JRootPane root = SwingUtilities.getRootPane(mainView);
        if (root == null) return;
        JLayeredPane layeredPane = root.getLayeredPane();

        OutlinedLabel label = new OutlinedLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(color);
        label.setOutlineColor(Color.BLACK);
        label.setStrokeWidth(3f);

        Point panelLoc = SwingUtilities.convertPoint(targetPanel, 0, 0, layeredPane);
        Dimension labelSize = label.getPreferredSize();

        int baseY = panelLoc.y - (labelSize.height / 2);
        int randomOffset = Core.Utils.Dice.getInstance().roll(-20, 20);
        int y = baseY + randomOffset;
        int baseX = panelLoc.x + (targetPanel.getWidth() / 2) - (labelSize.width / 2);
        randomOffset = Core.Utils.Dice.getInstance().roll(-20, 20);
        int x = baseX + randomOffset;

        label.setBounds(x, y, labelSize.width, labelSize.height);

        layeredPane.add(label, JLayeredPane.POPUP_LAYER);

        Timer timer = getTimer(label, layeredPane);
        timer.start();
    }

    private static Timer getTimer(OutlinedLabel label, JLayeredPane layeredPane) {
        Timer timer = new Timer(20, null);
        final int[] duration = {0};

        timer.addActionListener(e -> {
            duration[0]++;

            // Move the label up by 1 pixel per tick
            label.setLocation(label.getX(), label.getY() - 1);

            // After ~1.2 seconds (60 ticks), remove it
            if (duration[0] > 60) {
                timer.stop();
                layeredPane.remove(label);
                layeredPane.repaint(label.getBounds()); // Clean up artifacts
            }
        });
        return timer;
    }

    public void setMainView(BattleInterface mainView) {
        this.mainView = mainView;
    }
}
