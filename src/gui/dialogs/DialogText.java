package gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class DialogText extends JDialog {

    JTextArea textArea = new JTextArea();

    {
        add(textArea);
        textArea.setEnabled(false);
        textArea.setBackground(new Color(238, 242, 246));
        textArea.setDisabledTextColor(Color.darkGray);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        setVisible(true);
    }

    public DialogText(String text, Point location) {

        setLocation(location);
        textArea.setText(text);
        pack();
    }

    public DialogText(String text) {

        textArea.setText(text);
        pack();
    }

    public static void main(String[] args) {

        new DialogText("Default\nPosition");
        new DialogText("other\nPosition", new Point(100, 100));
    }
}
