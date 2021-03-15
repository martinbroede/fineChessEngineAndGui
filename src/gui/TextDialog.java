package gui;

import javax.swing.*;
import java.awt.*;

public class TextDialog extends JDialog {
    public TextDialog(String text){
        JTextArea textArea = new JTextArea();
        textArea.setText(text);
        add(textArea);
        pack();
        textArea.setEnabled(false);
        textArea.setBackground(new Color(238, 242, 246));
        textArea.setDisabledTextColor(Color.darkGray);
    }
}
