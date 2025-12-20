package UI.Components;

import Resource.Animation.AssetManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StyledButton extends JButton {
    private static final Color BG_NORMAL = new Color(40, 40, 40);
    private static final Color BG_HOVER = new Color(80, 80, 80);

    private static final Color TEXT_COLOR = Color.WHITE;

    private static final Color BORDER_NORMAL_COLOR = new Color(200, 200, 200);
    private static final Color BORDER_HOVER_COLOR = new Color(255, 215, 0); // Gold

    private static final String FONT_ID = "Vecna";
    private static final float FONT_SIZE = 24f;

    public StyledButton(String text) {
        super(text);
        initStyle();
        initBehavior();
    }

    private void initStyle() {
        Font customFont = AssetManager.getInstance().getFont(FONT_ID, FONT_SIZE);
        setFont(customFont);
        setForeground(TEXT_COLOR);

        setBackground(BG_NORMAL);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setBorder(createRPGBorder(BORDER_NORMAL_COLOR));
    }

    private void initBehavior() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(BG_HOVER);
                    setBorder(createRPGBorder(BORDER_HOVER_COLOR));

//                    AudioManager.getInstance().playSound("BUTTON_HOVER");
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(BG_NORMAL);
                    setBorder(createRPGBorder(BORDER_NORMAL_COLOR));
                }
            }
        });
    }

    private Border createRPGBorder(Color lineColor) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(lineColor, 2),
                BorderFactory.createEmptyBorder(5, 20, 5, 20) // Padding
        );
    }
}