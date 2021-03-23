package chessNetwork;

import gui.dialogs.IpAndPortDialog;

import java.awt.*;
import java.util.LinkedList;

public class Network {

    public final LinkedList<String> messageQueue = new LinkedList<>();
    private final int DELAY_MILLISEC = 10;
    private ChessServer server;
    private ChessClient client;
    private boolean active;

    public static void main(String[] args) {
        Network net = new Network();
        net.showClientIpDialog(new Point(100, 100));
    }

    public void createServer(String configIpAndPort) {

        if ((server != null) | (client != null)) safeDeleteServerOrClient();
        server = new ChessServer(configIpAndPort, DELAY_MILLISEC, messageQueue);
        server.start();
        active = true;
    }

    public boolean isConnected() {

        if (server != null) return server.isConnectionSuccessful();
        if (client != null) return client.isConnectionSuccessful();
        return false;
    }

    public void createClient(String configIpAndPort) {

        if ((server != null) | (client != null)) safeDeleteServerOrClient();
        client = new ChessClient(configIpAndPort, DELAY_MILLISEC, messageQueue);
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
        } else System.out.println("NETWORK IS NOT ACTIVE - DID NOT SEND " + message);
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

    public void showServerIpDialog(Point location) {
        new ServerIpDialog(location);
    }

    public void showClientIpDialog(Point location) {
        new ClientIpDialog(location);
    }

    class ServerIpDialog extends IpAndPortDialog {

        public ServerIpDialog(Point location) {

            super(location);
            dialog.setTitle("IP Konfiguration Server");
        }

        @Override
        public void okAction() {

            String config = getIp() + "/" + getPort();
            createServer(config);
            dialog.dispose();
        }
    }

    class ClientIpDialog extends IpAndPortDialog {

        public ClientIpDialog(Point location) {

            super(location);
            dialog.setTitle("IP Konfiguration Client");
        }

        @Override
        public void okAction() {

            String config = getIp() + "/" + getPort();
            createClient(config);
            dialog.dispose();
        }
    }
}