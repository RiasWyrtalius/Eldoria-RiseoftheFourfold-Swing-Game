package Core.Utils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

public class LogManager {
    private static JTextPane logComponent;
    private static JTextPane logHighlightComponent;
    private static final int DEFAULT_HIGHLIGHT_DURATION = 1500;
    private static Timer highlightClearTimer;

    private static final String FONT_PATH = "/Assets/Fonts/vecna.ttf";
    public static String CUSTOM_FONT_FAMILY = "SansSerif";

    public static void initialize(JTextPane component, JTextPane highlightComponent) {
        logComponent = component;
        logHighlightComponent = highlightComponent;
        loadCustomFont();
    }

    private static void loadCustomFont() {
        try {
            InputStream is = LogManager.class.getResourceAsStream(FONT_PATH);
            if (is == null) {
                System.err.println("Error: Font file not found at " + FONT_PATH);
                return;
            }

            Font vecnaFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(vecnaFont);

            CUSTOM_FONT_FAMILY = vecnaFont.getFamily();

            System.out.println("Loaded Custom Font: " + CUSTOM_FONT_FAMILY);

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.err.println("Failed to load custom font. Using default.");
        }
    }

    // ================== STANDARD LOGS ==================

    public static void log(String message) {
        log(message, Color.BLACK);
    }

    public static void log(String message, Color color) {
        // Pass null for fontFamily to use default
        appendToPane(logComponent, message + "\n", color, false, 13, null);
    }

    // New method to log with a specific font (useful for ASCII art boxes)
    public static void logWithFont(String message, Color color, String fontFamily) {
        appendToPane(logComponent, message + "\n", color, false, 13, fontFamily);
    }

    // ================== HIGHLIGHT LOGS ==================

    public static void logHighlight(String message, boolean persist) {
        logHighlight(message, Color.BLACK, persist);
    }

    public static void logHighlight(String message, Color color, boolean persist) {
        logHighlight(message, color, 20, null, persist);
    }

    public static void logHighlight(String message, Color color, int fontSize, String fontFamily, boolean persist) {
        if (highlightClearTimer != null && highlightClearTimer.isRunning()) {
            highlightClearTimer.stop();
        }

        clearHighlight();

        appendToPane(logHighlightComponent, message, color, true, fontSize, fontFamily);

        if (!persist) {
            highlightClearTimer = new Timer(DEFAULT_HIGHLIGHT_DURATION, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearHighlight();
                }
            });

            highlightClearTimer.setRepeats(false);
            highlightClearTimer.start();
        }
    }

    public static void clearHighlight() {
        if (logHighlightComponent != null) {
            SwingUtilities.invokeLater(() -> {
                logHighlightComponent.setText("");
            });
        }
    }

    // ================== CORE LOGIC ==================

    /**
     * Updated to accept String fontFamily
     */
    public static void appendToPane(JTextPane pane, String message, Color color, boolean isCentered, int fontSize, String fontFamily) {
        if (logComponent != null || logHighlightComponent != null) {
            SwingUtilities.invokeLater(() -> {
                try {
                    StyledDocument doc = pane.getStyledDocument();
                    SimpleAttributeSet charStyle = new SimpleAttributeSet();
                    StyleConstants.setForeground(charStyle, color);
                    StyleConstants.setFontSize(charStyle, fontSize);

                    if (fontFamily != null && !fontFamily.isEmpty()) { StyleConstants.setFontFamily(charStyle, fontFamily); }

                    int lengthBefore = doc.getLength();
                    doc.insertString(lengthBefore, message, charStyle);

                    if (isCentered) {
                        SimpleAttributeSet paraStyle = new SimpleAttributeSet();
                        StyleConstants.setAlignment(paraStyle, StyleConstants.ALIGN_CENTER);
                        doc.setParagraphAttributes(lengthBefore, message.length(), paraStyle, false);
                    } else {
                        SimpleAttributeSet leftStyle = new SimpleAttributeSet();
                        StyleConstants.setAlignment(leftStyle, StyleConstants.ALIGN_LEFT);
                        doc.setParagraphAttributes(lengthBefore, message.length(), leftStyle, false);
                    }
                    pane.setCaretPosition(doc.getLength());

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}