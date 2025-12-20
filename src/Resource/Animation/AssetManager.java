package Resource.Animation;

import Core.Utils.LogFormat;
import Core.Utils.LogManager;
import Core.Visuals.VisualAsset;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManager {
    /**
     * Singleton
     */
    private static final AssetManager INSTANCE = new AssetManager();
    private final Map<String, Animation> animationRepository;
    private final Map<String, ImageIcon> imageCache;
    private final Map<String, Font> fontCache;

    private AssetManager() {
        imageCache = new HashMap<>();
        animationRepository = new HashMap<>();
        fontCache = new HashMap<>();
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
            java.net.URL imgUrl = getClass().getResource(key);

            if (imgUrl == null && !key.startsWith("/")) {
                imgUrl = getClass().getResource("/" + key);
            }

            if (imgUrl == null) {
                throw new RuntimeException("Image resource not found in classpath: " + key);
            }

            ImageIcon originalIcon = new ImageIcon(imgUrl);

            if (originalIcon.getIconWidth() == -1) {
                throw new RuntimeException("Image file corrupted or invalid: " + key);
            }

            // scale
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            scaledIcon = new ImageIcon(scaledImg);
            imageCache.put(cachedKey, scaledIcon);
            LogManager.log("(Asset) : retrieving cached " + cachedKey, LogFormat.SYSTEM);

        } catch (Exception e) {
            // This will print the error and generate the Red "X" box
            System.err.println("Failed to load: " + key); // Print to console so you can see why
            LogManager.log("(Asset) : Failed to load/scale image '" + key + "'. Using fallback Icon", LogFormat.SYSTEM_ERROR);
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

    /**
     * Registered a full animation sequence (loaded frames) under a unique ID.
     * @param animationId
     * @param basePath
     * @param frameCount
     * @param width
     * @param height
     * @param durationMs
     */
    public void registerAnimation(String animationId, String basePath, int frameCount, int width, int height, int durationMs, AnimationLoopType loopType) {
        if (animationRepository.containsKey(animationId)) {
            LogManager.log("Animation ID: " + animationId + " is already registered.", LogFormat.SYSTEM);
            return;
        }

        List<ImageIcon> frames = loadAnimationFrames(basePath, frameCount, width, height);

        Animation newAnimation = new Animation(frames, durationMs, loopType);

        animationRepository.put(animationId, newAnimation);
        LogManager.log("Registered animation: " + animationId, LogFormat.SYSTEM);
    }

    /**
     * loads a sequences of images for an animation
     * @param basePath
     * @param frameCount
     * @param width
     * @param height
     * @return A list of scaled image icons
     */
    public List<ImageIcon> loadAnimationFrames(String basePath, int frameCount, int width, int height) {
        List<ImageIcon> frames = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            // fileformat must follow: "assets/fireball/frame_0.png", "assets/fireball/frame_1.png", etc.
            String key = String.format(basePath, i);
            frames.add(getImage(key, width, height));
        }
        return frames;
    }

    /**
     * Register TTF font from resources under a unique ID
     * @param fontID
     * @param basePath file path to resource ex. "/Assets/Fonts/vecna.ttf""
     */
    public void registerFont(String fontID, String basePath) {
        if (fontCache.containsKey(fontID)) {
            LogManager.log("Animation ID: " + fontID + " is already registered.", LogFormat.SYSTEM);
            return;
        }

        try (InputStream is = getClass().getResourceAsStream(basePath)) {
            if (is == null) {
                throw new IOException("Font resource not found: " + basePath);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            fontCache.put(fontID, font);
            LogManager.log("Registered font: " + fontID, LogFormat.SYSTEM);
        } catch (Exception e) {
            LogManager.log("Error loading font: " + fontID + ": " + e.getMessage());
            fontCache.put(fontID, new Font("Serif", Font.BOLD, 12));
        }
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    public Animation getAnimation(String  animationId) {
        return animationRepository.get(animationId);
    }

    public boolean isAnimation(String visualId) {
        return animationRepository.containsKey(visualId);
    }

    /**
     * Retrieves visual data for da given ID.
     * @param visualId
     * @return record containing the ID and boolean flag for animation
     */
    public VisualAsset getVisualAssetData(String visualId) {
        return new VisualAsset(visualId, isAnimation(visualId));
    }

    /**
     * Retrives registered font derived from specific size
     * @param fontID
     * @param size
     * @return
     */
    public Font getFont(String fontID, float size) {
        Font font = fontCache.get(fontID);
        if (font != null) {
            return font.deriveFont(size);
        }

        LogManager.log("Warning font ID [" + fontID + "] not found! Using fallback.", LogFormat.SYSTEM_WARNING);
        return fontCache.get("Serif").deriveFont(size);
    }
}
