package chessNetwork;

import gui.dialogs.IpAndPortDialog;

import java.awt.*;
import java.net.InetAddress;
import java.util.LinkedList;

import static fileHandling.StaticSetting.getSetting;
import static fileHandling.StaticSetting.rememberSetting;
import static java.lang.Thread.sleep;

public class Network {

    private NetInstance instance;
    private boolean active;

    public static String resolveIpFromString(String input) {

        try {
            String[] args = input.split("/");
            InetAddress inetAddress = InetAddress.getByName(args[0]);
            String ip = inetAddress.getHostAddress();
            return ip + '/' + args[1];
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public NetInstance getInstance() {
        return instance;
    }

    public LinkedList<String> getMessageQueue() {
        return instance.getMessageQueue();
    }

    public void createServer(String configIpAndPort) {

        if (instance != null) disconnect();
        instance = new ChessServer(configIpAndPort);
        active = true;

        Thread server = new Thread(instance);
        server.start();
    }

    public void createClient(String configIpAndPort) {

        if (instance != null) {
            disconnect();
            try {
                sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        instance = new ChessClient(configIpAndPort);
        active = true;

        Thread client = new Thread(instance);
        client.start();
    }

    public boolean isConnected() {

        if (instance != null) return instance.isConnectionSuccessful();
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public void send(String message) {

        if (instance != null) {
            instance.send(message);
        } else System.out.println("NETWORK IS NOT ACTIVE - DID NOT SEND " + message);
    }

    public void disconnect() {

        if (instance != null) {
            send("%SERVER DISCONNECT");
            instance.abort();
        }
        instance = null;
        active = false;
        System.out.println("DISCONNECTED SERVER/CLIENT\nTHREADS INTERRUPTED");
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
            String preferredIP = getSetting("%SERVER_IP");
            if (!preferredIP.equals("")) {
                ipField.setText(preferredIP);
                portField.setText(getSetting("%SERVER_PORT"));
            }
            dialog.setTitle("IP Konfiguration Server");
        }

        @Override
        public void action() {

            rememberSetting("%SERVER_IP " + getIp());
            rememberSetting("%SERVER_PORT " + getPort());
            dialog.dispose();

            String config = getIp() + "/" + getPort();
            config = resolveIpFromString(config);
            createServer(config);
        }
    }

    class ClientIpDialog extends IpAndPortDialog {

        public ClientIpDialog(Point location) {

            super(location);
            String preferredIP = getSetting("%CLIENT_IP");
            if (!preferredIP.equals("")) {
                ipField.setText(preferredIP);
                portField.setText(getSetting("%CLIENT_PORT"));
            }
            dialog.setTitle("IP Konfiguration Client");
        }

        @Override
        public void action() {

            rememberSetting("%CLIENT_IP " + getIp());
            rememberSetting("%CLIENT_PORT " + getPort());
            dialog.dispose();

            String config = getIp() + "/" + getPort();
            config = resolveIpFromString(config);
            createClient(config);
        }
    }
}
