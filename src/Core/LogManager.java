package Core;

import javax.swing.*;

public class LogManager {
    private static JTextArea logComponent;

    public static void initialize(JTextArea component) {
        logComponent = component;
    }

    public static void log(String message) {
        if (logComponent != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    logComponent.append(message + "\n");
                    // auto scroll to the bottom
                    logComponent.setCaretPosition(logComponent.getDocument().getLength());
                }
            });
        } else {
            System.out.println("LOG (NO UI LINKAGE): " + message);
        }
    }
}
