package UI;

import Abilities.JobClass;
import Abilities.Jobs.FireMage;
import Characters.Base.Hero;
import Characters.Party;
import Core.BattleController;
import Core.LogManager;
import UI.Components.CharacterStatusPanel;

import java.util.List;
import javax.swing.*;
import java.awt.*;

public class MainInterface extends JFrame{
    private JPanel heroPanel;
    private JPanel enemyPanel;

    private JPanel contentPanel;
    private JTextArea GameLogPanelTextArea;

    private JPanel sample;
    private JPanel heroPartyPanel4;

    private List<JPanel> heroPartyPanel;
    private List<JPanel> enemyPartyPanel;

//    sample data
    JobClass  mageJob = new FireMage();
    Hero kai = new Hero("Kai", 150, 15, 60, mageJob, "Assets/Images/bstudios.png");

    public MainInterface() {
        this.setContentPane(contentPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        setTitle("DND Swing Clone | Saja Boys");

        GameLogPanelTextArea.setEditable(false);
        LogManager.initialize(GameLogPanelTextArea);

        ((CharacterStatusPanel)heroPartyPanel4).setCharacterData(kai);
    }

    /**
     * Called by Main.java to give bind BattleController and give UI the party data
     * @param controller
     */
    public void linkController(BattleController controller) {

    }

    private void setHeroPartyUI() {

    }

    private void createUIComponents() {
        heroPartyPanel4 = new CharacterStatusPanel();
    }
}
