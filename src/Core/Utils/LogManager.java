package Core.Utils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogManager {
    private static JTextPane logComponent;
    private static JTextPane logHighlightComponent;
    private static final int DEFAULT_HIGHLIGHT_DURATION = 1500;
    private static Timer highlightClearTimer;

    public static void initialize(JTextPane component, JTextPane highlightComponent) {
        logComponent = component;
        logHighlightComponent = highlightComponent;
    }

    public static void log(String message) { //STANDARD LOG
        log(message, Color.BLACK);
    }

    public static void log(String message, Color color) { // COLORED LOGS
        appendToPane(logComponent, message + "\n", color, false, 13);
    }

    public static void logHighlight(String message, boolean persist) {
        logHighlight(message, Color.BLACK, persist);
    }

    public static void logHighlight(String message, Color color, boolean persist) {
        logHighlight(message, color, 20, persist);
    }

    public static void logHighlight(String message, Color color, int fontSize, boolean persist) {
        if (highlightClearTimer != null && highlightClearTimer.isRunning()) {
            highlightClearTimer.stop();
        }

        clearHighlight();
        appendToPane(logHighlightComponent, message, color, true, fontSize);

        if (!persist) {
            highlightClearTimer = new Timer(DEFAULT_HIGHLIGHT_DURATION, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearHighlight();
                }
            });

            highlightClearTimer.setRepeats(false); // Run once
            highlightClearTimer.start();
        }
    }

    // clear the highlight box should use this to start new turns or attacks AND EVERYTHING
    public static void clearHighlight() {
        if (logHighlightComponent != null) {
            SwingUtilities.invokeLater(() -> {
                logHighlightComponent.setText("");
            });
        }
    }

    public static void appendToPane(JTextPane pane, String message, Color color, boolean isCentered, int fontSize) {
        if (logComponent != null || logHighlightComponent != null) {
            SwingUtilities.invokeLater(() -> {
                try {
                    StyledDocument doc = pane.getStyledDocument();

                    SimpleAttributeSet charStyle = new SimpleAttributeSet();
                    StyleConstants.setForeground(charStyle, color);
                    StyleConstants.setFontSize(charStyle, fontSize);

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
        } else {
            System.out.println("LOG (NO UI LINKAGE): " + message);
        }
    }
}
