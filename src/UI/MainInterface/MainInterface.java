package UI.MainInterface;

import Core.LogManager;

import javax.swing.*;

public class MainInterface extends JFrame{
    private JPanel contentPanel;
    private JList list1;
    private JTextArea GameLogPanelTextArea;

    public MainInterface() {
        JFrame frame = new JFrame();
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        LogManager.initialize(GameLogPanelTextArea);
        LogManager.log("hello world!");
    }
}
