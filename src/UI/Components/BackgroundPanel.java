package UI.Components;

import Resource.Animation.AssetManager;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
//        backgroundLabel = new ScaledLabel();
//        this.setLayout(new BorderLayout());
//        this.add(backgroundLabel, BorderLayout.CENTER);
//        this.setPreferredSize(new Dimension(800, 600));
//        this.setSize(800, 600);
//        backgroundLabel.setSize(800, 600);
    }

    public void setBackgroundImage(String imageKey) {
//        VisualAsset assetData = AssetManager.getInstance().getVisualAssetData(imageKey);

//        VisualEffectsManager.getInstance().applyVisual(assetData, backgroundLabel, false);

        ImageIcon icon = AssetManager.getInstance().getImage(imageKey, 1000, 1000);

        if (icon != null) {
            this.backgroundImage = icon.getImage();
        } else {
            this.backgroundImage = null;
        }

        this.repaint();
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(800, 600);
//    }

//    private static class ScaledLabel extends JLabel {
//        @Override
//        protected void paintComponent(Graphics g) {
//            Icon icon = getIcon();
//
//            if (icon instanceof ImageIcon) {
//                Image image = ((ImageIcon) icon).getImage();
//                Graphics2D g2d = (Graphics2D)g;
//
//                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
//            }
//        }
//    }
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
