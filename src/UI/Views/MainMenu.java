package UI.Views;

import Core.GameManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Audio.AudioManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainMenu extends JFrame {
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

        //TODO: @samuelcarillogwapito
        AudioManager audio = AudioManager.getInstance();
        audio.registerSound("MENU_THEME", "Assets/Audio/SFX/victory_sound_1.wav");
        audio.playMusic("MENU_THEME");

        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Eldoria: Rise of the Fourfold");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

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
            e.printStackTrace();
        }

        contentPanel.setOpaque(true);

        makeTransparent(ngc_Panel);
        makeTransparent(exit_Panel);
        this.setVisible(true);

        // TODO: play music HERE!

        //  TODO: enable continue button only if content save file is available
        continueButton.setEnabled(false);

        exitButton.addActionListener(e -> {
            System.out.println("...Exiting Application");
            // stop timers before killing JVM for some reason
            VisualEffectsManager.getInstance().stopAllTimers();
            System.exit(0);
        });

        startButton.addActionListener(e -> {
            manager.startNewGame(this);
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
