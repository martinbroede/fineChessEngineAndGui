package gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static misc.Properties.resourceBundle;

public abstract class DialogIpAddr {

    public final JTextField ipField;
    public final JTextField portField;
    public final JButton okButton;
    public final JDialog dialog = new JDialog();

    {
        dialog.setTitle(resourceBundle.getString("ip.config"));
        dialog.setSize(300, 130);
        dialog.setLayout(new FlowLayout());
        JLabel ipLabel = new JLabel(resourceBundle.getString("ip.addr"));
        ipField = new JTextField("ABCD:0123:ABCD:0123:ABCD:0123:ABCD:0123"); //NON-NLS
        JLabel portLabel = new JLabel(resourceBundle.getString("port"));
        portField = new JTextField("54321"); //NON-NLS
        okButton = new JButton("  OK  "); //NON-NLS
        dialog.add(ipLabel);
        dialog.add(ipField);
        dialog.add(portLabel);
        dialog.add(portField);
        dialog.add(okButton);
        dialog.setVisible(true);
        okButton.addActionListener(e -> action());

        ipField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    action();
                }
            }
        });

        portField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    action();
                }
            }
        });
    }

    public DialogIpAddr(Point location) {
        dialog.setLocation(location);
    }

    public String getIp() {

        String ip = ipField.getText();
        return ip.replace(" ", "");
    }

    public String getPort() {

        String port = portField.getText();
        return port.replace(" ", "");
    }

    /** call after click or enter */
    abstract public void action();
}
