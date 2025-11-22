package UI.MainInterface;

import Core.LogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class MainInterface extends JFrame{
    private JPanel contentPanel;
    private JList list1;
    private JTextArea GameLogPanelTextArea;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;

    private JPanel sample;
    private JPanel sample1;
    private JPanel sample2;
    private JPanel sample3;


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
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // e.g., to 100x100
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            JLabel imageLabel = new JLabel(scaledIcon); // Use the icon here

            sample.add(imageLabel);
            sample.revalidate();
            sample.repaint();

            JLabel imageLabel1 = new JLabel(scaledIcon); // Use the icon here
            sample1.add(imageLabel1);
            sample1.revalidate();
            sample1.repaint();

            JLabel imageLabel2 = new JLabel(scaledIcon); // Use the icon here
            sample2.add(imageLabel2);
            sample2.revalidate();
            sample2.repaint();

            JLabel imageLabel3 = new JLabel(scaledIcon); // Use the icon here
            sample3.add(imageLabel3);
            sample3.revalidate();
            sample3.repaint();
        } catch (Exception e) {
            LogManager.log("Error loading image: " + e.getMessage());
        }

    }
}
