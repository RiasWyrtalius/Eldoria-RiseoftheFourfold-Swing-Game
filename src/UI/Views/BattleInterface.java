package UI.Views;
import Core.Utils.LogFormat;
import Core.Visuals.VisualEffectsManager;
import UI.Components.*;

import Abilities.Skill;
import Core.Battle.*;
import Characters.Base.Hero;
import Characters.Character;
import Core.GameManager;
import Core.Utils.LogManager;
import Items.Inventory;
import Items.Item;
import Resource.Audio.AudioManager;
import UI.Components.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.JTextPane;
import javax.swing.text.*;
import java.awt.Color;
import Core.GameFlow.VictoryPanel;

// TODO: DESCEND calls battle controller->game manager for next battle
public class BattleInterface extends JPanel {
    private BattleController battleController;

    private JPanel contentPanel;
    private JPanel battlePanel;
    private JTextPane GameLogPanelTextPane;
    private JTextPane GameLogHighlightPanelTextPane;
    private JPanel inventoryPanel;

    private JLabel heroPartyLabel;
        private JPanel heroPartyPanel1; private JPanel heroPartyPanel2; private JPanel heroPartyPanel3; private JPanel heroPartyPanel4;
    private JLabel enemyPartyLabel;
        private JPanel enemyPartyPanel1; private JPanel enemyPartyPanel2; private JPanel enemyPartyPanel3; private JPanel enemyPartyPanel4;

    private JPanel descendPanel;

    private final List<JPanel> heroPartyPanels;
    private final List<JPanel> enemyPartyPanels;

    // Character Inspector Components
    private JScrollPane CharacterInspector_JSP;
    private JTextPane inspectorText;

        private AnimatedStatBar inspector_HpBar;
        private AnimatedStatBar inspector_MpBar;
        private AnimatedStatBar inspector_XPBar;

    private final Map<Character, CharacterStatusPanel> characterToPanelMap = new HashMap<>();

    // STATE MACHINE FIELDS
    private BattleUIMode currentMode = BattleUIMode.HERO_SELECT;
    private Hero activeHero = null;
    private Skill selectedSkill = null;
    private List<Character> selectedTargets = new ArrayList<>();
    private Item selectedItem = null;

    private JPopupMenu targetConfirmMenu = new JPopupMenu();

    private JButton endTurnButton;
//    private JButton descendButton;
    private JPanel heroPanel;
    private JPanel infoPanel;
    private JPanel enemyPanel;
    private JLabel floorLabel;
    private JSplitPane SplitPane_1;
    private JSplitPane SplitPane_2;

