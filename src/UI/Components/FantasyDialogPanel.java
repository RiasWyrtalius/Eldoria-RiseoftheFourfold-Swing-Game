package UI.Components;

import javax.swing.*;
import java.awt.*;

/**
 * Draws a rounded, semi-transparent box with a gold border.
 */
public class FantasyDialogPanel extends JPanel {
    private static final Color GOLD_BORDER = new Color(218, 165, 32);
    private static final Color BG_SHADOW = new Color(0, 0, 0, 210);

    public FantasyDialogPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 30; // Rounded corner radius
        int borderThickness = 3;

        // Add a small margin so the border isn't cut off by the panel edge
        int x = 10;
        int y = 5;
        int w = getWidth() - 20;
        int h = getHeight() - 15;

        g2.setColor(BG_SHADOW);
        g2.fillRoundRect(x, y, w, h, arc, arc);

        g2.setColor(GOLD_BORDER);
        g2.setStroke(new BasicStroke(borderThickness));
        g2.drawRoundRect(x, y, w, h, arc, arc);

        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x + 5, y + 5, w - 10, h - 10, arc, arc);

        g2.dispose();
    }
}