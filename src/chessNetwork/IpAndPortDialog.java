package chessNetwork;

import javax.swing.*;
import java.awt.*;

public class IpAndPortDialog {

    public JTextField ipField;
    public JTextField portField;
    public JButton okButton;
    public JDialog dialog = new JDialog();

    public IpAndPortDialog(Point location) {
        dialog.setLocation(location);
        setUp();
    }

    public static void main(String[] args) {
        new IpAndPortDialog();
    }

    public IpAndPortDialog() {
        setUp();
    }

    private void setUp() {
        dialog.setTitle("IP Konfiguration");
        dialog.setSize(370, 100);
        dialog.setLayout(new FlowLayout());
        JLabel ipLabel = new JLabel(" ip - Adresse: ");
        ipField = new JTextField("2003:00c8:9706:2700:d933:17e8:c402:XXXX");
        JLabel portLabel = new JLabel(" Port: ");
        portField = new JTextField("50005");
        okButton = new JButton("          OK          ");
        dialog.add(ipLabel);
        dialog.add(ipField);
        dialog.add(portLabel);
        dialog.add(portField);
        dialog.add(okButton);
        dialog.setVisible(true);
    }
}
