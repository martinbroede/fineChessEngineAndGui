package gui;

import javax.swing.*;
import java.awt.*;

public class DialogMessage extends JDialog {

    public DialogMessage(String message) {
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
    }

    public static void main(String[] args) {
        DialogMessage dialogMessage = new DialogMessage("Verbinden erfolgreich");
    }
}