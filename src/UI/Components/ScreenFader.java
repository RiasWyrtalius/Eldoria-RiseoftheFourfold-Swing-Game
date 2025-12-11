package UI.Components;

import javax.swing.*;
import java.awt.*;

/**
 * custon jpanel where its job is to sit on the glass pane
 * and draw a rectangle that fades in and out and creates
 * a smooth transition effect. totally not needed but its
 * super cool to have - gops
 *
 * maybe it has other utilities in vfx manager
 */
public class ScreenFader extends JPanel {
    private float alpha = 0.0f;

    private Timer timer;
    private final int FADE_SPEED_MS = 20;
    private final float FADE_STEP = 0.05f; // How much alpha changes per update

    public ScreenFader() {
        setOpaque(false);
        setVisible(false);

        // prevents user from interacting while fading
        addMouseListener(new java.awt.event.MouseAdapter() {});
    }

    /**
     * Overrides the paint method to draw the black rectangle with current alpha.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // No super.paintComponent() call, as we are handling the entire draw.
        Graphics2D g2d = (Graphics2D) g.create();
        // Apply the current transparency level
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        // Draw the black rectangle over the entire component area
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.dispose();
    }

    /**
     * The main public method to start a fade transition.
     * @param onMidpoint A Runnable containing the code to execute when the screen is fully black
     *                   (e.g., swapping the content pane).
     */
    public void startTransition(Runnable onMidpoint) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        setVisible(true);
        alpha = 0.0f;

        // number 15.. burger king f-
        // 1. fade to black
        timer = new Timer(FADE_SPEED_MS, e -> {
            alpha += FADE_STEP;

            if (alpha >= 1.0f) {
                alpha = 1.0f; // Cap at solid black
                repaint();
                ((Timer) e.getSource()).stop();

                try {
                    if (onMidpoint != null) {
                        onMidpoint.run();
                    }
                } finally {
                    startFadeIn();
                }

            } else {
                repaint(); // redraw panel with new alpha
            }
        });

        timer.start();
    }

    private void startFadeIn() {
        // 2. fade to clear
        timer = new Timer(FADE_SPEED_MS, e -> {
            alpha -= FADE_STEP;

            if (alpha <= 0.0f) {
                alpha = 0.0f; // Cap at fully transparent
                repaint();
                ((Timer) e.getSource()).stop();
                setVisible(false); // hides fader panel and re-enables input
            } else {
                repaint();
            }
        });

        timer.start();
    }
}