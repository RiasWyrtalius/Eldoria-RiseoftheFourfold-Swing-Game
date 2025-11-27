package UI.Views;

import Abilities.Skill;
import Core.Battle.*;
import Characters.Base.Hero;
import Characters.Character;
import Core.Utils.LogManager;
import Items.Inventory;
import Items.Item;
import UI.Components.BackgroundPanel;
import UI.Components.BattleUIMode;
import UI.Components.CharacterStatusPanel;
import UI.Components.InventoryPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

// TODO: DESCEND calls battle controller->game manager for next battle
public class BattleInterface extends JFrame{
    private BattleController battleController;

    private JPanel contentPanel;
    private JPanel battlePanel;
    private JTextPane GameLogPanelTextPane;
    private JTextPane GameLogHighlightPanelTextPane;
    private JPanel inventoryPanel;

    private JLabel heroPartyLabel;
    private JPanel heroPartyPanel1;
    private JPanel heroPartyPanel2;
    private JPanel heroPartyPanel3;
    private JPanel heroPartyPanel4;

    private JLabel enemyPartyLabel;
    private JPanel enemyPartyPanel1;
    private JPanel enemyPartyPanel2;
    private JPanel enemyPartyPanel3;
    private JPanel enemyPartyPanel4;

    private JPanel descendPanel;

    private List<JPanel> heroPartyPanels;
    private List<JPanel> enemyPartyPanels;

    private JScrollPane CharacterInspector_JSP;
    private JTextArea inspectorText;

    private final Map<Character, CharacterStatusPanel> characterToPanelMap = new HashMap<>();

    // STATE MACHINE FIELDS
    private BattleUIMode currentMode = BattleUIMode.HERO_SELECT;
    private Hero activeHero = null;
    private Skill selectedSkill = null;
    private List<Character> selectedTargets = new ArrayList<>();
    private Item selectedItem = null;
    private int maxTargetsAllowed;

    private JPopupMenu targetConfirmMenu = new JPopupMenu();

    private JButton endTurnButton;
    private JButton descendButton;
    private JPanel heroPanel;
    private JPanel infoPanel;
    private JPanel enemyPanel;

    public BattleInterface() {
        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupInspector();
        this.pack();
        this.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        // shows when battle ends and heroes win to
        descendPanel.setVisible(false);

        GameLogPanelTextPane.setEditable(false);
        GameLogHighlightPanelTextPane.setEditable(false);
        heroPartyPanels = Arrays.asList(heroPartyPanel1, heroPartyPanel2, heroPartyPanel3, heroPartyPanel4);
        enemyPartyPanels = Arrays.asList(enemyPartyPanel1, enemyPartyPanel2, enemyPartyPanel3, enemyPartyPanel4);
    }

    public void linkControllerAndData(BattleController controller) {
        this.battleController = controller;

        listenerInit();
        refreshUI();
    }

    public void listenerInit() {
        if (endTurnButton != null) {
            endTurnButton.addActionListener(e -> {
//                resetSelectionState();
                battleController.endHeroPhaseManually();
                refreshUI();
            });
        }
        refreshUI();
    }

