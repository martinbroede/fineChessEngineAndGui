package gui.dialogs;

import gui.MainWindow;

import javax.swing.*;
import java.awt.*;

import static misc.Properties.resourceBundle;

public abstract class DialogFeedback extends JDialog {
    public final JTextArea textArea = new JTextArea();
    final JButton sendButton = new JButton(resourceBundle.getString("send.feedback"));
    private final JScrollPane scrollPane = new JScrollPane(textArea);

    {
        setLayout(new BorderLayout());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.NORTH);
        add(sendButton, BorderLayout.SOUTH);
        textArea.setBackground(new Color(238, 242, 246));
        scrollPane.setPreferredSize(new Dimension(300, 300));
        textArea.setFont(new Font(MainWindow.COURIER_NEW, Font.PLAIN, 14));
        sendButton.addActionListener(e -> buttonClicked());
        setVisible(true);
    }

    public DialogFeedback(String text, Point location) {

        setLocation(location);
        textArea.setText(text);
        pack();
    }

    abstract public void buttonClicked();
}
