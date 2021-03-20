package chessNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class IpAndPortDialog {

    public JTextField ipField;
    public JTextField portField;
    public JButton okButton;
    public JDialog dialog = new JDialog();
    {
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
        okButton.addActionListener(e -> {
            okAction();
        });

        ipField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okAction();
                }
            }
        });

        portField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okAction();
                }
            }
        });
    }

    public IpAndPortDialog(Point location) {
        dialog.setLocation(location);
    }

    public IpAndPortDialog() {
    }

    public static void main(String[] args) {
        new IpAndPortDialog();
    }

    public String getIp() {
        String ip = ipField.getText();
        return ip.replace(" ", "");
    }

    public String getPort() {
        String port = portField.getText();
        return port.replace(" ", "");
    }

    /** will be performed after a you klick on OK button or you hit enter*/
    public void okAction() {
    }
}
