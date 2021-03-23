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
        setVisible(true);
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
        new DialogText("other\nPosition", new Point(100,100));
    }
}
