package gui.dialogs;

import chessNetwork.Password;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class DialogNameAndPassword extends JDialog {

    public JTextField nameIn = new JTextField("ohneNamen");
    public JTextField pwIn = new JTextField("tollesPW123");

    public DialogNameAndPassword(Point location) {
        setLocation(location);
        setTitle("Namen und Passwort wählen");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JLabel lName = new JLabel(" Name ");
        JLabel lPassword = new JLabel(" Passwort ");
        JButton button = new JButton(" OK ");
        setLayout(new FlowLayout());
        add(lName);
        add(nameIn);
        add(lPassword);
        add(pwIn);
        add(button);
        pack();
        setVisible(true);
        button.addActionListener(this::actionPerformed);
        pwIn.addKeyListener(new KeyListener() {
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

    public static void main(String[] args) {
        new DialogNameAndPassword(new Point(0, 0)) {
            @Override
            public void buttonClicked() {
                System.out.println(nameIn.getText() + "\n"
                        + Password.toSHA256String(nameIn.getText() + pwIn.getText()));
            }
        };
    }

    public abstract void buttonClicked();

    private void actionPerformed(ActionEvent e) {
        buttonClicked();
    }
}