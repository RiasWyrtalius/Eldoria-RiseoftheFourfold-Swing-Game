package UI.Views;

import Core.GameManager;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BattleSummary { // Removed JDialog inheritance, we are using the panel
    private JPanel panel1;
    private JTextPane summaryPanel;
    private JLabel resultLabel;
    private JButton descendButton;
    private JLabel textLabel;

    private Font titleFont;
    private Font bodyFont;
    private Font buttonFont;

    public BattleSummary() {
        titleFont = getVecnaFont(42f); // Big Title
        bodyFont = getVecnaFont(18f);  // List items
        buttonFont = getVecnaFont(24f); // Button text

        panel1.setOpaque(false);

        resultLabel.setFont(titleFont);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        summaryPanel.setOpaque(false);
        summaryPanel.setEditable(false);
        summaryPanel.setFont(bodyFont);
        summaryPanel.setForeground(Color.WHITE);

        textLabel.setFont(bodyFont.deriveFont(14f));
        textLabel.setForeground(new Color(200, 200, 200)); // Light Gray
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        styleRPGButton(descendButton);
    }

    //
    private Font getVecnaFont(float size) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/Assets/Fonts/vecna.ttf");
            if (is == null) return new Font("Serif", Font.BOLD, (int)size);
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            return new Font("Serif", Font.BOLD, (int)size);
        }
    }

    public JPanel getPanel() {
        return new JPanel(new BorderLayout()) {
            {
                setOpaque(false);
                add(panel1, BorderLayout.CENTER);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(10, 10, 15, 240));
                g2.fillRoundRect(5, 5, w - 10, h - 10, 25, 25);

                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(80, 80, 80));
                g2.drawRoundRect(5, 5, w - 10, h - 10, 25, 25);

                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(218, 165, 32));
                g2.drawRoundRect(8, 8, w - 16, h - 16, 20, 20);

                super.paintComponent(g);
            }
        };
    }

    public void setSummaryData(String title, String details) {
        resultLabel.setText(title);
        if (title.equalsIgnoreCase("DEFEAT")) {
            resultLabel.setForeground(new Color(220, 20, 60)); // Crimson Red
            textLabel.setText("");
        } else {
            resultLabel.setForeground(new Color(255, 215, 0)); // Gold
        }

        summaryPanel.setText(details);

        StyledDocument doc = summaryPanel.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontFamily(center, bodyFont.getFamily());
        StyleConstants.setFontSize(center, 18);

        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    public void configureButton(String buttonText, Runnable action) {
        if (descendButton == null) return;

        descendButton.setText(buttonText);

        // Remove old listeners to prevent stacking
        for (ActionListener al : descendButton.getActionListeners()) {
            descendButton.removeActionListener(al);
        }

        descendButton.addActionListener(e -> {
            JRootPane root = SwingUtilities.getRootPane(descendButton);
            if (root != null) root.getGlassPane().setVisible(false);
            action.run();
        });
    }

    // helpers
    private void styleRPGButton(JButton btn) {
        btn.setFont(buttonFont);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40)); // Dark Grey
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(5, 20, 5, 20) // Padding
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //hover effect
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
}