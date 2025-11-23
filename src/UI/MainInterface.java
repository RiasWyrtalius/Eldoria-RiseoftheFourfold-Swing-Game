package UI;

import Abilities.Skill;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;
import Core.BattleController;
import Core.BattlePhase;
import Core.LogManager;
import UI.Components.CharacterStatusPanel;

import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class MainInterface extends JFrame{
    private BattleController battleController;

    private JPanel contentPanel;
    private JTextArea GameLogPanelTextArea;

    private JPanel heroPartyPanel1;
    private JPanel heroPartyPanel2;
    private JPanel heroPartyPanel3;
    private JPanel heroPartyPanel4;

    private JPanel enemyPartyPanel1;
    private JPanel enemyPartyPanel2;
    private JPanel enemyPartyPanel3;
    private JPanel enemyPartyPanel4;

    private List<JPanel> heroPartyPanels;
    private List<JPanel> enemyPartyPanels;

    // STATE MACHINE FIELDS
    private BattleUIMode currentMode = BattleUIMode.HERO_SELECT;
    private Hero activeHero = null;
    private Skill selectedSkill = null;

    private JPopupMenu skillMenu;

    private JButton endTurnButton;

    public MainInterface() {
        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        GameLogPanelTextArea.setEditable(false);
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
                battleController.endHeroPhaseManually();
                refreshUI();
            });
        }
    }

    public void refreshUI() {
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

        boolean shouldEnable = (phase == BattlePhase.HERO_ACTION_WAIT);

        endTurnButton.setEnabled(shouldEnable);
    }

    private void setPartyUI(List<Character> party, List<JPanel> setupPanel) {
        // call set character data for each party
        for (int i = 0; i < setupPanel.size(); i++) {
            CharacterStatusPanel panel = (CharacterStatusPanel)setupPanel.get(i);
            if (i < party.size()) {
                panel.setCharacterData(party.get(i));
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
                onTargetSelect(clickedCharacter);
                break;
            case SKILL_SELECT:
                LogManager.log("SELECT SKILL");
            case IDLE:
                LogManager.log("IDLE");
                break;
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

    public void onTargetSelect(Character target) {
        if (currentMode != BattleUIMode.TARGET_SELECT) return;

        battleController.executeActionFromUI(activeHero, selectedSkill, target);
        resetState();
    }

    private void resetState() {
        activeHero = null;
        selectedSkill = null;
        currentMode = BattleUIMode.HERO_SELECT;
    }

    private void resetSelectionState() {
        this.activeHero = null;
        this.selectedSkill = null;
        this.currentMode = BattleUIMode.HERO_SELECT;
        refreshUI();
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

        LogManager.log("Skill menu shown for " + hero.getName() + ".");
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
            JMenuItem item = new JMenuItem(skill.getName() + "\t(" + skill.getManaCost() + " ) MP");
            item.addActionListener(e -> onSkillSelect(skill));
            menu.add(item);
        }
        return menu;
    }
    // =============== PUBLIC GETTERS FOR UI ===============

    public JTextArea getGameLogPanelTextArea() {
        return GameLogPanelTextArea;
    }


}
