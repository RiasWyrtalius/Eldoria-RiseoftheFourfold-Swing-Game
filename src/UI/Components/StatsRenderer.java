package UI.Components;

import Abilities.Skill;
import Characters.Base.Hero;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class StatsRenderer {
    private JTextPane textPane;
    private StyledDocument doc;
    private Style classStyle, hpStyle, mpStyle, descStyle, labelStyle;

    public StatsRenderer(JTextPane textPane) {
        this.textPane = textPane;
        this.doc = textPane.getStyledDocument();
        initStyles();

        // Basic settings
        textPane.setOpaque(false);
        textPane.setEditable(false);
        textPane.setBorder(null);
    }

    private void initStyles() {
        labelStyle = textPane.addStyle("LabelStyle", null);
        StyleConstants.setForeground(labelStyle, Color.BLACK);
        StyleConstants.setBold(labelStyle, true);

        classStyle = textPane.addStyle("ClassStyle", null);
        StyleConstants.setForeground(classStyle, new Color(218, 165, 32)); // Gold

        hpStyle = textPane.addStyle("HPStyle", null);
        StyleConstants.setForeground(hpStyle, new Color(255, 76, 76)); // Crimson

        mpStyle = textPane.addStyle("MPStyle", null);
        StyleConstants.setForeground(mpStyle, new Color(76, 110, 255)); // Arcane Blue

        descStyle = textPane.addStyle("DescStyle", null);
        StyleConstants.setForeground(descStyle, new Color(0, 0, 0));
    }

    public void updateDisplay(Hero hero) {
        try {
            doc.remove(0, doc.getLength());
            insertPair("Class: ", hero.getJob().getName() + "\n", classStyle);
            insertPair("HP: ", hero.getInitialHealth() + "\n", hpStyle);
            insertPair("MP: ", hero.getMaxMana() + "\n\n", mpStyle);

            doc.insertString(doc.getLength(), "Description:\n", labelStyle);
            doc.insertString(doc.getLength(), hero.getJob().getDescription(), descStyle);

            doc.insertString(doc.getLength(), "\n\nSkills:\n", labelStyle);
            if (hero.getSkills() != null) {
                for (Skill s : hero.getSkills()) {
                    doc.insertString(doc.getLength(), "• " + s.getName() + " ", descStyle);
                    doc.insertString(doc.getLength(), "[" + s.getManaCost() + " MP]\n", mpStyle);
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertPair(String label, String value, Style valueStyle) throws BadLocationException {
        doc.insertString(doc.getLength(), label, labelStyle);
        doc.insertString(doc.getLength(), value, valueStyle);
    }
}
