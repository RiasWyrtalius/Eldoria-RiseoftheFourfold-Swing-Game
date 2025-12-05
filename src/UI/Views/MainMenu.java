package UI.Views;

import java.io.File;

import Core.GameFlow.SaveManager;
import Core.GameManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Audio.AudioManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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

    public MainMenu(GameManager manager) {
        this.manager = manager;

        this.setLayout(new BorderLayout());
        if (contentPanel != null) {
            this.add(contentPanel, BorderLayout.CENTER);
        }

        AudioManager audio = AudioManager.getInstance();
        audio.registerSound("MAIN-THEME", "Assets/Audio/SFX/mainMenu_bgm.wav");
        audio.playMusic("MAIN-THEME");

        if (SaveManager.hasSaveFile()) {
            continueButton.setEnabled(true);
        } else {
            continueButton.setEnabled(false);
        }

        try {
            File fontFile = new File("Assets/Fonts/Vecna.ttf");

            if (fontFile.exists()) {
                Font elordiaFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(96f);


                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(elordiaFont);


                titleLabel.setFont(elordiaFont);
                titleLabel.setForeground(new Color(255, 215, 0));

                System.out.println("Font loaded successfully!");
            } else {
                System.err.println("Error: Font file not found at " + fontFile.getAbsolutePath());
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading font: " + e.getMessage());
        }

        contentPanel.setOpaque(true);

        makeTransparent(ngc_Panel);
        makeTransparent(exit_Panel);
        this.setVisible(true);

        // TODO: play music HERE!

        continueButton.addActionListener(e -> {
            AudioManager.getInstance().stopMusic();
            manager.loadSavedGame();
        });

        exitButton.addActionListener(e -> {
            AudioManager.getInstance().stopMusic();
            System.out.println("...Exiting Application");
            // stop timers before killing JVM for some reason
            VisualEffectsManager.getInstance().stopAllTimers();
            System.exit(0);
        });

        startButton.addActionListener(e -> {
            AudioManager.getInstance().stopMusic();
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            manager.startNewGame(parentFrame);
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
        Image tempImage = null;
        try {
            tempImage = ImageIO.read(new File("Assets/Images/MainMenu/Elordia_BG.png"));
        } catch (IOException e) {
            System.err.println("Could not load image: " + e.getMessage());
        }

        Image bgImage = tempImage;

        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
        };
        contentPanel.setOpaque(true);
    }
}
