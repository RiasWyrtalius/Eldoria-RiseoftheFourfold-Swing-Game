package Core.GameFlow;

import UI.Components.OutlinedLabel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class VictoryPanel extends JPanel {
    public VictoryPanel(ActionListener onExit) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);

        //Title
        OutlinedLabel title = new OutlinedLabel("CAMPAIGN COMPLETE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(new Color(255, 215, 0)); // Gold
        title.setOutlineColor(Color.BLACK);
        title.setStrokeWidth(4f);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Subtitle
        JLabel subTitle = new JLabel("You have conquered all floors!");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subTitle.setForeground(Color.WHITE);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton exitBtn = new JButton("Return to Title");
        exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exitBtn.setFocusPainted(false);
        exitBtn.setBackground(Color.WHITE);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        exitBtn.addActionListener(onExit);

        contentBox.add(title);
        contentBox.add(Box.createVerticalStrut(15));
        contentBox.add(subTitle);
        contentBox.add(Box.createVerticalStrut(40));
        contentBox.add(exitBtn);

        add(contentBox);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Draw the dark semi-transparent background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }
}
