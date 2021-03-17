package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import static java.lang.Thread.sleep;

public class DialogMessage extends JDialog {

    public DialogMessage(String message){
        setUp(message);
    }

    public DialogMessage(String message, Point location){
        setUp(message);
        this.setLocation(location);
    }

    public void setUp(String message) {
        setTitle("INFO");
        JLabel label = new JLabel(" " + message + " ");
        JButton ok = new JButton(" OK ");
        setLayout(new FlowLayout());
        add(label);
        add(ok);
        pack();
        setLocation(new Point(400, 200));
        setVisible(true);
        ok.addActionListener(e -> this.dispose());
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        DialogMessage dialogMessage = new DialogMessage("Verbinden erfolgreich");
    }
}