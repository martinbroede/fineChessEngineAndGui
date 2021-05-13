package chessNetwork;

import gui.dialogs.DialogMessage;
import gui.dialogs.IpAndPortDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TestReachability {

    public TestReachability() {
        new ReachableDialog();
    }

    public static void main(String[] args) {
        new TestReachability();
    }

    private void reachable(String ip, int port) {

        boolean reached = false;
        try {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ip, port), 2000);
            }
            reached = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (reached) {
            new DialogMessage(ip + " ist erreichbar");
        } else new DialogMessage(ip + " ist NICHT erreichbar.");

    }

    class ReachableDialog extends IpAndPortDialog {

        public void action() {

            reachable(getIp(), Integer.parseInt(getPort()));
            dialog.dispose();
        }
    }
}