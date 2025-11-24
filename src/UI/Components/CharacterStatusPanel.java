    package UI.Components;

    import Characters.Base.Hero;
    import Characters.Character;
    import Characters.Party;
    import Core.LogManager;
    import Core.VisualAsset;
    import Core.VisualEffectsManager;
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
        private final JLabel iconDisplayLabel;
        private JPanel iconPanel;


        public CharacterStatusPanel(MainInterface parentInterface) {
            nameLabel = new JLabel("N/A - Lvl 0");
            hpBar = new JProgressBar();
            manaBar = new JProgressBar();
            iconDisplayLabel = new JLabel();
            iconPanel = new JPanel();


            MouseAdapter clickAdapter = attachListener(parentInterface);
            this.addMouseListener(clickAdapter);

            nameLabel.setOpaque(false);
            iconPanel.setOpaque(false);

            hpBar.setOpaque(false);
            manaBar.setOpaque(false);

            nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

            iconDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            iconPanel.setLayout(new CardLayout());
            iconPanel.add(iconDisplayLabel, "Icon");
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
                        parentInterface.onCharacterPanelClick(character);
                    }
                }
            };
        }

        public void setCharacterData(Character character) {
            if (character == null) {
                this.setVisible(false);
                VisualEffectsManager.getInstance().stopAllTimers();
                getIconDisplayLabel().setIcon(null);
                return;
            }

            this.character = character;
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

            String visualId = character.getImageKey();
            VisualAsset assetData = AssetManager.getInstance().getVisualAssetData(visualId);

            VisualEffectsManager.getInstance().applyVisual(assetData, iconDisplayLabel);

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

        public Character getCharacter() {
            return character;
        }

        public JLabel getIconDisplayLabel() {
            return iconDisplayLabel;
        }
    }

