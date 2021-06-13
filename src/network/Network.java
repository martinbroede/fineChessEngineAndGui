package network;

import gui.dialogs.DialogIpAddr;

import java.awt.*;
import java.net.InetAddress;
import java.util.LinkedList;

import static java.lang.Thread.sleep;
import static misc.Properties.resourceBundle;
import static misc.StaticSetting.getSetting;
import static misc.StaticSetting.rememberSetting;

public abstract class Network {

    private NetInstance instance;

    public static String resolveIpFromString(String input) {

        try {
            String[] args = input.split("/");
            InetAddress inetAddress = InetAddress.getByName(args[0]);
            String ip = inetAddress.getHostAddress();
            return String.format("%s/%s", ip, args[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public NetInstance getInstance() {
        return instance;
    }

    public LinkedList<String> getMessageQueue() {

        assert instance != null;
        return instance.getMessageQueue();
    }

    public void createServer(String configIpAndPort) {

        if (instance != null) disconnect();

        instance = new ChessServer(configIpAndPort) {
            @Override
            void showMessage(String msg) {
                Network.this.showMessage(msg);
            }

            @Override
            void notifyOnlineToOffline() {
                Network.this.notifyOnlineToOffline();
            }
        };

        Thread server = new Thread(instance);
        server.start();
    }

    abstract public void notifyOnlineToOffline();

    abstract public void showMessage(String msg);

    public void createClient(String configIpAndPort) {

        if (instance != null) {
            disconnect();
            try {
                sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        instance = new ChessClient(configIpAndPort) {
            @Override
            void showMessage(String msg) {
                Network.this.showMessage(msg);
            }

            @Override
            void notifyOnlineToOffline() {
                Network.this.notifyOnlineToOffline();
            }
        };

        Thread client = new Thread(instance);
        client.start();
    }

    public boolean isConnected() {

        if (instance != null) return instance.isConnectionSuccessful();
        return false;
    }

    public void send(String message) {

        if (instance != null) {
            instance.send(message);
        } else System.out.printf("NETWORK IS NOT ACTIVE - DID NOT SEND %s%n", message);
    }

    public void disconnect() {

        if (instance != null) {
            send("%SERVER DISCONNECT");
            instance.abort();
        }
        instance = null;
        System.out.println("DISCONNECTED SERVER/CLIENT\nTHREADS INTERRUPTED");
    }

    public void showServerIpDialog(Point location) {
        new ServerDialogIpAddr(location);
    }

    public void showClientIpDialog(Point location) {
        new ClientDialogIpAddr(location);
    }


    class ServerDialogIpAddr extends DialogIpAddr {

        public ServerDialogIpAddr(Point location) {

            super(location);
            String preferredIP = getSetting("%SERVER_IP");
            if (!preferredIP.equals("")) {
                ipField.setText(preferredIP);
                portField.setText(getSetting("%SERVER_PORT"));
            }
            dialog.setTitle(resourceBundle.getString("ip.config.server"));
        }

        @Override
        public void action() {

            rememberSetting(String.format("%%SERVER_IP %s", getIp()));
            rememberSetting(String.format("%%SERVER_PORT %s", getPort()));
            dialog.dispose();

            String config = String.format("%s/%s", getIp(), getPort());
            config = resolveIpFromString(config);
            createServer(config);
        }
    }

    class ClientDialogIpAddr extends DialogIpAddr {

        public ClientDialogIpAddr(Point location) {

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

            rememberSetting(String.format("%%CLIENT_IP %s", getIp()));
            rememberSetting(String.format("%%CLIENT_PORT %s", getPort()));
            dialog.dispose();
            String config = String.format("%s/%s", getIp(), getPort());
            config = resolveIpFromString(config);
            createClient(config);
        }
    }
}
