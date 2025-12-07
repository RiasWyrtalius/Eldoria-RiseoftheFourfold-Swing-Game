package UI.Views;

import Core.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattleSummary extends JDialog{
    private JPanel panel1;
    private JTextPane summaryPanel;
    private JLabel resultLabel;
    private JButton descendButton;
    private JLabel textLabel;

    public BattleSummary() {

        //main
        panel1.setOpaque(false);
        panel1.setBackground(new Color(0,0,0,0));

        //textpane
        summaryPanel.setOpaque(false);
        summaryPanel.setBackground(new Color(0,0,0,0));

        summaryPanel.setForeground(Color.WHITE);
        resultLabel.setForeground(Color.YELLOW);
        textLabel.setForeground(Color.WHITE);
    }

    public JPanel getPanel() {
        return new JPanel(new BorderLayout()) {
            {
                setOpaque(false);
                add(panel1, BorderLayout.CENTER);
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0,0,0,150));
                g.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                super.paintComponent(g);
            }
        };
    }

    public void listenerInit() {
        if (descendButton != null) {
            descendButton.addActionListener(e -> {
                JRootPane root = SwingUtilities.getRootPane(descendButton);
                if (root != null) root.getGlassPane().setVisible(false);
                GameManager.getInstance().loadNextLevel();
            });
        }
    }

    public void setSummaryData(String title, String details) {
        if (resultLabel != null) resultLabel.setText(title);
        if (summaryPanel!= null) summaryPanel.setText(details);

        if (title.equalsIgnoreCase("DEFEAT")) {
            if (resultLabel != null) resultLabel.setForeground(Color.RED); //lose
        } else {
            if (resultLabel != null) resultLabel.setForeground(Color.YELLOW); //win
        }
    }

    public void configureButton(String buttonText, Runnable action) {
        if (descendButton == null) return;

        descendButton.setText(buttonText);

        for (ActionListener al : descendButton.getActionListeners()) {
            descendButton.removeActionListener(al);
        }

        // Win or Lose
        descendButton.addActionListener(e -> {
            JRootPane root = SwingUtilities.getRootPane(descendButton);
            if (root != null) root.getGlassPane().setVisible(false);
            action.run();
        });
    }
}
