package Resource;

import Core.LogColor;
import Core.LogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    /**
     * Singleton
     */
    private static final AssetManager INSTANCE = new AssetManager();
    private final Map<String, ImageIcon> imageCache;

    private AssetManager() {
        imageCache = new HashMap<>();
    }

    public static AssetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Loads an image (ImageIcon) fron given path, caching it if It's not already
     * used. ensures image is loaded from disk only once :)
     * @param key
     * @param width
     * @param height
     * @return
     */
    public ImageIcon getImage(String key, int width, int height) {
        String cachedKey = key + "_" + width + "x" + height;
        if (imageCache.containsKey(cachedKey)) {
            return imageCache.get(cachedKey);
        }

        ImageIcon scaledIcon = null;

        try {
//            load original icon
            ImageIcon originalIcon = new ImageIcon(key);

            if (originalIcon.getIconWidth() == -1) {
                throw new RuntimeException("Image file not found or corrupted: " + key);
            }

//            scale
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            scaledIcon = new ImageIcon(scaledImg);
            imageCache.put(cachedKey, scaledIcon);
            LogManager.log("AssetManager: retrieving cached " + cachedKey, LogColor.SYSTEM);
        } catch (Exception e) {
            LogManager.log("AssetManager ERROR: Failed to load/scale image '" + key + "'. Using fallback Icon");
            scaledIcon = createFallbackIcon(width, height);
            imageCache.put(cachedKey, scaledIcon);
        }
        return scaledIcon;
    }

    /**
     * Creates a simple, visible icon to use when a real asset fails to load.
     */
    private ImageIcon createFallbackIcon(int width, int height) {
        // Simple code to draw a red box with an X inside
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawString("X", width / 2 - 4, height / 2 + 4);
        g.dispose();
        return new ImageIcon(image);
    }


    // TODO: preloading assets, do it after file handling
    public void preLoad() {
        // call getImage() for all known assets
    }
}
