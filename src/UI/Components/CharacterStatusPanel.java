    package UI.Components;

    import Characters.Character;
    import Core.Visuals.VisualAsset;
    import Core.Visuals.VisualEffectsManager;
    import Resource.AssetManager;
    import UI.Views.BattleInterface;

    import javax.swing.*;
    import javax.swing.border.Border;
    import javax.swing.plaf.basic.BasicProgressBarUI;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;

    public class CharacterStatusPanel extends JPanel {
        private Character character;
        private JLabel nameLabel;
        private JProgressBar hpBar;
        private JProgressBar manaBar;

        private final JLabel overlayDisplayLabel;
        private final JLabel iconDisplayLabel;

        private JPanel iconPanel;

        public CharacterStatusPanel(BattleInterface parentInterface) {
            this.setOpaque(false);
            nameLabel = new JLabel("N/A - Lvl 0");
            hpBar = new JProgressBar();
            manaBar = new JProgressBar();

            hpBar.setUI(new BasicProgressBarUI());
            manaBar.setUI(new BasicProgressBarUI());

            overlayDisplayLabel = new JLabel();
            iconDisplayLabel = new JLabel();

            iconPanel = new JPanel();
            iconPanel.setLayout(null); // makes it stack for some reason
            iconPanel.setOpaque(false);

            final int ICON_SIZE = 100;

            iconDisplayLabel.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
            overlayDisplayLabel.setBounds(0, 0, ICON_SIZE, ICON_SIZE);

            iconPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));

            MouseAdapter clickAdapter = attachListener(parentInterface);
            this.addMouseListener(clickAdapter);

            nameLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

            overlayDisplayLabel.setOpaque(false);
            overlayDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            iconPanel.add(iconDisplayLabel);
            iconPanel.add(overlayDisplayLabel);
            iconPanel.addMouseListener(clickAdapter);

            nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            hpBar.setStringPainted(true);

            add(nameLabel);
            add(hpBar);
            add(manaBar);
            add(iconPanel);
        }

        private MouseAdapter attachListener(BattleInterface parentInterface) {
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
                getIconDisplayLabel().setIcon(null);

                this.setToolTipText(null);

                return;
            }
            if (!character.isAlive()) {
                remove(hpBar);
                if (character.getMaxMana() > 0)
                    remove(manaBar);
                return;
            }

            this.character = character;
            this.setVisible(true);
            nameLabel.setText("Lvl " + character.getLevel() + " - " + character.getName());

            hpBar.setMaximum(character.getInitialHealth());
            hpBar.setValue(character.getHealth());
            hpBar.setString("hp " + character.getHealth() + "/" + character.getInitialHealth());
            hpBar.setForeground(barState(character.getHealth(), character.getInitialHealth()));

            if (character.getMaxMana() <= 0)
                remove(manaBar);
            else {
                manaBar.setStringPainted(true);

                manaBar.setMaximum(character.getMaxMana());
                manaBar.setValue(character.getMana());
                manaBar.setString("mp " + character.getMana() + "/" + character.getMaxMana());
                manaBar.setForeground(barState(character.getMana(), character.getMaxMana()));

                manaBar.setForeground(new Color(0, 150, 255));
            }

            String visualId = character.getImageKey();
            VisualAsset assetData = AssetManager.getInstance().getVisualAssetData(visualId);

            VisualEffectsManager.getInstance().applyVisual(assetData, iconDisplayLabel, false);

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

        public void setSelectionOverlay(boolean isSelected) {
            if (isSelected) {
                Color semiTransparentRed = new Color(255, 0, 0, 100);
                Border lineBorder = BorderFactory.createLineBorder(semiTransparentRed, 1);
                iconDisplayLabel.setBorder(lineBorder);
            } else {
                iconDisplayLabel.setBorder(null);
            }
            iconDisplayLabel.repaint();
        }

        public Character getCharacter() {
            return character;
        }

        public JLabel getIconDisplayLabel() {
            return iconDisplayLabel;
        }

        public JLabel getOverlayDisplayLabel() {
            return overlayDisplayLabel;
        }
    }

