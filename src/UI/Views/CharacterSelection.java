package UI.Views;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.StyledDocument;


import UI.Components.BackgroundPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelection extends JFrame {
    private JPanel CharacterSelection;
    private JPanel CharacterPreview;
    private JButton selectCharacterButton;
    private JPanel SelectedCharacter;
    private JPanel infoPanel;
    private JButton nextButton;
    private JButton previousButton;
    private JTextField textField1;
    private JTextField textField2;
    private JTextArea statsTA;

    //for dynamic character
    private JLabel characterImageLabel;
    private JTextArea infoTextArea;


    //data holder & logic
    private List<CharacterDisplayData> characterList;
    private int currentIndex = 0;

    public CharacterSelection() {
        this.setContentPane(CharacterSelection);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Character Selection");

        infoPanel.setOpaque(false);
        SelectedCharacter.setOpaque(false);

        infoPanel.setPreferredSize(new Dimension(300, 500));

        //border Setup
        customizePanel(infoPanel, "Character Information", TitledBorder.LEFT);
        customizePanel(SelectedCharacter, "Selected Hero", TitledBorder.CENTER);

        //image Setup
        CharacterPreview.setPreferredSize(new Dimension(500, 500));
        ((BackgroundPanel) CharacterPreview).setBackgroundImage("Assets/Images/Backgrounds/CSelection_BG.png");

        initCharacterData(); // temporary data holder
        setupPanels(); // for styles

        CharacterPreview.setLayout(new GridBagLayout());
        characterImageLabel = new JLabel();
        CharacterPreview.add(characterImageLabel);

        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.add(scrollPane, BorderLayout.PAGE_END);

        infoTextArea = new JTextArea();
        infoTextArea.setOpaque(false);
        infoTextArea.setEditable(false);
        infoTextArea.setForeground(Color.BLACK);

        //TODO: IMPLEMENT JUSTIFY CONTENT
        //infoTextArea.add(infoTextArea, BorderLayout.CENTER);

        setupListeners(); //buttons
        updateView();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void setupPanels() {
        infoPanel.setOpaque(false);
        SelectedCharacter.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(300, 500));

        // Border Setup
        customizePanel(infoPanel, "Character Information", TitledBorder.LEFT);
        customizePanel(SelectedCharacter, "Selected Hero", TitledBorder.CENTER);

        // Image Setup (Background)
        CharacterPreview.setPreferredSize(new Dimension(500, 500));
        ((BackgroundPanel) CharacterPreview).setBackgroundImage("Assets/Images/Backgrounds/CSelection_BG.png");
    }

    private void initCharacterData() {
        characterList = new ArrayList<>();
        // Add characters here.
        characterList.add(new CharacterDisplayData("Warrior",
                "Assets/Animations/Heroes/Warrior/Idle/sprite_0.png",
                120,
                100,
                "A battle-hardened fighter clad in steel, the Warrior thrives on the frontlines. With unmatched strength and resilience, " +
                        "he shields allies from harm while delivering crushing blows to enemies. His loyalty and courage make him the backbone of any party."
        ));
        characterList.add(new CharacterDisplayData("Archer", "Assets/Animations/Heroes/Archer/Idle/sprite_0.png", 80, 100, "Agile and precise, the Archer strikes from afar with deadly accuracy. Her keen eyesight and swift reflexes allow her to rain arrows upon foes before they can close the distance. She embodies speed, cunning, and tactical finesse."));
        characterList.add(new CharacterDisplayData("AeroMancer", "Assets/Animations/Heroes/Mage-Wind/Idle/sprite_0.png", 100, 120, "Master of the skies, the AeroMancer bends the wind to her will. She summons gales to scatter enemies, rides currents to evade danger, and unleashes razor-sharp blasts of air. Her magic is swift, elusive, and devastating."));
        characterList.add(new CharacterDisplayData("Mage Earth", "Assets/Animations/Heroes/Mage-Earth/Idle/sprite_0.png", 100, 120, "A stalwart spellcaster who channels the raw power of stone and soil. The Mage Earth conjures barriers, summons tremors, and hardens allies’ defenses. Steadfast and immovable, she is the embodiment of endurance and stability."));
        characterList.add(new CharacterDisplayData("Mage Fire", "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_0.png", 100, 120, "Fierce and unpredictable, the Mage Fire wields flames with destructive passion. She incinerates her foes with fireballs, engulfs battlefields in blazing infernos, and thrives on chaos. Her magic is as dangerous as it is mesmerizing."));
        characterList.add(new CharacterDisplayData("CryoMancer", "Assets/Animations/Heroes/Mage-Ice/Idle/sprite_0.png", 100, 120, "Cold and calculating, the CryoMancer freezes enemies in their tracks. She conjures blizzards, sharp ice shards, and chilling prisons to sap the strength of her foes. Her frosty power brings control and precision to the battlefield."));
        characterList.add(new CharacterDisplayData("Cleric", "Assets/Animations/Heroes/Cleric/Idle/sprite_0.png", 100, 120, "A devoted healer and protector, the Cleric channels divine energy to restore allies and banish darkness. Her blessings strengthen companions, while her radiant light wards off evil. Compassionate yet formidable, she is the heart of the party."));
    }


    private void setupListeners() {
        nextButton.addActionListener(e -> {
            currentIndex++;
            if (currentIndex >= characterList.size()) {
                currentIndex = 0;
            }
            updateView();
        });

        previousButton.addActionListener(e -> {
            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = characterList.size() - 1;
            }
            updateView();

        });

        selectCharacterButton.addActionListener(e -> {
            CharacterDisplayData selected = characterList.get(currentIndex);
            System.out.println("Selected Character: " + selected.name);
            // TODO: pass this selection to your BattleController/Game Manager
        });
    }



    private void updateView() {
        if (characterList.isEmpty()) return;

        CharacterDisplayData data = characterList.get(currentIndex);

        // Update Text
        statsTA.setText("Name: " + data.name + "\n" + "Base HP: " + data.baseHP + "\n" + "Base MP: " + data.baseMP + "\n" + data.desc);

        // this updates the image
        ImageIcon icon = new ImageIcon(data.imagePath);
        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(300, 300,  java.awt.Image.SCALE_SMOOTH); // size
            characterImageLabel.setIcon(new ImageIcon(newImg));
        } else {
            characterImageLabel.setText("[" + data.name + " Image Missing]");
            characterImageLabel.setIcon(null);
        }
    }

    // data holder
    private static class CharacterDisplayData {
        String name;
        String imagePath;
        int baseHP;
        int baseMP;
        String desc;

        public CharacterDisplayData(String name,String imagePath, int baseHP, int baseMP, String desc) {
            this.name = name;
            this.imagePath = imagePath;
            this.baseHP = baseHP;
            this.baseMP = baseMP;
            this.desc = desc;
        }
    }

    private void customizePanel(JPanel panel, String titleText, int alignment) {
        Color goldColor = new Color(0xD4AF37);
        Font font = new Font("Georgia", Font.BOLD, 20);

        Border line = BorderFactory.createLineBorder(goldColor, 2);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(line, titleText);
        titledBorder.setTitleFont(font);
        titledBorder.setTitleColor(goldColor);
        titledBorder.setTitleJustification(alignment);

        Border padding = new EmptyBorder(15, 15, 15, 15);
        panel.setBorder(new CompoundBorder(titledBorder, padding));
    }

    private void createUIComponents() {
        CharacterPreview = new BackgroundPanel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CharacterSelection::new);
    }
}