    public BattleInterface() {

        if (battlePanel != null) {
            battlePanel.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int w = battlePanel.getWidth();
                    int h = battlePanel.getHeight();
                    //System.out.println("BATTLE BACKGROUND SIZE needed: " + w + " x " + h);
                }
            });
        }

        this.setLayout(new BorderLayout());
        if (contentPanel != null) {
            this.add(contentPanel, BorderLayout.CENTER);
        }

        heroPartyLabel.setForeground(Color.WHITE);
        if (SplitPane_1 != null) {
            SplitPane_1.setUI(new BasicSplitPaneUI() {
                @Override
                public BasicSplitPaneDivider createDefaultDivider() {
                    return new BasicSplitPaneDivider(this) { @Override public void setBorder(Border b) {} };
                }
            });
            SplitPane_1.setBackground(new Color(44, 44, 44));
            SplitPane_1.setDividerSize(10);
        }
        if (SplitPane_2 != null) {
            SplitPane_2.setUI(new BasicSplitPaneUI() {
                @Override
                public BasicSplitPaneDivider createDefaultDivider() {
                    return new BasicSplitPaneDivider(this) { @Override public void setBorder(Border b) {} };
                }
            });
            SplitPane_2.setBackground(new Color(44, 44, 44));
            SplitPane_2.setDividerSize(10);
        }

        setupInspector();

        descendPanel.setVisible(false);

        GameLogPanelTextPane.setEditable(false);
        GameLogHighlightPanelTextPane.setEditable(false);
        heroPartyPanels = Arrays.asList(heroPartyPanel1, heroPartyPanel2, heroPartyPanel3, heroPartyPanel4);
        enemyPartyPanels = Arrays.asList(enemyPartyPanel1, enemyPartyPanel2, enemyPartyPanel3, enemyPartyPanel4);

        listenerInit();
    }

    public void linkControllerAndData(BattleController controller) {
        this.battleController = controller;
        resetSelectionState();
        refreshUI();
    }

    public void listenerInit() {
        if (endTurnButton != null) {
            endTurnButton.addActionListener(e -> {
                resetSelectionState();
                battleController.endHeroPhaseManually();
                refreshUI();
            });
        }
//        if (descendButton != null) {
//            descendButton.addActionListener(e -> {
//                resetSelectionState();
//                descendPanel.setVisible(false);
//                GameManager.getInstance().loadNextLevel();
//            });
//        }
        battlePanel.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) { resetSelectionState();}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
        });
    }

    public void refreshUI() {
        // TODO: floor label text should change color when going deeper
        floorLabel.setText("Floor " + battleController.getLevelNumber());
        floorLabel.setForeground(Color.red);

        heroPartyLabel.setText(battleController.getHeroParty().getPartyName());
        enemyPartyLabel.setText(battleController.getEnemyParty().getPartyName());

        if (activeHero != null && selectedSkill != null) {
            LogManager.log("(HERO) Active Hero: " + activeHero.getName(), LogFormat.UI_ACTIVE_HERO);
            LogManager.log("(SKILL) Selected Skill: " + selectedSkill.getName(), LogFormat.UI_SKILL_SELECT);
        }

        setPartyUI(battleController.getHeroParty().getPartyMembers(), heroPartyPanels);
        setPartyUI(battleController.getEnemyParty().getPartyMembers(), enemyPartyPanels);

        if (inventoryPanel != null && battleController != null) {
            Inventory inv = battleController.getHeroParty().getInventory();

//            LogManager.log("DEBUG: Loading Inventory. Item Count: " + inv.getAllItems().size());

            ((InventoryPanel) inventoryPanel).loadInventory(inv);
        }

        updateControls();
    }

    private void updateControls() {
//        LogManager.log("current mode: " + currentMode.toString());
        BattlePhase phase = battleController.getCurrentPhase();

        boolean isPlayerTurn = (phase == BattlePhase.HERO_ACTION_WAIT);
        endTurnButton.setEnabled(isPlayerTurn);
        boolean canUseItems = isPlayerTurn && (currentMode == BattleUIMode.HERO_SELECT);

        if (inventoryPanel != null) {
            inventoryPanel.setEnabled(canUseItems);
        }

        if (phase == BattlePhase.BATTLE_ENDED) {
            // TODO: set battleOutcome JLabel to victory, or tie
            BattleResult result = battleController.getFinalResult();
            endTurnButton.setVisible(false);

            if (result == BattleResult.VICTORY || result == BattleResult.DEFEAT) {
                showSummaryScreen(result);
            }

            if (descendPanel != null) descendPanel.setVisible(false);

            if (result == BattleResult.VICTORY){
                showSummaryScreen(result);
            }
        } else {
            endTurnButton.setVisible(true);
            descendPanel.setVisible(false);
        }
    }

    private void showSummaryScreen(BattleResult result) {
        if (currentMode == BattleUIMode.IDLE) return;
        currentMode = BattleUIMode.IDLE; // Lock logic

        BattleSummary summary = new BattleSummary();
        StringBuilder sb = new StringBuilder();

        if (result == BattleResult.VICTORY) {

            int xp = battleController.getEarnedXP();
            sb.append("XP gained: ").append(xp).append("\n\n");

            java.util.List<Items.Item> items = battleController.getEarnedItems();

            if (!items.isEmpty()) {
                sb.append("LOOT ACQUIRED\n");

                Map<String, Integer> itemCounts = new LinkedHashMap<>();

                for (Items.Item item : items) {
                    String name = item.getName();
                    itemCounts.put(name, itemCounts.getOrDefault(name, 0) + 1);
                }

                for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                    sb.append(" • ").append(entry.getKey());

                    if (entry.getValue() > 1) {
                        sb.append(" (x").append(entry.getValue()).append(")");
                    }
                    sb.append("\n");
                }
            } else {
                sb.append("No items found.\n");
            }

            summary.setSummaryData("VICTORY!", sb.toString());
            summary.configureButton("Descend", () -> {
                GameManager.getInstance().loadNextLevel();
            });

        } else if (result == BattleResult.DEFEAT) {
            sb.append("The party has fallen.\n\n");
            sb.append("Your journey ends on Floor ").append(battleController.getLevelNumber()).append(".");
            summary.setSummaryData("DEFEAT", sb.toString());
            summary.configureButton("Return to Title", () -> {
                GameManager.getInstance().transitionToMainMenu();
            });
        }

        JRootPane root = SwingUtilities.getRootPane(this);

        JPanel glassOverlay = new JPanel(new GridBagLayout());
        glassOverlay.setOpaque(false);
        glassOverlay.addMouseListener(new java.awt.event.MouseAdapter() {});
        JPanel summaryBox = summary.getPanel();
        summaryBox.setPreferredSize(new Dimension(400, 300));
        summaryBox.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1));
        glassOverlay.add(summaryBox);
        root.setGlassPane(glassOverlay);
        glassOverlay.setVisible(true);
    }

    private void setPartyUI(List<Character> party, List<JPanel> setupPanel) {
        // call set character data for each party
        for (int i = 0; i < setupPanel.size(); i++) {
            CharacterStatusPanel panel = (CharacterStatusPanel)setupPanel.get(i);
            if (i < party.size()) {
                Character character = party.get(i);
                characterToPanelMap.put(character, panel);
                panel.setCharacterData(character);
            } else {
                panel.setCharacterData(null);
            }
        }
    }

    private void createUIComponents() {
        battlePanel = new BackgroundPanel();
        inventoryPanel = new InventoryPanel();

        ((InventoryPanel)inventoryPanel).setOnItemSelected(this::onItemSelect);

        heroPartyPanel1 = new CharacterStatusPanel(this);
        heroPartyPanel2 = new CharacterStatusPanel(this);
        heroPartyPanel3 = new CharacterStatusPanel(this);
        heroPartyPanel4 = new CharacterStatusPanel(this);

        enemyPartyPanel1 = new CharacterStatusPanel(this);
        enemyPartyPanel2 = new CharacterStatusPanel(this);
        enemyPartyPanel3 = new CharacterStatusPanel(this);
        enemyPartyPanel4 = new CharacterStatusPanel(this);
    }

    private void updateTargetHighlights(TargetCondition condition) {
        processHighlightLoop(heroPartyPanels, condition);
        processHighlightLoop(enemyPartyPanels, condition);
    }

    private void processHighlightLoop(List<JPanel> panels, TargetCondition condition) {
        for (JPanel p : panels) {
            CharacterStatusPanel panel = (CharacterStatusPanel) p;
            Character c = panel.getCharacter();

            if (c != null) {
                boolean isValid = condition.isValid(c);
                panel.setTargetAvailability(true, isValid);
            } else {
                // empty slot :))
                panel.setTargetAvailability(true, false);
            }
        }
    }

    private void clearTargetHighlights() {
        for (JPanel p : heroPartyPanels) {
            ((CharacterStatusPanel) p).setTargetAvailability(false, false);
        }
        for (JPanel p : enemyPartyPanels) {
            ((CharacterStatusPanel) p).setTargetAvailability(false, false);
        }
    }

    public void onCharacterPanelClick(Character clickedCharacter) {

        updateInspectorPanel(clickedCharacter);

        switch (currentMode) {
            case HERO_SELECT:
//                LogManager.log("SELECT HERO");
                if (clickedCharacter instanceof Hero) {
                    Hero hero = (Hero)clickedCharacter;
                    if (hero.isAlive() && !hero.isExhausted()) {
                        onHeroSelect(hero);
                    } else {
                        VisualEffectsManager.getInstance().showFloatingText(hero, "Exhausted!", Color.RED);
                    }
                }
//                else LogManager.log("Enemy is Selected!");
                break;

            case TARGET_SELECT:
//                LogManager.log("SELECT TARGET");

                TargetType typeRule = null;
                TargetCondition conditionRule = null;
                String actionName = "";

                CharacterStatusPanel panel = getCharacterPanel(clickedCharacter);
                if (panel == null) return;

                if (selectedSkill != null) {
                    typeRule = selectedSkill.getTargetType();
                    conditionRule = selectedSkill.getTargetCondition();
                    actionName = selectedSkill.getName();
                } else if (selectedItem != null) {
                    typeRule = selectedItem.getTargetType();
                    conditionRule = selectedItem.getTargetCondition();
                    actionName = selectedItem.getName();
                } else {
                    return;
                }

                if (!conditionRule.isValid(clickedCharacter)) {
                    LogManager.log("Invalid Target: " + actionName + " requires a " + conditionRule + " target.");
                    return;
                }

                if (selectedTargets.contains(clickedCharacter)) {
                    selectedTargets.remove(clickedCharacter);
                    panel.setSelectionOverlay(false);
//                    LogManager.log("Deselected " + clickedCharacter.getName());
                }
                else {
                    // check selection limit
                    if (selectedTargets.size() < typeRule.getMaxTargets()) {
                        selectedTargets.add(clickedCharacter);
                        LogManager.log("Selected " + clickedCharacter.getName() +
                                " (" + selectedTargets.size() + "/" + typeRule.getMaxTargets() + ")");
                        panel.setSelectionOverlay(true);
                    } else {
                        LogManager.logHighlight("Maximum targets reached (" + typeRule.getMaxTargets() + ").", false);
                        // TODO: REPLACE FUNCTION?!!!
                    }
                }

                if (!selectedTargets.isEmpty()) {
                    showTargetConfirmMenu(clickedCharacter); // Shows menu anchored to contentPanel
                } else {
                    hideTargetConfirmMenu();
                }

                refreshUI();
                break;
            case SKILL_SELECT:
//                LogManager.log("SELECT SKILL");
            case IDLE:
//                LogManager.log("IDLE");
                break;
        }
    }

    private class VictoryPanel extends JPanel {
        public VictoryPanel(ActionListener onExit) {
            setLayout(new GridBagLayout()); // Centers everything in the middle of the screen
            setOpaque(false); // Allows us to paint the transparent background manually

            // Create a vertical box to hold Text -> Space -> Button
            JPanel contentBox = new JPanel();
            contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
            contentBox.setOpaque(false);

            //Title
            OutlinedLabel title = new OutlinedLabel("CAMPAIGN COMPLETE", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 48));
            title.setForeground(new Color(255, 215, 0)); // Gold text
            title.setOutlineColor(Color.BLACK);
            title.setStrokeWidth(4f);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            //ubtitle
            JLabel subTitle = new JLabel("You have cleared all stages!");
            subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            subTitle.setForeground(Color.WHITE);
            subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            //Exit
            JButton exitBtn = new JButton("Return to Title");
            exitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            exitBtn.setFocusPainted(false);
            exitBtn.setBackground(Color.WHITE);
            exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            exitBtn.addActionListener(onExit);

            //title, sub, exit
            contentBox.add(title);
            contentBox.add(Box.createVerticalStrut(15));
            contentBox.add(subTitle);
            contentBox.add(Box.createVerticalStrut(40));
            contentBox.add(exitBtn);

            add(contentBox);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            super.paintComponent(g);
        }
    }

    public void showCampaignVictoryScreen() {
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root == null) return;
        JLayeredPane layeredPane = root.getLayeredPane();
        VictoryPanel victoryOverlay = new VictoryPanel(e -> {
            Resource.Audio.AudioManager.getInstance().stopMusic();
            VisualEffectsManager.getInstance().stopAllTimers();
            layeredPane.remove((Component) ((JButton)e.getSource()).getParent().getParent());
            layeredPane.repaint();
            GameManager.getInstance().transitionToMainMenu();
        });
        victoryOverlay.setBounds(0, 0, root.getWidth(), root.getHeight());
        layeredPane.add(victoryOverlay, JLayeredPane.MODAL_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private void clearAllSelectionOverlays() {
        for (JPanel panel : heroPartyPanels) {
            ((CharacterStatusPanel) panel).setSelectionOverlay(false);
        }
        for (JPanel panel : enemyPartyPanels) {
            ((CharacterStatusPanel) panel).setSelectionOverlay(false);
        }
    }

    public void onConfirmAction() {
        boolean requiresTargets = (selectedSkill != null && selectedSkill.getTargetType().getMaxTargets() > 0) ||
                (selectedItem != null && selectedItem.getTargetType().getMaxTargets() > 0);

        boolean isAutoTarget = (selectedSkill != null && selectedSkill.getTargetType() == TargetType.AOE_ALL_TARGETS) ||
                (selectedItem != null && selectedItem.getTargetType() == TargetType.AOE_ALL_TARGETS);

        if (requiresTargets && !isAutoTarget && selectedTargets.isEmpty()) {
            LogManager.log("Cannot confirm: No targets selected.", LogFormat.UI_TARGET_SELECT);
            return;
        }

        boolean actionSuccess = false;

        if (selectedSkill != null) {
            actionSuccess = battleController.executeActionFromUI(activeHero, selectedSkill, selectedTargets);
        }
        else if (selectedItem != null) {
            actionSuccess = battleController.executeItemActionFromUI(selectedItem, selectedTargets);
        }

        if (actionSuccess) {
            hideTargetConfirmMenu();
            resetSelectionState();
            refreshUI();
        } else {
            hideTargetConfirmMenu();
        }
    }

    public void onHeroSelect(Hero clickedHero) {
        if (currentMode != BattleUIMode.HERO_SELECT) return;

        if (clickedHero.isAlive() && !clickedHero.isExhausted()) {
            this.activeHero = clickedHero;
            this.currentMode = BattleUIMode.SKILL_SELECT;

            showSkillSelectionMenu(clickedHero);

            refreshUI();
        } else {
            LogManager.log(clickedHero.getName() + " is unable to act.", LogFormat.SYSTEM_ERROR);
        }
    }

    public void onSkillSelect(Skill skill) {
        if (currentMode != BattleUIMode.SKILL_SELECT) return;

        updateTargetHighlights(skill.getTargetCondition());

        this.selectedSkill = skill;
        this.currentMode = BattleUIMode.TARGET_SELECT;
        TargetType type = skill.getTargetType();

        if (type == TargetType.NO_TARGETS) {
            onConfirmAction();
            return;
        }

        LogManager.log("(SKILL) : Select a target for " + skill.getName(), LogFormat.UI_SKILL_SELECT);
        refreshUI();
    }

    public void onItemSelect(Item item) {
        LogManager.log("(ITEM) : ITEM SELECTED: " + item.getName(), LogFormat.UI_ITEM_SELECT);
        this.currentMode = BattleUIMode.ITEM_SELECT;

        this.selectedSkill = null;
        this.selectedItem = item;
        this.selectedTargets.clear();

        TargetType type = item.getTargetType();

        updateTargetHighlights(item.getTargetCondition());

        if (type == TargetType.NO_TARGETS) {
//            if (activeHero != null) {
//                selectedTargets.add(activeHero);
//            } else {
//                selectedTargets.add(battleController.getHeroParty().getPartyMembers().get(0));
//            }
            onConfirmAction();
            return;
        }

//        else if (type == TargetType.AOE_ALL_TARGETS) {
////            executeItemActionFromUI(activeHero, item, null);
//
//            // manual clean up since we skipped onconfirmed action and stuff
//            hideTargetConfirmMenu();
//            resetSelectionState();
//            return;
//        }

        // manual targetting
        this.currentMode = BattleUIMode.TARGET_SELECT;

        LogManager.log("Select up to " + type.getMaxTargets() + " target(s).", LogFormat.UI_TARGET_SELECT);
        refreshUI();
    }

    private void resetSelectionState() {
        this.selectedItem = null;
        this.activeHero = null;
        this.selectedSkill = null;
        this.selectedTargets.clear();
        this.currentMode = BattleUIMode.HERO_SELECT;

        hideTargetConfirmMenu();
        refreshUI();
        clearTargetHighlights();
        clearAllSelectionOverlays();
    }

    private void hideTargetConfirmMenu() {
        targetConfirmMenu.setVisible(false);
    }

    private void showTargetConfirmMenu(Character character) {
        JPopupMenu menu = getJPopUpConfirm();

        CharacterStatusPanel activePanel = null;
        for (JPanel panel : heroPartyPanels) {
            CharacterStatusPanel statusPanel = (CharacterStatusPanel)panel;
            if (statusPanel.getCharacter() == character) { activePanel = statusPanel; break; }
        }
        for (JPanel panel : enemyPartyPanels) {
            CharacterStatusPanel statusPanel = (CharacterStatusPanel)panel;
            if (statusPanel.getCharacter() == character) { activePanel = statusPanel; break; }
        }

        if (activePanel != null) {
            menu.show(activePanel, 0, 0);
        } else {
            menu.show(this, 100, 100);
        }
    }

    private JPopupMenu getJPopUpConfirm() {
        JPopupMenu menu = new JPopupMenu();

        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        int maxTargets = 0;
        String actionType = "Action";

        if (selectedSkill != null) {
            maxTargets = selectedSkill.getTargetType().getMaxTargets();
            actionType = "Skill";
        } else if (selectedItem != null) {
            maxTargets = selectedItem.getTargetType().getMaxTargets();
            actionType = "Item";
        }

        JMenuItem confirm = new JMenuItem("Confirm (" +
                selectedTargets.size() + "/" + maxTargets + ")");
        confirm.addActionListener(e -> onConfirmAction());

        JMenuItem cancel = new JMenuItem("Cancel " + actionType);

        String finalActionType = actionType;
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogManager.log(finalActionType + " selection cancelled", LogFormat.UI_TARGET_SELECT);
                resetSelectionState();
            }
        });

        menu.add(confirm);
        menu.add(cancel);
        return menu;
    }

    private void showSkillSelectionMenu(Hero hero) {
        JPopupMenu menu = getJPopupMenu(hero);

        CharacterStatusPanel activePanel = null;
        for (JPanel panel : heroPartyPanels) {
            CharacterStatusPanel statusPanel = (CharacterStatusPanel)panel;
             if (statusPanel.getCharacter() == hero) { activePanel = statusPanel; break; }
        }

        if (activePanel != null) {
            menu.show(activePanel, 0, 0);
        } else {
            menu.show(this, 100, 100);
        }

//        LogManager.log("Skill menu shown for " + hero.getName() + ".");
    }

    private JPopupMenu getJPopupMenu(Hero hero) {
        JPopupMenu menu = new JPopupMenu();

        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                if (selectedTargets.isEmpty())
                    resetSelectionState();
            }
        });

        for (Skill skill : hero.getJob().getSkills()) {
            JMenuItem item = new JMenuItem(skill.getName() + "\t(" + skill.getManaCost() + ") MP");
            item.addActionListener(e -> onSkillSelect(skill));
            menu.add(item);
        }
        return menu;
    }

    private void setupInspector() {
        inspectorText = new JTextPane();
        inspectorText.setEditable(false);
        inspectorText.setMargin(new Insets(10, 10, 10, 10)); // padding
        inspectorText.setCaretColor(Color.WHITE);

        inspector_XPBar = new AnimatedStatBar(100, Color.YELLOW, "XP: ");
        inspector_HpBar = new AnimatedStatBar(100, Color.GREEN, "HP:");
        inspector_MpBar = new AnimatedStatBar(100, new Color(100, 149, 237), "MP:");

        inspector_XPBar.setPreferredSize(new Dimension(180, 15));
        inspector_HpBar.setPreferredSize(new Dimension(180, 25));
        inspector_MpBar.setPreferredSize(new Dimension(180, 25));

        if (CharacterInspector_JSP != null) {
            CharacterInspector_JSP.setViewportView(inspectorText);
            CharacterInspector_JSP.getViewport().setBackground(new Color(44, 44, 44));
        }
    }

    private void updateInspectorPanel(Character c) {
        if (inspectorText == null) return;

        inspectorText.setText("");

        //no character selected
        if (c == null) { return; }

        inspector_HpBar.setVisible(true);
        inspector_MpBar.setVisible(true);
        inspector_XPBar.setVisible(true);

        if (c instanceof Hero) {
            Hero h = (Hero)c;
            inspector_XPBar.setMaxValue(h.getRequiredXP());
            inspector_XPBar.setValue(h.getXP());
        }

        inspector_HpBar.setMaxValue(c.getMaxHealth());
        inspector_HpBar.setValue(c.getHealth());

        inspector_MpBar.setMaxValue(c.getMaxMana());
        inspector_MpBar.setValue(c.getMana());

        StyledDocument doc = inspectorText.getStyledDocument();

        //Helper Styles
        SimpleAttributeSet titleStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(titleStyle, new Color(65, 105, 225));
        StyleConstants.setBold(titleStyle, true);
        StyleConstants.setFontSize(titleStyle, 20);

        SimpleAttributeSet levelStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(levelStyle, "Segoe UI Light");
        StyleConstants.setForeground(levelStyle, new Color(41, 65, 121));
        StyleConstants.setFontSize(levelStyle, 18);

        SimpleAttributeSet skillStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(skillStyle, "Segoe UI Light");
        StyleConstants.setForeground(skillStyle, new Color(41, 65, 121));
        StyleConstants.setFontSize(skillStyle, 18);

        SimpleAttributeSet hpStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(hpStyle, Color.GREEN);
        StyleConstants.setBold(hpStyle, true);

        SimpleAttributeSet mpStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(mpStyle, new Color(100, 149, 237));
        StyleConstants.setBold(mpStyle, true);

        SimpleAttributeSet grayStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(grayStyle, Color.GRAY);

        SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultStyle, Color.BLACK);

        SimpleAttributeSet spacerStyle = new SimpleAttributeSet();
        StyleConstants.setFontSize(spacerStyle, 10);

        try {
            //NAME & CLASS
            doc.insertString(doc.getLength(), c.getName().toUpperCase() + "\n", titleStyle);

            if (c instanceof Hero) {
                doc.insertString(doc.getLength(), ((Hero) c).getJob().getName() + "\n", defaultStyle);
            }

            doc.insertString(doc.getLength(), "LVL: " + c.getLevel() + "\n", levelStyle);

            //XP, HP, & MANA
            if (c instanceof Hero) {
                Hero h = (Hero) c;
                inspector_XPBar.setMaxValue(h.getRequiredXP());
                inspector_XPBar.setValue(h.getXP());
            }

            inspectorText.setCaretPosition(doc.getLength());
            inspectorText.insertComponent(inspector_XPBar);
            doc.insertString(doc.getLength(), "\n", spacerStyle);

            inspectorText.setCaretPosition(doc.getLength());
            inspectorText.insertComponent(inspector_HpBar);
            //doc.insertString(doc.getLength(), "\n", spacerStyle);

            inspectorText.setCaretPosition(doc.getLength());
            inspectorText.insertComponent(inspector_MpBar);

            //SKILLS
            doc.insertString(doc.getLength(), "\n\n[ SKILLS ]\n", skillStyle);
            if (c.getSkills() != null) {
                for (Skill s : c.getSkills()) {
                    doc.insertString(doc.getLength(), "• " + s.getName() + " ", defaultStyle);
                    doc.insertString(doc.getLength(), "[" + s.getManaCost() + " MP]\n", mpStyle);
                }
            }

            String description = c.getDescription();
            if (description != null && !description.isEmpty()) {
                doc.insertString(doc.getLength(), "\n──────────────── ⋆⋅☆⋅⋆ ────────────────\n\n", grayStyle);
                doc.insertString(doc.getLength(), description, defaultStyle);
            }

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        inspectorText.setCaretPosition(0);
    }

    // =============== PUBLIC GETTERS AND SETTERS FOR UI ===============
    public JTextPane getGameLogPanelTextPane() {
        return GameLogPanelTextPane;
    }

    public JTextPane getGameLogHighlightPanelTextPane() {
        return GameLogHighlightPanelTextPane;
    }

    public CharacterStatusPanel getCharacterPanel(Character character) {
        return characterToPanelMap.get(character);
    }

    public void setBattleBackground(String imageKey) {
        if (battlePanel != null) {
            ((BackgroundPanel)battlePanel).setBackgroundImage(imageKey);
        }
    }
}
