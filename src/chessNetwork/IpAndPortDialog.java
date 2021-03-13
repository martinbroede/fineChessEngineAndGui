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

    public IpAndPortDialog() {
        setUp();
    }

    private void setUp(){
        dialog.setTitle("IP Konfiguration");
        dialog.setSize(200, 130);
        dialog.setLayout(new FlowLayout());
        JLabel ipLabel = new JLabel(" ip - Adresse: ");
        ipField = new JTextField("192.168.178.27");
        JLabel portLabel = new JLabel("          Port: ");
        portField = new JTextField("3777");
        okButton = new JButton("        OK        ");
        dialog.add(ipLabel);
        dialog.add(ipField);
        dialog.add(portLabel);
        dialog.add(portField);
        dialog.add(okButton);
        dialog.setVisible(true);
    }
}
