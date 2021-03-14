package chessNetwork;

import javax.swing.*;
import java.awt.*;

public class IpAndPortDialog {

    private JTextField ipField;
    private JTextField portField;
    public JButton okButton;
    public JDialog dialog = new JDialog();

    public IpAndPortDialog(Point location) {
        dialog.setLocation(location);
        setUp();
    }

    public static void main(String[] args) {
        new IpAndPortDialog();
    }

    public String getIp(){
        String ip = ipField.getText();
        return ip.replace(" ", "");
    }

    public String getPort(){
        String port = portField.getText();
        return port.replace(" ", "");
    }

    public IpAndPortDialog() {
        setUp();
    }

    private void setUp() {
        dialog.setTitle("IP Konfiguration");
        dialog.setSize(370, 100);
        dialog.setLayout(new FlowLayout());
        JLabel ipLabel = new JLabel(" ip - Adresse: ");
        ipField = new JTextField("2003:00c8:9706:2700:d933:XXXX:c402:2f3c");
        /*ipField = new JTextField("0.0.0.0                                        ");*/
        JLabel portLabel = new JLabel(" Port: ");
        portField = new JTextField(" 50005 ");
        okButton = new JButton("          OK          ");
        dialog.add(ipLabel);
        dialog.add(ipField);
        dialog.add(portLabel);
        dialog.add(portField);
        dialog.add(okButton);
        dialog.setVisible(true);
    }
}
