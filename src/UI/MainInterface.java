package UI;

import Characters.Character;
import Characters.Party;
import Core.BattleController;
import Core.BattlePhase;
import UI.Components.CharacterStatusPanel;

import java.util.Arrays;
import java.util.List;
import javax.swing.*;

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

    private JButton endTurnButton;

    public MainInterface(BattleController battleController) {
        this.battleController = battleController;

        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        GameLogPanelTextArea.setEditable(false);
        heroPartyPanels = Arrays.asList(heroPartyPanel1, heroPartyPanel2, heroPartyPanel3, heroPartyPanel4);
        enemyPartyPanels = Arrays.asList(enemyPartyPanel1, enemyPartyPanel2, enemyPartyPanel3, enemyPartyPanel4);

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
        setPartyUI(battleController.getHeroParty().getPartyMembers(), heroPartyPanels);
        setPartyUI(battleController.getEnemyParty().getPartyMembers(), enemyPartyPanels);

        updateControls();
    }

    private void updateControls() {
        BattlePhase phase = battleController.getCurrentPhase();

        boolean shouldEnable = (phase == BattlePhase.HERO_ACTION_WAIT);

        endTurnButton.setEnabled(shouldEnable);

//        TODO: Logic for skill buttons, enabling hero selection also goes here
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
        heroPartyPanel1 = new CharacterStatusPanel();
        heroPartyPanel2 = new CharacterStatusPanel();
        heroPartyPanel3 = new CharacterStatusPanel();
        heroPartyPanel4 = new CharacterStatusPanel();

        enemyPartyPanel1 = new CharacterStatusPanel();
        enemyPartyPanel2 = new CharacterStatusPanel();
        enemyPartyPanel3 = new CharacterStatusPanel();
        enemyPartyPanel4 = new CharacterStatusPanel();
    }

    public JTextArea getGameLogPanelTextArea() {
        return GameLogPanelTextArea;
    }

}
