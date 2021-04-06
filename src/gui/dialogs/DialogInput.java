package gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DialogInput extends JDialog {

    public JTextField input;

    public DialogInput(String title, String message, String defaultInput, String buttonText, Point location) {
        setLocation(location);
        setTitle(title);
        JLabel label = new JLabel(" " + message + " ");
        input = new JTextField(defaultInput);
        JButton button = new JButton(" " + buttonText + " ");
        setLayout(new FlowLayout());
        add(label);
        add(input);
        add(button);
        pack();
        setVisible(true);
        button.addActionListener(this::actionPerformed);
        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buttonClicked();
                }
            }
        });
    }

    public void buttonClicked() {
    }

    private void actionPerformed(ActionEvent e) {
        buttonClicked();
    }
}
