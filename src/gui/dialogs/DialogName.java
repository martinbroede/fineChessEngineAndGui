package gui.dialogs;

import javax.swing.*;

import static misc.Properties.resourceBundle;

public class DialogName {

    public static final int NAME = 0;
    public static final int PASSWORD = 1;

    private DialogName() {
    }

    public static String[] getNameAndPassword(JFrame frame) {
        JTextField nameField = new JTextField(10);
        JTextField pwField = new JTextField(10);
        JPanel namePanel = new JPanel();
        namePanel.add(new JLabel(resourceBundle.getString("name")));
        namePanel.add(nameField);
        namePanel.add(new JLabel(resourceBundle.getString("passwort")));
        namePanel.add(pwField);
        int result = JOptionPane.showConfirmDialog(frame, namePanel,
                resourceBundle.getString("choose.name.pw"), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            return new String[]{nameField.getText(), pwField.getText()};
        } else {
            return new String[]{"", ""};
        }
    }
}