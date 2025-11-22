package UI.Components;

import Characters.Character;
import javax.swing.*;
import java.awt.*;

public class CharacterStatusPanel extends JPanel {
    private JLabel nameLabel;
    private JProgressBar hpBar;
    private JProgressBar manaBar;

    public void setCharacterData(Character character) {
        if (character == null) {
            this.setVisible(false);
            return;
        }

        this.setVisible(true);
        nameLabel.setText(character.getName() + "Lvl " + character.getLevel());

        hpBar.setMaximum(character.getInitialHealth());
        hpBar.setValue(character.getHealth());

        manaBar.setMaximum(character.getMaxMana());
        manaBar.setValue(character.getHealth());

        // handle dead or alive
        this.setBackground(character.isAlive() ? getBackground() : Color.gray);
    }
}

