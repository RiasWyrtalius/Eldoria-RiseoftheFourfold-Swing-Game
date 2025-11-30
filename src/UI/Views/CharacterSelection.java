package UI.Views;

import javax.swing.*;
import javax.swing.border.*;
import Characters.Base.Hero;
import Characters.HeroRepository;
import Resource.Animation.Animation;
import Resource.Animation.AssetManager;
import UI.Components.BackgroundPanel;
import Characters.CharacterDisplayData;
import java.awt.*;
import java.util.List;
import Characters.Character;
import UI.Components.StatsRenderer;

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
    private JTextPane statsTA;

    private StatsRenderer statsRenderer;
    private List<CharacterDisplayData> characterList;

    private JLabel characterImageLabel;
    private int currentIndex = 0;
    private Timer animationTimer;
    private Animation currentAnimation;

    public CharacterSelection() {
        this.setContentPane(CharacterSelection);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Character Selection");

        setupPanels();

        if (statsTA != null) {
            statsRenderer = new StatsRenderer(statsTA);
        }

        characterList = HeroRepository.getHeroes();

        CharacterPreview.setLayout(new GridBagLayout());
        characterImageLabel = new JLabel();
        CharacterPreview.add(characterImageLabel);

        //Logic Setup
        setupListeners();
        updateView();

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void updateView() {
        if (characterList.isEmpty()) return;

        CharacterDisplayData data = characterList.get(currentIndex);
        Character myChar = data.getCharacter();

        if (myChar instanceof Hero) {
            Hero myHero = (Hero) myChar;

            if (statsRenderer != null) {
                statsRenderer.updateDisplay(myHero);
            }

            updateAnimation(myHero);
        }
    }

    private void updateAnimation(Hero myHero) {
        String visualID = myHero.getImageKey();

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        if (AssetManager.getInstance().isAnimation(visualID)) {
            currentAnimation = AssetManager.getInstance().getAnimation(visualID);
            currentAnimation.reset();
            ImageIcon firstFrame = currentAnimation.getCurrentFrame();
            Image scaledImg = firstFrame.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            characterImageLabel.setIcon(new ImageIcon(scaledImg));
            startAnimationLoop();
        } else {
            characterImageLabel.setIcon(null);
            characterImageLabel.setText("Missing Asset: " + visualID);
        }
    }

    private void startAnimationLoop() {
        int delay = currentAnimation.getFrameDurationMs();
        animationTimer = new Timer(delay, e -> {
            ImageIcon rawIcon = currentAnimation.getNextFrame();
            if (rawIcon != null) {
                Image img = rawIcon.getImage();
                Image scaledImg = img.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                characterImageLabel.setIcon(new ImageIcon(scaledImg));
            }
        });
        animationTimer.start();
    }

    private void setupListeners() {
        nextButton.addActionListener(e -> {
            currentIndex++;
            textField1.setText("");
            textField2.setText("");
            if (currentIndex >= characterList.size()) currentIndex = 0;
            updateView();
        });

        previousButton.addActionListener(e -> {
            currentIndex--;
            textField1.setText("");
            textField2.setText("");
            if (currentIndex < 0) currentIndex = characterList.size() - 1;
            updateView();
        });

        selectCharacterButton.addActionListener(e -> {
            System.out.println("Selected: " + characterList.get(currentIndex).getCharacter().getName());
        });
    }

    private void setupPanels() {
        infoPanel.setOpaque(false);
        SelectedCharacter.setOpaque(false);
        infoPanel.setPreferredSize(new Dimension(300, 500));

        customizePanel(infoPanel, "Character Information", TitledBorder.LEFT);
        customizePanel(SelectedCharacter, "Selected Hero", TitledBorder.CENTER);

        CharacterPreview.setPreferredSize(new Dimension(500, 500));
        ((BackgroundPanel) CharacterPreview).setBackgroundImage("Assets/Images/Backgrounds/CSelection_BG-TEST.png");
    }

    private void customizePanel(JPanel panel, String titleText, int alignment) {
        Color goldColor = new Color(0xD4AF37);
        Font font = new Font("Georgia", Font.BOLD, 20);
        Border line = BorderFactory.createLineBorder(goldColor, 2);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(line, titleText);
        titledBorder.setTitleFont(font);
        titledBorder.setTitleColor(goldColor);
        titledBorder.setTitleJustification(alignment);
        panel.setBorder(new CompoundBorder(titledBorder, new EmptyBorder(15, 15, 15, 15)));
    }

    private void createUIComponents() { CharacterPreview = new BackgroundPanel(); }
    public static void main(String[] args) { SwingUtilities.invokeLater(CharacterSelection::new); }
}