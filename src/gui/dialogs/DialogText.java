package gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DialogText extends JDialog {

    private final JTextArea textArea = new JTextArea();

    public DialogText(String text, Point location) {

        setLocation(location);
        setAndPack(text);
    }

    public DialogText(String text) {

        setAndPack(text);
    }

    public static void main(String[] args) {

        new DialogText("Default\nPosition");
        new DialogText("other\nPosition", new Point(100, 100)) {
            @Override
            public void onMouseClick() {
                System.out.println("Click!");
            }
        };
    }

    private void setAndPack(String text) {
        add(textArea);
        textArea.setEnabled(false);
        textArea.setBackground(new Color(238, 242, 246));
        textArea.setDisabledTextColor(Color.darkGray);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        textArea.setText(text);
        textArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClick();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        pack();
        setVisible(true);
    }

    public void onMouseClick() {
    }
}
