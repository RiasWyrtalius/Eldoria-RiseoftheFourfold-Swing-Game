package UI.Views;

import Core.GameManager;
import Core.Visuals.VisualEffectsManager;
import Resource.Audio.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private final GameManager manager;

    private JPanel contentPanel;
    private JButton exitButton;
    private JButton startButton;
    private JButton continueButton;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1280, 720);
    }

    public MainMenu(GameManager manager) {
        this.manager = manager;

        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Elordia: Rise of the Fourfold");

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

//        this.pack();
//        this.setLocationRelativeTo(null); // Center the window
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
}
