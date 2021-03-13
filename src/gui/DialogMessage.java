package gui;

import javax.swing.*;
import java.awt.*;

public class DialogMessage{
    public DialogMessage(String message){
        JDialog dialog = new JDialog();
        dialog.setTitle("INFO");
        JLabel label  = new JLabel(" "+message + " ");
        JButton ok = new JButton(" OK ");
        dialog.setLayout(new FlowLayout());
        dialog.add(label);
        dialog.add(ok);
        dialog.pack();
        dialog.setLocation(new Point(400,200));
        dialog.setVisible(true);
        ok.addActionListener(e -> dialog.dispose());
    }

    public static void main(String[] args) {
        DialogMessage dialogMessage = new DialogMessage("Verbinden erfolgreich");
    }
}