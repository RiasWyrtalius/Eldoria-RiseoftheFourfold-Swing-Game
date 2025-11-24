package Core;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class LogManager {
    private static JTextPane logComponent;

    public static void initialize(JTextPane component) {
        logComponent = component;
    }

    public static void log(String message) { //STANDARD LOG
        log(message, Color.BLACK);
    }

    public static void log(String message, Color color) { // COLORED LOGS
        append(message + "\n", color);
    }

    public static void append(final String message, final Color color) {
        if (logComponent != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Logic to add colored text
                        StyledDocument doc = logComponent.getStyledDocument();
                        SimpleAttributeSet style = new SimpleAttributeSet();
                        StyleConstants.setForeground(style, color);

                        // Insert the message
                        doc.insertString(doc.getLength(), message, style);

                        // Auto scroll
                        // CORRECT
                        logComponent.setCaretPosition(doc.getLength());;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            System.out.println("LOG (NO UI LINKAGE): " + message);
        }
    }
}
