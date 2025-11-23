package UI.Components;

import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;
import Core.LogManager;
import Resource.AssetManager;
import UI.MainInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CharacterStatusPanel extends JPanel {
    private Character character;
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private JProgressBar manaBar;
    private JPanel iconPanel;

    // TODO: FIX CHARACTER STATUS PANEL CALLED BEFORE MAIN INTERFACE FINISHES
    public CharacterStatusPanel(MainInterface parentInterface) {
        nameLabel = new JLabel("N/A - Lvl 0");
        hpBar = new JProgressBar();
        manaBar = new JProgressBar();
        iconPanel = new JPanel();

        MouseAdapter clickAdapter = attachListener(parentInterface);
        this.addMouseListener(clickAdapter);

        nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iconPanel.setLayout(new CardLayout());
        iconPanel.addMouseListener(clickAdapter);

        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        hpBar.setStringPainted(true);
        manaBar.setStringPainted(true);

        add(nameLabel);
        add(hpBar);
        add(manaBar);
        add(iconPanel);
    }

    private MouseAdapter attachListener(MainInterface parentInterface) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (character != null) {
                    LogManager.log("sfhsuihdf");
                    parentInterface.onCharacterPanelClick(character);
                }
            }
        };
    }

    public void setCharacterData(Character character) {
        if (character == null) {
            this.setVisible(false);
            return;
        }

//        LogManager.log("Setting character data: " + character.getName());

        this.setVisible(true);
        nameLabel.setText("Lvl " + character.getLevel() + " - " + character.getName());

        hpBar.setMaximum(character.getInitialHealth());
        hpBar.setValue(character.getHealth());
        hpBar.setString(character.getHealth() + "/" + character.getInitialHealth());
        hpBar.setForeground(barState(character.getHealth(), character.getInitialHealth()));

        manaBar.setMaximum(character.getMaxMana());
        manaBar.setValue(character.getMana());
        manaBar.setString(character.getMana() + "/" + character.getMaxMana());
        manaBar.setForeground(barState(character.getMana(), character.getMaxMana()));

        JLabel iconLabel = new JLabel(AssetManager.getInstance().getImage(character.getImageKey(), 100, 100));
        iconPanel.add(iconLabel);

        // handle dead or alive
        this.setBackground(character.isAlive() ? getBackground() : Color.gray);
    }

    private Color barState(int current, int max) {
        if (max == 0) return Color.GRAY;

        double percentage = (double) current / max;

        if (percentage >= 0.5) return Color.GREEN;
        else if (percentage > 0.2) return Color.YELLOW;
        else return Color.RED;
    }
}

