package UI.Views;

import Resource.Animation.AssetManager;
import UI.Components.StyledButton;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattleSummary extends JPanel {
    private JPanel panel;
    private JTextPane summaryPanel;
    private JLabel resultLabel;
    private JButton firstButton;
    private JButton secondButton;
    private JLabel textLabel;

    private final Font titleFont;
    private final Font bodyFont;

    public BattleSummary() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        this.setBackground(new Color(0, 0, 0, 200));

        // Block input to underlying layers
//        this.addMouseListener(new MouseAdapter() {}); NO MORE

        titleFont = AssetManager.getInstance().getFont("Vecna", 42f);
        bodyFont =  AssetManager.getInstance().getFont("Vecna",18f);

        if (panel == null) createUIComponents();

        resultLabel.setFont(titleFont);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        summaryPanel.setOpaque(false);
        summaryPanel.setEditable(false);
        summaryPanel.setFont(bodyFont);
        summaryPanel.setForeground(Color.WHITE);

        if (textLabel != null) {
            textLabel.setFont(bodyFont.deriveFont(14f));
            textLabel.setForeground(new Color(200, 200, 200));
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        this.add(panel);
    }

    private void createUIComponents() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background
                g2.setColor(new Color(10, 10, 15, 240));
                g2.fillRoundRect(5, 5, w - 10, h - 10, 25, 25);

                // Outer Border
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(80, 80, 80));
                g2.drawRoundRect(5, 5, w - 10, h - 10, 25, 25);

                // Inner Gold Border
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(218, 165, 32));
                g2.drawRoundRect(8, 8, w - 16, h - 16, 20, 20);
            }
        };
        firstButton = new StyledButton("Descend");
        secondButton = new StyledButton("Recuperate");
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void setSummaryData(String title, String details) {
        resultLabel.setText(title);
        if (title.equalsIgnoreCase("DEFEAT")) {
            resultLabel.setForeground(new Color(220, 20, 60));
            if (textLabel != null) textLabel.setText("");
        } else {
            resultLabel.setForeground(new Color(255, 215, 0));
        }

        summaryPanel.setText(details);
        centerText(summaryPanel);
    }

    public void configFirstButton(String buttonText, Runnable action) {
        if (firstButton == null) return;

        firstButton.setText(buttonText);

        for (ActionListener al : firstButton.getActionListeners()) {
            firstButton.removeActionListener(al);
        }

        firstButton.addActionListener(e -> {
            UI.SceneManager.getInstance().goBack();
            if (action != null) action.run();
        });
    }

    public void configSecondButton(String buttonText, Runnable action) {
        if (secondButton == null) return;

        secondButton.setText(buttonText);

        for (ActionListener al : secondButton.getActionListeners()) {
            secondButton.removeActionListener(al);
        }

        secondButton.addActionListener(e -> {
            UI.SceneManager.getInstance().goBack();
            if (action != null) action.run();
        });
    }

    private void centerText(JTextPane pane) {
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }
}