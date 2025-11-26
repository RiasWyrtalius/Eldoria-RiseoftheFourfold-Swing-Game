package UI.Views;

import Abilities.Skill;
import Abilities.SkillTarget;
import Characters.Base.Hero;
import Characters.Character;
import Core.Battle.BattleController;
import Core.Battle.BattlePhase;
import Core.Battle.BattleResult;
import Core.Utils.LogManager;
import UI.Components.CharacterStatusPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

// TODO: Selected targets also highlight their background
// TODO: DESCEND calls battle controller->game manager for next battle

public class MainInterface extends JFrame{
    private BattleController battleController;

    private JPanel contentPanel;
    private JTextPane GameLogPanelTextPane;

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
    private JLabel battleOutcome;

    private JScrollPane CharacterInspector_JSP;
    private JTextArea inspectorText;

    private final Map<Character, CharacterStatusPanel> characterToPanelMap = new HashMap<>();

    // STATE MACHINE FIELDS
    private BattleUIMode currentMode = BattleUIMode.HERO_SELECT;
    private Hero activeHero = null;
    private Skill selectedSkill = null;
    private List<Character> selectedTargets = new ArrayList<>();

    private JPopupMenu targetConfirmMenu = new JPopupMenu();

    private JButton endTurnButton;
    private JButton descendButton;

    public MainInterface() {
        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupInspector();
        this.pack();
        this.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        // shows when battle ends
        battleOutcome.setVisible(false);
        // shows when battle ends and heroes win to
        descendPanel.setVisible(false);

        GameLogPanelTextPane.setEditable(false);
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
                resetSelectionState();
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

        updateControls();
    }

    private void updateControls() {
        BattlePhase phase = battleController.getCurrentPhase();
        endTurnButton.setEnabled(phase == BattlePhase.HERO_ACTION_WAIT);
        if (phase == BattlePhase.BATTLE_ENDED) {
            // TODO: set battleOutcome JLabel to victory, or tie
            BattleResult result = battleController.getFinalResult();
            String resultText = switch (result) {
                case VICTORY -> "VICTORY! Well Done!";
                case DEFEAT -> "DEFEAT! Game Over.";
                case TIE -> "TIE! Game Over. Truly no one wins in the end.";
                default -> "Battle Ended.";
            };
            battleOutcome.setText(resultText);
            battleOutcome.setVisible(true);
            endTurnButton.setVisible(false);
            if (result == BattleResult.VICTORY)
                descendPanel.setVisible(true);
        } else {
            battleOutcome.setVisible(false);
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
//                LogManager.log("SELECT HERO");
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
//                LogManager.log("SELECT TARGET");
                SkillTarget requiredTarget = selectedSkill.getSkillTarget();

                CharacterStatusPanel panel = getCharacterPanel(clickedCharacter);
                if (panel == null) return;

                if (!clickedCharacter.isAlive()) {
                    LogManager.log(clickedCharacter.getName() + " is already knocked out and cannot be targeted.");
                    return;
                }

                if (selectedTargets.contains(clickedCharacter)) {
                    selectedTargets.remove(clickedCharacter);
                    panel.setSelectionOverlay(false);
                    LogManager.log("Deselected " + clickedCharacter.getName() + ".");
                } else if (selectedTargets.size() < requiredTarget.getMaxTargets()){
                    selectedTargets.add(clickedCharacter);
                    LogManager.log("Selected " + selectedSkill.getName() + " (" +
                            selectedTargets.size() + "/" + requiredTarget.getMaxTargets() + ")");
                    showTargetConfirmMenu(clickedCharacter);
                    panel.setSelectionOverlay(true);
                } else {
                    LogManager.log("Maximum targets (" + requiredTarget.getMaxTargets() + ") already selected.");
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
        if (currentMode != BattleUIMode.TARGET_SELECT) return;

        if (selectedTargets.isEmpty()) {
            LogManager.log("Cannot confirm: No targets selected.");
            return;
        }

        battleController.executeActionFromUI(activeHero, selectedSkill, selectedTargets);

        hideTargetConfirmMenu();

        resetSelectionState();
        refreshUI();
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

        LogManager.log("Select a target for " + skill.getName());
        refreshUI();
    }

    private void resetSelectionState() {
        this.activeHero = null;
        this.selectedSkill = null;
        this.currentMode = BattleUIMode.HERO_SELECT;
        selectedTargets = new ArrayList<>();
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
                // prolly will add more targets
            }
        });

        JMenuItem confirm = new JMenuItem("Confirm (" +
                selectedTargets.size() + "/" + selectedSkill.getSkillTarget().getMaxTargets() + ")");
        confirm.addActionListener(e -> onConfirmAction()); // TODO:
        JMenuItem cancel = new JMenuItem("Cancel Skill ");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogManager.log("Skill selection cancelled");
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

        // --- HP & MANA ---
        sb.append("HP: ").append(c.getHealth()).append("/").append(c.getInitialHealth()).append("\n");
        sb.append("MP: ").append(c.getMana()).append("/").append(c.getMaxMana()).append("\n");
        sb.append("\n");

        // --- SKILLS (Heroes only) ---
        if (c instanceof Hero) {
            sb.append("[ SKILLS ]\n");
            Hero h = (Hero) c;
            for (Skill s : h.getJob().getSkills()) {
                sb.append("• ").append(s.getName())
                        .append(" (").append(s.getManaCost()).append(" MP)\n");
            }
        }

        inspectorText.setText(sb.toString());
        inspectorText.setCaretPosition(0);
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    public JTextPane getGameLogPanelTextPane() {
        return GameLogPanelTextPane;
    }

    public CharacterStatusPanel getCharacterPanel(Character character) {
        return characterToPanelMap.get(character);
    }
}