    public void refreshUI() {
        heroPartyLabel.setText(battleController.getHeroParty().getPartyName());
        enemyPartyLabel.setText(battleController.getEnemyParty().getPartyName());

        if (activeHero != null && selectedSkill != null) {
            LogManager.log("Active Hero: " + activeHero.getName());
            LogManager.log("Selected Skill: " + selectedSkill.getName());
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
            if (result == BattleResult.VICTORY)
                descendPanel.setVisible(true);
        } else {
            endTurnButton.setVisible(true);
            descendPanel.setVisible(false);
        }
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


    public void onCharacterPanelClick(Character clickedCharacter) {

        updateInspectorPanel(clickedCharacter);

        switch (currentMode) {
            case HERO_SELECT:
                LogManager.log("SELECT HERO");
                if (clickedCharacter instanceof Hero) {
                    Hero hero = (Hero)clickedCharacter;
                    if (hero.isAlive() && !hero.isExhausted()) {
                        onHeroSelect(hero);
                    } else {
                        LogManager.log(hero.getName() + " cannot start an action now.");
                    }
                } else LogManager.log("Enemy is Selected!");
                break;

            case TARGET_SELECT:
                LogManager.log("SELECT TARGET");

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
                    LogManager.log("Deselected " + clickedCharacter.getName());
                }
                else {
                    // check selection limit
                    if (selectedTargets.size() < typeRule.getMaxTargets()) {
                        selectedTargets.add(clickedCharacter);
                        LogManager.log("Selected " + clickedCharacter.getName() +
                                " (" + selectedTargets.size() + "/" + typeRule.getMaxTargets() + ")");
                        panel.setSelectionOverlay(true);
                    } else {
                        LogManager.log("Maximum targets reached (" + typeRule.getMaxTargets() + ").");
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
            LogManager.log("Cannot confirm: No targets selected.");
            return;
        }

        if (selectedSkill != null) {
            battleController.executeActionFromUI(activeHero, selectedSkill, selectedTargets);
        }
        else if (selectedItem != null) {
            battleController.executeItemActionFromUI(selectedItem, selectedTargets);
        }

        hideTargetConfirmMenu();
        resetSelectionState();
        refreshUI();

//        if (currentMode != BattleUIMode.TARGET_SELECT) return;
//
//        // TODO: sometimes items or skills require no targets
//
//        if (selectedTargets.isEmpty()) {
//            LogManager.log("Cannot confirm: No targets selected.");
//            return;
//        }
//        if (currentMode == BattleUIMode.ITEM_SELECT) {
//            battleController.executeItemActionFromUI(selectedItem, selectedTargets);
//        } else {
//            battleController.executeActionFromUI(activeHero, selectedSkill, selectedTargets);
//        }
//
//        hideTargetConfirmMenu();
//
//        resetSelectionState();
//        refreshUI();

//        TargetType type = null;
//        if (selectedSkill != null) type = selectedSkill.getTargetType();
//        else if (selectedItem != null) type = selectedItem.getTargetType();
//
//        // Only block empty selection if the type REQUIRES targets
//        boolean requiresSelection = (type == TargetType.SINGLE_TARGET ||
//                type == TargetType.AOE_TWO_TARGETS ||
//                type == TargetType.AOE_THREE_TARGETS);
//
//        if (requiresSelection && selectedTargets.isEmpty()) {
//            LogManager.log("Cannot confirm: No targets selected.");
//            return;
//        }
//
//        if (selectedSkill != null) {
//            battleController.executeActionFromUI(activeHero, selectedSkill, selectedTargets);
//        } else if (selectedItem != null) {
//            battleController.executeItemActionFromUI(selectedItem, selectedTargets);
//        }
//
//        hideTargetConfirmMenu();
////        resetSelectionState();
//        refreshUI();
    }

    public void onHeroSelect(Hero clickedHero) {
        if (currentMode != BattleUIMode.HERO_SELECT) return;

        if (clickedHero.isAlive() && !clickedHero.isExhausted()) {
            this.activeHero = clickedHero;
            this.currentMode = BattleUIMode.SKILL_SELECT;

            showSkillSelectionMenu(clickedHero);

            refreshUI();
        } else {
            LogManager.log(clickedHero.getName() + " is unable to act.");
        }
    }

    public void onSkillSelect(Skill skill) {
        if (currentMode != BattleUIMode.SKILL_SELECT) return;

        this.selectedSkill = skill;
        this.currentMode = BattleUIMode.TARGET_SELECT;
        TargetType type = skill.getTargetType();

        if (type == TargetType.NO_TARGETS) {
            onConfirmAction();
            return;
        }

        LogManager.log("Select a target for " + skill.getName());
        refreshUI();
    }

    public void onItemSelect(Item item) {
        LogManager.log("ITEM SELECTED: " + item.getName());
        this.currentMode = BattleUIMode.ITEM_SELECT;

        this.selectedSkill = null;
        this.selectedItem = item;
        this.selectedTargets.clear();

        TargetType type = item.getTargetType();

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

        LogManager.log("Select up to " + type.getMaxTargets() + " target(s).");
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
                LogManager.log(finalActionType + " selection cancelled");
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
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                LogManager.log("Skill selection cancelled");
//                resetSelectionState();
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
        inspectorText = new JTextArea();
        inspectorText.setEditable(false);
        inspectorText.setLineWrap(true);
        inspectorText.setWrapStyleWord(true);

        if (CharacterInspector_JSP != null) {
            CharacterInspector_JSP.setViewportView(inspectorText);
        }
    }

    private void updateInspectorPanel(Character c) {
        if (inspectorText == null) { return; }

        if (c == null) {
            inspectorText.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();

        // --- NAME & CLASS ---
        sb.append(c.getName().toUpperCase()).append("\n");
        if (c instanceof Hero) {
            sb.append(((Hero) c).getJob().getName()).append("\n");
        }
        sb.append("----------------\n");

        sb.append("LVL: " + c.getLevel()).append("\n");
        if (c instanceof Hero) {
            Hero h = (Hero)c;
            sb.append("XP: ").append(h.getXP()).append("/").append(h.getRequiredXP()).append("\n");
        }
        // --- HP & MANA ---
        sb.append("HP: ").append(c.getHealth()).append("/").append(c.getInitialHealth()).append("\n");
        sb.append("MP: ").append(c.getMana()).append("/").append(c.getMaxMana()).append("\n");
        sb.append("\n");

        sb.append("[ SKILLS ]\n");
        // --- SKILLS
        for (Skill s : c.getSkills()) {
            sb.append("• ").append(s.getName())
                    .append(" (").append(s.getManaCost()).append(" MP)\n");
        }

        String description = c.getDescription();
        if (description != null) {
            sb.append("----------------\n");
            sb.append(c.getDescription());
        }

        inspectorText.setText(sb.toString());
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
