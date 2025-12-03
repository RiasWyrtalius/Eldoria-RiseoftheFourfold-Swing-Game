package UI.Components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;


public class AnimatedStatBar extends JPanel {
    private int currentValue = 0;
    private int targetValue = 0;
    private int maxValue;
    private String labelText;
    private Color barColor;
    private Timer animationTimer;

    public AnimatedStatBar(int maxValue, Color barColor, String labelText) {
        this.maxValue = maxValue;
        this.barColor = barColor;
        this.labelText = labelText;

        //this is for the bar size
        this.setPreferredSize(new Dimension(200, 25));
        this.setOpaque(false);

        animationTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentValue != targetValue) {
                    int step = (targetValue - currentValue) / 10;

                    if (step == 0) {
                        step = (targetValue > currentValue) ? 1 : -1;
                    }

                    currentValue += step;

                    if (Math.abs(targetValue - currentValue) < 2) {
                        currentValue = targetValue;
                        animationTimer.stop();
                    }
                    repaint();
                } else {
                    animationTimer.stop();
                }
            }
        });
    }

    //prevention for division by zeroes
    public void setMaxValue(int max) {
        this.maxValue = max;
        if (this.maxValue <= 0) {
            this.maxValue = 1;
        }
        repaint();
    }

    public void setValue(int newValue) {
        this.targetValue = newValue;
        if (targetValue > maxValue) targetValue = maxValue;
        if (targetValue < 0) targetValue = 0;
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        //for empty bar
        g2.setColor(new Color(50, 50, 50));
        g2.fillRoundRect(0, 0, width, height, 10, 10);

        //bar not empty
        if (maxValue == 0) maxValue = 1;
        double percentage = (double) currentValue / maxValue;
        int fillWidth = (int) (width * percentage);

        g2.setColor(barColor);
        g2.fillRoundRect(0, 0, fillWidth, height, 10, 10);

        //borders for bar
        g2.setColor(new Color(30, 30, 30));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, width, height, 10, 10);

        //text
        String text = labelText + " " + currentValue; // + " / " + maxValue
        g2.setFont(new Font("JetBrains Mono", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();

        int textX = (width - fm.stringWidth(text)) / 2;
        int textY = (height - fm.getHeight()) / 2 + fm.getAscent();

        //text shadow & color
        g2.setColor(Color.BLACK);
        g2.drawString(text, textX + 1, textY + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(text, textX, textY);
    }
}
