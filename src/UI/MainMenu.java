package UI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    private JPanel contentPanel;
    private JButton exitButton;
    private JButton startButton;
    private JButton continueButton;

    public MainMenu() {
        JFrame frame = new JFrame();
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //  TODO: enable continue button only if content save file is available
        continueButton.setEnabled(false);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("...Exiting Application");
                System.exit(0);
            }
        });

    }
}
