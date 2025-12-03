package UI.Views;

import Core.Story.StorySlide;
import UI.Components.BackgroundPanel;
import UI.Components.FantasyDialogPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StoryView extends JPanel {
    private JPanel backgroundPanel;
    private JTextPane contentPane = null;
    private JPanel textContainer; // Changed to custom class

    private List<StorySlide> sequence;
    private int currentSlideIndex = 0;
    private int currentLineIndex = 0;
    private Runnable onFinishCallback;
    private final Timer typeTimer;
    private String fullTextToDisplay = "";
    private int typeCharIndex = 0;
    private boolean isTyping = false;
    private final SimpleAttributeSet narrationStyle;

    private static final Color TEXT_COLOR = new Color(245, 240, 220);

    public StoryView() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        backgroundPanel.setLayout(new BorderLayout());
        this.add(backgroundPanel, BorderLayout.CENTER);

        contentPane.setOpaque(false);
        contentPane.setEditable(false);
        contentPane.setFocusable(false);
        contentPane.setHighlighter(null);

        textContainer = new FantasyDialogPanel();
        textContainer.setLayout(new BorderLayout());

        textContainer.setBorder(new EmptyBorder(35, 60, 35, 60));


        textContainer.setPreferredSize(new Dimension(1280, 220));

        textContainer.add(contentPane, BorderLayout.CENTER);


        backgroundPanel.add(textContainer, BorderLayout.SOUTH);


        narrationStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(narrationStyle, TEXT_COLOR);

        StyleConstants.setFontFamily(narrationStyle, "Georgia");
        StyleConstants.setFontSize(narrationStyle, 26);
        StyleConstants.setItalic(narrationStyle, true);
        StyleConstants.setAlignment(narrationStyle, StyleConstants.ALIGN_CENTER);

        typeTimer = new Timer(30, e -> updateTypewriter());

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleInput();
            }
        });
    }

    private void createUIComponents() {
        this.backgroundPanel = new BackgroundPanel();
        this.textContainer = new FantasyDialogPanel();
    }

    public void startSequence(List<StorySlide> sequence, Runnable onFinish) {
        this.sequence = sequence;
        this.onFinishCallback = onFinish;
        this.currentSlideIndex = 0;
        this.currentLineIndex = 0;
        typeTimer.stop();
        isTyping = false;
        loadCurrentSlide();
    }

    private void loadCurrentSlide() {
        if (currentSlideIndex >= sequence.size()) return;
        StorySlide slide = sequence.get(currentSlideIndex);
        ((BackgroundPanel)backgroundPanel).setBackgroundImage(slide.imageKey());
        if (slide.onStart() != null) {
            try { slide.onStart().run(); }
            catch (Exception e) { e.printStackTrace(); }
        }
        playCurrentLine();
    }

    private void playCurrentLine() {
        StorySlide slide = sequence.get(currentSlideIndex);
        if (slide.lines() != null && !slide.lines().isEmpty()) {
            String text = slide.lines().get(currentLineIndex);
            startTypewriter(text);
        }
    }

    private void handleInput() {
        if (sequence == null || sequence.isEmpty()) return;
        if (isTyping) {
            completeTypewriter();
        } else {
            StorySlide currentSlide = sequence.get(currentSlideIndex);
            if (currentLineIndex < currentSlide.lines().size() - 1) {
                currentLineIndex++;
                playCurrentLine();
            } else {
                currentSlideIndex++;
                if (currentSlideIndex < sequence.size()) {
                    currentLineIndex = 0;
                    loadCurrentSlide();
                } else {
                    if (onFinishCallback != null) onFinishCallback.run();
                }
            }
        }
    }

    private void startTypewriter(String text) {
        this.fullTextToDisplay = text;
        this.typeCharIndex = 0;
        this.isTyping = true;
        contentPane.setText("");
        StyledDocument doc = contentPane.getStyledDocument();
        doc.setParagraphAttributes(0, 0, narrationStyle, true);
        typeTimer.start();
    }

    private void updateTypewriter() {
        if (typeCharIndex < fullTextToDisplay.length()) {
            try {
                StyledDocument doc = contentPane.getStyledDocument();
                String charToAdd = String.valueOf(fullTextToDisplay.charAt(typeCharIndex));
                doc.insertString(doc.getLength(), charToAdd, narrationStyle);
                typeCharIndex++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isTyping = false;
            typeTimer.stop();
        }
    }

    private void completeTypewriter() {
        typeTimer.stop();
        contentPane.setText("");
        try {
            StyledDocument doc = contentPane.getStyledDocument();
            doc.setParagraphAttributes(0, 0, narrationStyle, true);
            doc.insertString(0, fullTextToDisplay, narrationStyle);
        } catch (Exception e) {
            contentPane.setText(fullTextToDisplay);
        }
        isTyping = false;
    }
}