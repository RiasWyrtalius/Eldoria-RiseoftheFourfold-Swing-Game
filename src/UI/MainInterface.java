package UI;

import Abilities.JobClass;
import Abilities.Jobs.FireMage;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Party;
import Core.BattleController;
import Core.LogManager;
import UI.Components.CharacterStatusPanel;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class MainInterface extends JFrame{
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

    private BattleController battleController;

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

        setHeroPartyUI();
        setEnemyPartyUI();
    }

    // TODO: Party UI generation
    private void setHeroPartyUI() {
        // call set character data for each party
        Party heroParty = battleController.getHeroParty();
        List<Character> heroes = heroParty.getPartyMembers();

        for (int i = 0; i < heroPartyPanels.size(); i++) {
            CharacterStatusPanel panel = (CharacterStatusPanel)heroPartyPanels.get(i);

            if (i < heroes.size()) {
                panel.setCharacterData(heroes.get(i));
            } else {
                panel.setCharacterData(null);
            }
        }
    }

    private void setEnemyPartyUI() {
        // call set character data for each party
        Party enemyParty = battleController.getEnemyParty();
        List<Character> heroes = enemyParty.getPartyMembers();

        for (int i = 0; i < enemyPartyPanels.size(); i++) {
            CharacterStatusPanel panel = (CharacterStatusPanel)enemyPartyPanels.get(i);

            if (i < heroes.size()) {
                panel.setCharacterData(heroes.get(i));
            } else {
                panel.setCharacterData(null);
            }
        }
    }

    private void createUIComponents() {
        // omfg heroparty panels aren't put in a list yet here lol so we
        // can't even run this
//        for (int i = 0; i < heroPartyPanels.size(); i++) {
//            heroPartyPanels.set(i, new CharacterStatusPanel());
//        }
//        for (int i = 0; i < enemyPartyPanels.size(); i++) {
//            enemyPartyPanels.set(i, new CharacterStatusPanel());
//        }
//        do it all manually >:P
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
