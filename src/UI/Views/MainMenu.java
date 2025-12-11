package UI.Views;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import Core.GameFlow.SaveManager;
import Core.GameManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Audio.AudioManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainMenu extends JPanel {
    private final GameManager manager;

    private JPanel contentPanel;
    private JButton exitButton;
    private JButton startButton;
    private JButton continueButton;
    private JPanel title_Panel;
    private JPanel ngc_Panel;
    private JPanel exit_Panel;
    private JLabel titleLabel;

    private final Color INACTIVE_STATE = new Color(169, 169, 169);
    private final Color ACTIVE_STATE = new Color(211, 211, 211);
    private final Color GOLD_COLOR = new Color(255, 215, 0);

    // Constant Paths
    private static final String FONT_PATH = "/Assets/Fonts/vecna.ttf";
    private static final String BG_IMAGE_PATH = "/Assets/Images/MainMenu/Elordia_BG.png";

    public MainMenu(GameManager manager) {
        this.manager = manager;

        this.setLayout(new BorderLayout());
        if (contentPanel != null) {
            this.add(contentPanel, BorderLayout.CENTER);
        }

        AudioManager audio = AudioManager.getInstance();
        audio.playMusic("MAIN-THEME");

        createUIComponents();

        try (InputStream is = getClass().getResourceAsStream(FONT_PATH)) {
            if (is != null) {
                Font fontBase = Font.createFont(Font.TRUETYPE_FONT, is);

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(fontBase);

                Font buttonFont = fontBase.deriveFont(50f);
                addHoverEffect(startButton, buttonFont);
                addHoverEffect(continueButton, buttonFont);
                addHoverEffect(exitButton, buttonFont);

                if (titleLabel != null) {
                    titleLabel.setFont(fontBase.deriveFont(96f));
                    titleLabel.setForeground(GOLD_COLOR);
                }

                System.out.println("Font loaded successfully via Stream!");
            } else {
                System.err.println("FONT ERROR: Could not find resource in Classpath: " + FONT_PATH);
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading font: " + e.getMessage());
            e.printStackTrace();
        }

        if (SaveManager.hasSaveFile()) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }

        contentPanel.setOpaque(true);

        makeTransparent(ngc_Panel);
        makeTransparent(exit_Panel);
        this.setVisible(true);

        // TODO: play music HERE!

        continueButton.addActionListener(e -> {
            audio.playSound("BUTTON_SELECT");
            AudioManager.getInstance().stopMusic();
            manager.loadSavedGame();
        });

        exitButton.addActionListener(e -> {
            audio.playSound("BUTTON_SELECT");
            AudioManager.getInstance().stopMusic();
            VisualEffectsManager.getInstance().stopAllTimers();
            System.exit(0);
        });

        startButton.addActionListener(e -> {
            audio.playSound("BUTTON_SELECT");
            AudioManager.getInstance().stopMusic();
            manager.startNewGame();
        });
    }

    private void addHoverEffect(JButton button, Font font) {
        button.setFont(font);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        String originalText = button.getText();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    AudioManager.getInstance().playSound("BUTTON_HOVER");
                    button.setForeground(ACTIVE_STATE);
                    button.setText("> " + originalText + " <"); // Add symbols
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setForeground(INACTIVE_STATE);
                    button.setText(originalText); // Restore text
                }
            }
        });
    }

    private void makeTransparent(JComponent panel) {
        panel.setOpaque(false);

        if (panel instanceof JScrollPane) {
            ((JScrollPane) panel).getViewport().setOpaque(false);
            ((JScrollPane) panel).setBorder(null); // Optional: removes the thin border
        }

        for (Component child : panel.getComponents()) {
            if (child instanceof JComponent) {
                makeTransparent((JComponent) child);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1280, 720);
    }

    private void createUIComponents() {
        Image bgImage = null;
        try {
            URL imgUrl = getClass().getResource(BG_IMAGE_PATH);
            if (imgUrl != null) {
                bgImage = ImageIO.read(imgUrl);
            } else {
                System.err.println("IMAGE ERROR: Could not find background at " + BG_IMAGE_PATH);
            }
        } catch (IOException e) {
            System.err.println("Could not load image: " + e.getMessage());
        }

        final Image finalBgImage = bgImage;

        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) {
                    g.drawImage(finalBgImage, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
        };
        contentPanel.setOpaque(true);
    }
}
