package UI.Views;

import javax.swing.*;
import javax.swing.border.*;
import Characters.Base.Hero;
import Characters.HeroRepository;
import Core.GameFlow.CharacterSelectionMode;
import Resource.Animation.Animation;
import Resource.Animation.AssetManager;
import UI.Components.AnimatedStatBar;
import UI.Components.BackgroundPanel;
import Characters.CharacterDisplayData;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

import Characters.Character;
import UI.Components.StatsRenderer;
import Abilities.JobClass;

public class CharacterSelection extends JPanel {
    private CharacterSelectionMode mode;
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
    private JPanel barsPanel;

    private JLabel classLabel;
    private AnimatedStatBar hpBar;
    private AnimatedStatBar mpBar;

    private StatsRenderer statsRenderer;
    private List<CharacterDisplayData> characterList;

    private JLabel characterImageLabel;
    private List<ImageIcon> cacheScaledFrames;
    private int currentIndex = 0;
    private Timer animationTimer;
    private Animation currentAnimation;

    private static final int PREVIEW_SIZE = 250;
    private static final Color GOLD_COLOR = new Color(0xD4AF37);

    private final BiConsumer<Hero, String> onSelectionComplete;

    public CharacterSelection(CharacterSelectionMode mode, BiConsumer<Hero, String> onSelectionComplete) {
        this.mode = mode;
        this.onSelectionComplete = onSelectionComplete;

        this.setLayout(new BorderLayout());
        this.setBackground(new Color(0, 0, 0, 200)); // 80% Black Overlay
        this.setOpaque(false);

        JPanel modalContainer = new JPanel(new BorderLayout());
        modalContainer.setBorder(new LineBorder(new Color(0xD4AF37), 3)); // Gold Border

        if (CharacterSelection != null) {
            modalContainer.add(CharacterSelection, BorderLayout.CENTER);
        }

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(modalContainer);
        this.add(centerWrapper, BorderLayout.CENTER);

        setupPanels();
        setupStatBar();

        if (statsTA != null) {
            statsRenderer = new StatsRenderer(statsTA);
            statsTA.setOpaque(false);
            statsTA.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        characterList = HeroRepository.getHeroes();
        CharacterPreview.setLayout(new GridBagLayout());
        characterImageLabel = new JLabel();
        CharacterPreview.add(characterImageLabel);

        //Logic Setup
        setupMode();
        setupListeners();
        updateView();
    }

    private void setupMode() {
        if (textField2 == null) return;
        textField2.setVisible(mode != CharacterSelectionMode.ADD_TO_EXISTING_PARTY);
    }

    private void setupStatBar() {

        hpBar = new AnimatedStatBar(500, new Color(220, 50, 50), "HP: ");
        mpBar = new AnimatedStatBar(300, new Color(50, 150, 255), "MP: ");

        //ensures it doesnt stretch
        hpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mpBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        hpBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        mpBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (textField1 != null) {
            textField1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            textField1.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        if (textField2 != null) {
            textField2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            textField2.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        if (barsPanel != null) {
            barsPanel.setOpaque(false);
            barsPanel.setLayout(new BoxLayout(barsPanel, BoxLayout.Y_AXIS));

            barsPanel.add(hpBar);
            barsPanel.add(Box.createVerticalStrut(5));
            barsPanel.add(mpBar);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Manually paint the semi-transparent background
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Light", Font.BOLD, 16));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void updateView() {
        if (characterList.isEmpty()) return;

        CharacterDisplayData data = characterList.get(currentIndex);
        Character myChar = data.getCharacter();

        if (myChar instanceof Hero) {
            Hero myHero = (Hero) myChar;
            JobClass job = myHero.getJob();

            if (classLabel != null) {
                classLabel.setText("<html>Class: <font color='blue'>" + job.getName() + "</font></html>");
            }

            if (statsRenderer != null) {
                statsRenderer.updateDisplay(myHero);
            }

            hpBar.setValue(myHero.getMaxHealth());
            mpBar.setValue(myHero.getMaxMana());

            updateAnimation(myHero);
        }
    }

    private void updateAnimation(Hero myHero) {
        String visualID = myHero.getIdleImageKey();

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

        selectCharacterButton.addActionListener(e -> {
            String nameInput = textField1.getText().trim();
            String partyNameInput = textField2.getText().trim();
            String partyName = partyNameInput.isEmpty() ? null : partyNameInput;

            if (nameInput.isEmpty() && partyNameInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Hero Name and a Party Name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            else if (nameInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Hero Name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (partyNameInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Party Name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }


            CharacterDisplayData data = characterList.get(currentIndex);
            Hero template = (Hero) data.getCharacter();
            template.setName(nameInput);

            System.out.println("Selected: " + template.getName());

            if (onSelectionComplete != null) {
                onSelectionComplete.accept(template, partyName);
            }
        });

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
        infoPanel.setPreferredSize(new Dimension(300, 550));

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
}