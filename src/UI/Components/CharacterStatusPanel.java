    package UI.Components;

    import Characters.Character;
    import Core.Visuals.VisualAsset;
    import Core.Visuals.VisualEffectsManager;
    import Resource.Animation.AssetManager;
    import UI.Views.BattleView;

    import javax.swing.*;
    import javax.swing.plaf.basic.BasicProgressBarUI;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import Characters.Enemies.Varoth;

    public class CharacterStatusPanel extends JPanel {
        private Character character;
        private OutlinedLabel nameLabel;
        private JProgressBar hpBar;
        private JProgressBar manaBar;

        private final JLabel overlayDisplayLabel;
        private final JLabel iconDisplayLabel;

        private static final int ICON_SIZE = 100; //standard
        private static final int BOSS_ICON_SIZE = 250;

        private JPanel iconPanel;

        public CharacterStatusPanel(BattleView parentInterface) {
            this.setOpaque(false);
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            nameLabel = new OutlinedLabel("N/A - Lvl 0", JLabel.LEFT);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

            nameLabel.setOutlineColor(Color.BLACK);
            nameLabel.setStrokeWidth(2.5f);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));

            hpBar = createStyledBar();
            manaBar = createStyledBar();

            overlayDisplayLabel = createIconLabel();
            iconDisplayLabel = createIconLabel();

            iconPanel = new JPanel();
            iconPanel.setLayout(null);
            iconPanel.setOpaque(false);
            iconPanel.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));

            iconPanel.add(overlayDisplayLabel);
            iconPanel.add(iconDisplayLabel);

            iconPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (character != null) {
                        parentInterface.onCharacterPanelClick(character);
                    }
                }
            });

            add(nameLabel);
            add(hpBar);
            add(manaBar);
            add(iconPanel);
        }

        private JProgressBar createStyledBar() {
            JProgressBar bar = new JProgressBar();
            bar.setUI(new BasicProgressBarUI());
            bar.setStringPainted(true);
            return bar;
        }

        private JLabel createIconLabel() {
            JLabel label = new JLabel();
            label.setBounds(0, 0, ICON_SIZE, ICON_SIZE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }

        public void setCharacterData(Character character) {
            if (character == null) {
                this.setVisible(false);
                // stop animations and clear icon
                getIconDisplayLabel().setIcon(null);
                this.setToolTipText(null);
                return;
            }

            int currentSize = ICON_SIZE;

            if (character instanceof Varoth) {
                currentSize = BOSS_ICON_SIZE;
            }
            iconPanel.setPreferredSize(new Dimension(currentSize, currentSize));
            iconDisplayLabel.setBounds(0, 0, currentSize, currentSize);
            overlayDisplayLabel.setBounds(0, 0, currentSize, currentSize);
            this.revalidate();
            this.repaint();

            if (!character.isAlive()) {
                hpBar.setVisible(false);
                manaBar.setVisible(false);
                manaBar.setString("");
            } else {
                hpBar.setVisible(true);
                manaBar.setVisible(true);
            }
            if (character.getMaxMana() <= 0) {
                manaBar.setVisible(false);
                manaBar.setString("");
            }
            else {
                manaBar.setVisible(true);

                manaBar.setStringPainted(true);
                manaBar.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
                manaBar.setMaximum(character.getMaxMana());
                manaBar.setValue(character.getMana());
                manaBar.setString("MP " + character.getMana() + "/" + character.getMaxMana());
                manaBar.setForeground(barState(character.getMana(), character.getMaxMana()));

                manaBar.setForeground(new Color(0, 150, 255));
            }

            this.character = character;
            this.setVisible(true);

            nameLabel.setText("Lv. " + character.getLevel() + " - " + character.getName());
            nameLabel.setForeground(Color.WHITE);

            hpBar.setMaximum(character.getMaxHealth());
            hpBar.setValue(character.getHealth());
            hpBar.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
            hpBar.setString("HP " + character.getHealth() + "/" + character.getMaxHealth());
            hpBar.setForeground(barState(character.getHealth(), character.getMaxHealth()));

            String visualId = character.getIdleImageKey();
            VisualAsset assetData = AssetManager.getInstance().getVisualAssetData(visualId);

            VisualEffectsManager.getInstance().applyVisual(assetData, iconDisplayLabel, false);

            // handle dead or alive
            this.setBackground(character.isAlive() ? getBackground() : Color.gray);
        }

        private Color barState(int current, int max) {
            double percentage = (double) current / max;

            if (percentage >= 0.5) return Color.GREEN;
            else if (percentage > 0.2) return Color.YELLOW;
            else return Color.RED;
        }

        public void setTargetAvailability(boolean isTargetingMode, boolean isValidTarget) {
            if (!isTargetingMode) {
                this.setCursor(Cursor.getDefaultCursor());
                overlayDisplayLabel.setBackground(null);
                overlayDisplayLabel.setOpaque(false);
                return;
            }

            if (isValidTarget) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // TODO: dd a faint green glow
                overlayDisplayLabel.setBackground(new Color(0, 100, 0, 100));
                overlayDisplayLabel.setOpaque(true);
            } else {
                this.setCursor(Cursor.getDefaultCursor());

                // Dim effect using a semi-transparent black overlay
                overlayDisplayLabel.setBackground(new Color(0, 0, 0, 150));
                overlayDisplayLabel.setOpaque(true);
            }

            this.repaint();
        }

        public void setSelectionOverlay(boolean isSelected) {
            if (isSelected) {
                Color semiTransparentRed = new Color(255, 0, 0, 100);
                overlayDisplayLabel.setBackground(semiTransparentRed);
                overlayDisplayLabel.setOpaque(true);
            } else {
                overlayDisplayLabel.setBackground(null);
                overlayDisplayLabel.setOpaque(false);
            }
            overlayDisplayLabel.repaint();
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

