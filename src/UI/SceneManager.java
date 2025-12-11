package UI;

import UI.Components.ScreenFader;

import javax.swing.*;
import java.util.Stack;

/**
 * as the name suggests, it manages scenes
 * this is when i realized that glass panes are super cool, and THEY STACK SO THEY
 * USE A STACK OMG GFGFGF
 */
public class SceneManager {
    private static final SceneManager INSTANCE = new SceneManager();

    private JFrame gameWindow;
    private JLayeredPane layeredPane;
    private ScreenFader fader;

    // a stack of currently open overlays
    private final Stack<JPanel> overlayStack = new Stack<>();

    private SceneManager() {}

    public static SceneManager getInstance() { return INSTANCE; }

    public void initialize(JFrame gameWindow) {
        this.gameWindow = gameWindow;
        this.layeredPane = gameWindow.getLayeredPane();

        this.fader = new ScreenFader();
        gameWindow.setGlassPane(fader);
    }

    // scene management
    public void transitionTo(JPanel newScene) {
        if (gameWindow == null || fader == null) {
            if (gameWindow != null) {
                gameWindow.setContentPane(newScene);
                gameWindow.revalidate();
                gameWindow.repaint();
            }
            return;
        }

        // --- THE FADE LOGIC ---
        closeAllOverlays();

        Runnable onMidpoint = () -> {
            gameWindow.setContentPane(newScene);
            gameWindow.revalidate();
            gameWindow.repaint();
        };

        fader.startTransition(onMidpoint);
    }

    // overlay management
    public void showOverlay(JPanel overlayPanel) {
        if (layeredPane == null) return;

        // hides the previous top overlay if one exists at all
        if (!overlayStack.isEmpty()) {
            overlayStack.peek().setVisible(false);
        }

        overlayPanel.setBounds(0, 0, gameWindow.getWidth(), gameWindow.getHeight());
        layeredPane.add(overlayPanel, JLayeredPane.POPUP_LAYER);
        overlayStack.push(overlayPanel);

        gameWindow.revalidate();
        gameWindow.repaint();
    }

    public void goBack() {
        if (layeredPane == null || overlayStack.isEmpty()) return;

        JPanel topPanel = overlayStack.pop();
        layeredPane.remove(topPanel);

        if (!overlayStack.isEmpty()) {
            overlayStack.peek().setVisible(true);
        }

        gameWindow.revalidate();
        gameWindow.repaint();
    }

    public void closeAllOverlays() {
        while (!overlayStack.isEmpty()) {
            goBack();
        }
    }
}
