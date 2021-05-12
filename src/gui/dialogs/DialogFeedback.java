package gui.dialogs;

import jdk.nashorn.internal.scripts.JD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class DialogFeedback extends JDialog {
    public JTextArea textArea = new JTextArea();
    JButton sendButton = new JButton(" Feedback senden ");

    {
        setLayout(new FlowLayout());
        add(textArea);
        add(sendButton);
        textArea.setBackground(new Color(238, 242, 246));
        textArea.setPreferredSize(new Dimension(300,300));
        textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonClicked();
            }
        });
        setVisible(true);
    }

    abstract public void buttonClicked();

    public DialogFeedback(String text, Point location) {

        setLocation(location);
        textArea.setText(text);
        pack();
    }

    public static void main(String[] args) {
        new DialogFeedback("hi",new Point(100,100)){
          public void buttonClicked(){
              System.out.println(textArea.getText());
              dispose();
          }
        };
    }
}
