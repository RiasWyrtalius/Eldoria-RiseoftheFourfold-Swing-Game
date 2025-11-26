package UI.Components;

import Resource.AssetManager;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        this.setLayout(new BorderLayout());
    }

    public void setBackgroundImage(String imageKey) {
        ImageIcon icon = AssetManager.getInstance().getImage(imageKey, 1000, 1000);

        if (icon != null) {
            this.backgroundImage = icon.getImage();
        } else {
            this.backgroundImage = null;
        }

        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ensure opacity and borders work
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D)g;

            g2d.drawImage(
                    backgroundImage,
                    0,0,
                    getWidth(),
                    getHeight(),
                    this
            );
        }
    }
}
