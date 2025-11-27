package UI.Views;

import javax.swing.*;
import javax.swing.border.*;
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

        // Add text area to info panel
        infoPanel.setLayout(new BorderLayout());
        infoTextArea = new JTextArea();
        infoTextArea.setOpaque(false);
        infoTextArea.setEditable(false);
        infoTextArea.setForeground(Color.BLACK);
        infoPanel.add(infoTextArea, BorderLayout.CENTER);

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
        characterList.add(new CharacterDisplayData("Warrior", "Assets/Animations/Heroes/Warrior/Idle/sprite_0.png"));
        characterList.add(new CharacterDisplayData("Mage Wind", "Assets/Animations/Heroes/Mage-Wind/Idle/sprite_0.png"));
        characterList.add(new CharacterDisplayData("Mage Earth", "Assets/Animations/Heroes/Mage-Earth/Idle/sprite_0.png"));
        characterList.add(new CharacterDisplayData("Mage Fire", "Assets/Animations/Heroes/Mage-Fire/Idle/sprite_0.png"));
        characterList.add(new CharacterDisplayData("Mage Ice", "Assets/Animations/Heroes/Mage-Ice/Idle/sprite_0.png"));
        characterList.add(new CharacterDisplayData("Archer", "Assets/Animations/Heroes/Archer/Idle/sprite_0.png"));
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
        infoTextArea.setText("Name: " + data.name + "\n\n");

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

        public CharacterDisplayData(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
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