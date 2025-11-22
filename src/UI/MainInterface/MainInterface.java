package UI.MainInterface;

import Core.LogManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainInterface extends JFrame{
    private JPanel contentPanel;
    private JList list1;
    private JTextArea GameLogPanelTextArea;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;

    public MainInterface() {
        JFrame frame = new JFrame();
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        LogManager.initialize(GameLogPanelTextArea);
    }
}
