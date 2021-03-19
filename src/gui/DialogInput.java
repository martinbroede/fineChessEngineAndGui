package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogInput extends JDialog {

    public JTextField input;

    public DialogInput(String title, String message, String defaultInput, String buttonText, Point location) {
        setLocation(location);
        setTitle(title);
        JLabel label = new JLabel(" " + message + " ");
        input = new JTextField(defaultInput);
        JButton button = new JButton(" "+ buttonText + " ");
        setLayout(new FlowLayout());
        add(label);
        add(input);
        add(button);
        pack();
        setVisible(true);
        button.addActionListener(this::actionPerformed);
    }

    public void buttonKlicked() {
    }

    private void actionPerformed(ActionEvent e) {
        buttonKlicked();
    }
}
