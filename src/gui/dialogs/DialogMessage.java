package gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
        ok.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    dispose();
                }
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