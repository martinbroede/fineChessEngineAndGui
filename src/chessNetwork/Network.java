package chessNetwork;

import core.Move;

import java.awt.*;
import java.util.LinkedList;

public class Network {

    public final LinkedList<String> messageQueue;
    private final int delayMilliSec = 100;
    private ChessServer server;
    private ChessClient client;
    private boolean active = false;

    public Network() {
        messageQueue = new LinkedList<>();
    }

    public static void main(String[] args) {
        Network net = new Network();
        net.showClientIpDialog(new Point(100, 100));
    }

    public void createServer(String configIpAndPort) {

        if ((server != null) | (client != null)) safeDeleteServerOrClient();
        server = new ChessServer(configIpAndPort, delayMilliSec, messageQueue);
        server.start();
        active = true;
    }

    public void createClient(String configIpAndPort) {

        if ((server != null) | (client != null)) safeDeleteServerOrClient();
        client = new ChessClient(configIpAndPort, delayMilliSec, messageQueue); //todo adjust delay
        client.start();
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void sendToNet(String message) {

        if (client != null) {
            client.send(message);
        } else if (server != null) {
            server.send(message);
        } else System.err.println("NETWORK IS NOT ACTIVE.");
    }

    public void safeDeleteServerOrClient() {

        if (server != null) {
            server.killThreads();
            server.interrupt();
        }
        if (client != null) {
            client.killThreads();
            client.interrupt();
        }
        server = null;
        client = null;
        System.out.println("SHUT DOWN SERVER/CLIENT. THREADS KILLED.");
        active = false;
    }

    public void startGame() {
        sendToNet("MOVE " + Move.START_GAME);
    }

    public void showServerIpDialog(Point location) {
        new ServerIpDialog(location);
    }

    public void showClientIpDialog(Point location) {
        new ClientIpDialog(location);
    }

    class ServerIpDialog extends IpAndPortDialog {
        public ServerIpDialog(Point location) {
            super(location);
            okButton.addActionListener(e -> {
                String config = ipField.getText() + "/" + portField.getText();
                createServer(config);
                dialog.dispose();
            });
        }
    }

    class ClientIpDialog extends IpAndPortDialog {
        public ClientIpDialog(Point location) {
            super(location);
            okButton.addActionListener(e -> {
                String config = ipField.getText() + "/" + portField.getText();
                createClient(config);
                dialog.dispose();
            });
        }
    }
}