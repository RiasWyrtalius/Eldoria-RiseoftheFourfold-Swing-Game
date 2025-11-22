package UI;

import Core.LogManager;

import java.util.List;
import javax.swing.*;
import java.awt.*;

public class MainInterface extends JFrame{
    private JPanel heroPanel;
    private JPanel enemyPanel;

    private JPanel contentPanel;
    private JTextArea GameLogPanelTextArea;

    private JPanel sample;

    private List<JPanel> heroPartyPanel;
    private List<JPanel> enemyPartyPanel;

    public MainInterface() {
        JFrame frame = new JFrame();
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        GameLogPanelTextArea.setEditable(false);
        LogManager.initialize(GameLogPanelTextArea);

        try {
            ImageIcon originalIcon = new ImageIcon("Assets/Images/bstudios.png");

            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // e.g., to 100x100
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            JLabel imageLabel = new JLabel(scaledIcon); // Use the icon here
            sample.add(imageLabel);
            sample.revalidate();
            sample.repaint();

        } catch (Exception e) {
            LogManager.log("Error loading image: " + e.getMessage());
        }

    }
}
