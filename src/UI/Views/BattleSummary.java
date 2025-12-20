package UI.Views;

import UI.SceneManager;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

public class BattleSummary extends JPanel {
    private JPanel panel;
    private JTextPane summaryPanel;
    private JLabel resultLabel;
    private JButton descendButton;
    private JLabel textLabel;

    private final Font titleFont;
    private final Font bodyFont;
    private final Font buttonFont;

    public BattleSummary() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        this.setBackground(new Color(0, 0, 0, 200));

        // Block input to underlying layers
//        this.addMouseListener(new MouseAdapter() {}); NO MORE

        titleFont = getVecnaFont(42f);
        bodyFont = getVecnaFont(18f);
        buttonFont = getVecnaFont(24f);

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

        styleRPGButton(descendButton);

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
        panel.setPreferredSize(new Dimension(600, 500));
        panel.setMinimumSize(new Dimension(600, 500));
        panel.setOpaque(false);
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

    public void configureButton(String buttonText, Runnable action) {
        if (descendButton == null) return;

        descendButton.setText(buttonText);

        for (ActionListener al : descendButton.getActionListeners()) {
            descendButton.removeActionListener(al);
        }

        descendButton.addActionListener(e -> {
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

    private void styleRPGButton(JButton btn) {
        if (btn == null) return;
        btn.setFont(buttonFont);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(5, 20, 5, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(80, 80, 80));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                        BorderFactory.createEmptyBorder(5, 20, 5, 20)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(40, 40, 40));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        BorderFactory.createEmptyBorder(5, 20, 5, 20)
                ));
            }
        });
    }

    private Font getVecnaFont(float size) {
        try (InputStream is = getClass().getResourceAsStream("/Assets/Fonts/vecna.ttf")) {
            if (is == null) return new Font("Serif", Font.BOLD, (int)size);
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            return new Font("Serif", Font.BOLD, (int)size);
        }
    }
}