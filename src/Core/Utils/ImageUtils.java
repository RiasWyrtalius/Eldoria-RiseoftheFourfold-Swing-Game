package Core.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {
    /**
     * Tints an image with a specific color. (OR STATUS DAWG)
     *
     * @param icon  The original ImageIcon.
     * @param color The color to tint with. MUST have Alpha < 255 to see details.
     * @return A new ImageIcon that is tinted.
     */
    public static ImageIcon tintImage(ImageIcon icon, Color color) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        BufferedImage tintedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tintedImage.createGraphics();

        g2d.drawImage(icon.getImage(), 0, 0, null);

        g2d.setComposite(AlphaComposite.SrcAtop);

        g2d.setColor(color);
        g2d.fillRect(0, 0, w, h);

        g2d.dispose();

        return new ImageIcon(tintedImage);
    }
}
