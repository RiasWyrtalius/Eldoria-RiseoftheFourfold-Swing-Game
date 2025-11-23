package UI.Components;

import Characters.Character;
import Core.LogManager;
import Resource.AssetManager;

import javax.swing.*;
import java.awt.*;

public class CharacterStatusPanel extends JPanel {
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private JProgressBar manaBar;
    private JPanel iconPanel;

    // calls internal init components
    public CharacterStatusPanel() {
        nameLabel = new JLabel("N/A - Lvl 0");
        hpBar = new JProgressBar();
        manaBar = new JProgressBar();
        iconPanel = new JPanel();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        iconPanel.setLayout(new CardLayout());

//        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        hpBar.setBackground(Color.RED);
        manaBar.setBackground(Color.BLUE);

        add(nameLabel);
        add(hpBar);
        add(manaBar);
        add(iconPanel);
    }


    public void setCharacterData(Character character) {
        if (character == null) {
            this.setVisible(false);
            return;
        }

        LogManager.log("Setting character data: " + character.getName());

        this.setVisible(true);
        nameLabel.setText("Lvl " + character.getLevel() + " - " + character.getName());

        hpBar.setMaximum(character.getInitialHealth());
        hpBar.setValue(character.getHealth());

        manaBar.setMaximum(character.getMaxMana());
        manaBar.setValue(character.getMana());

        JLabel iconLabel = new JLabel(AssetManager.getInstance().getImage(character.getImageKey(), 100, 100));
        iconPanel.add(iconLabel);

        // handle dead or alive
        this.setBackground(character.isAlive() ? getBackground() : Color.gray);
    }
}

