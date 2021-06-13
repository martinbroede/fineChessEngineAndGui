package gui.dialogs;

import gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DialogTextArea extends JDialog {

    public DialogTextArea(String text, Point location) {

        setLocation(location);
        setAndPack(text);
    }

    public DialogTextArea(String text) {

        setLocationRelativeTo(null);
        setAndPack(text);
    }

    private void setAndPack(String text) {

        JTextArea textArea = new JTextArea();

        textArea.setEnabled(false);
        textArea.setBackground(new Color(238, 242, 246));
        textArea.setDisabledTextColor(Color.darkGray);
        textArea.setFont(new Font(MainWindow.COURIER_NEW, Font.PLAIN, 14));
        textArea.setText(text);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClick();
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane);

        int x = Math.min(textArea.getPreferredSize().width * 12 / 10, 600);
        int y = Math.min(textArea.getPreferredSize().height * 13 / 10, 600);
        setSize(new Dimension(x, y));

        setVisible(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void onMouseClick() {
    }
}
