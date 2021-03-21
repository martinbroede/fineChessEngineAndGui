package gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class DialogMessage extends JDialog {
    JLabel label = new JLabel();
    {
        setTitle("INFO");
        JButton ok = new JButton(" OK ");
        setLayout(new FlowLayout());
        add(label);
        add(ok);
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

    public DialogMessage(String message) {
        label.setText(" " + message + " ");
        pack();

    }

    public DialogMessage(String message, Point location) {
        this.setLocation(location);
        label.setText(" " + message + " ");
        pack();
    }


    public static void main(String[] args) {
        DialogMessage dialogMessage = new DialogMessage("Verbinden erfolgreich");
        DialogMessage dialogMessage2 = new DialogMessage("verschieben möglich.", new Point(100,200));
    }
}