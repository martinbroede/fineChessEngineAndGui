package network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static misc.Properties.resourceBundle;

public abstract class ChessServer extends NetInstance {

    private InetAddress inetAddress;
    private int port;
    private ServerSocket serverSocket;

    public ChessServer(String configIpAndPort) {

        System.out.println("CREATE SERVER LISTENING ON " + configIpAndPort);
        String[] args = configIpAndPort.split("/");
        String ipString = "";
        if (args.length != 2) {
            System.out.println("WRONG NUMBER OF ARGUMENTS FOR SERVER");
        } else {
            ipString = args[0];
            port = Integer.parseInt(args[1]);
        }
        try {
            inetAddress = InetAddress.getByName(ipString);
        } catch (UnknownHostException ex) {
            showMessage(ex.toString());
            ex.printStackTrace();
        }
    }

    public boolean provideServerSocket() {

        try {
            serverSocket = new ServerSocket(port, 2, inetAddress);
            System.out.println("SERVER SOCKET PROVIDED. ADDRESS: " + serverSocket.getLocalSocketAddress());
            socket = serverSocket.accept();
            System.out.println("CONNECTED WITH " + socket.getRemoteSocketAddress());
            showMessage(String.format("Server - Verbinden mit %s erfolgreich", socket.getRemoteSocketAddress()));
            scanner = new Scanner(socket.getInputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sender.setWriter(bufferedWriter);
            receiver = new Receiver("SERVER RECEIVER", this);
            System.out.println("SERVER READY");

            send(""); //to send messages in queue
            serverSocket.close();
            connectionSuccessful = true;
            return true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            showMessage(resourceBundle.getString("connect.failed"));
            return false;
        }
    }

    public void host() {

        if (provideServerSocket()) {
            receiver.start();
        }
    }

    abstract void showMessage(String msg);

    abstract void notifyOnlineToOffline();

    @Override
    public void run() {
        host();
    }

    @Override
    public void abort() {

        super.abort();

        try {
            serverSocket.close();
            System.out.println("CLOSED SERVER SOCKET");
        } catch (IOException | NullPointerException ignored) {
        }
        System.out.println("SERVER ABORTED");
    }
}