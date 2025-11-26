package Resource;

import Core.Utils.LogColor;
import Core.Utils.LogManager;
import Core.Visuals.VisualAsset;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    private AssetManager() {
        imageCache = new HashMap<>();
        animationRepository = new HashMap<>();
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
            LogManager.log("AssetManager ERROR: Failed to load/scale image '" + key + "'. Using fallback Icon", LogColor.SYSTEM);
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
            LogManager.log("Animation ID: " + animationId + " is already registered.", Color.yellow);
            return;
        }

        List<ImageIcon> frames = loadAnimationFrames(basePath, frameCount, width, height);

        Animation newAnimation = new Animation(frames, durationMs, loopType);

        animationRepository.put(animationId, newAnimation);
        LogManager.log("Registered animation: " + animationId);
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

    // TODO: preloading assets, do it after file handling
    public void preLoad() {
        // call getImage() for all known assets
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
}
