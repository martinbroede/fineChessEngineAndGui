package chessNetwork;

import gui.dialogs.DialogMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

public class ChessServer extends NetInstance implements Runnable {

    private InetAddress inetAddress;
    private int port;
    private ServerSocket serverSocket;

    public ChessServer(String configIpAndPort) {

        System.out.println("CREATE SERVER LISTENING ON " + configIpAndPort);
        String[] args = configIpAndPort.split("/");
        String ipString = "";
        if (args.length != 2) {
            System.err.println("WRONG NUMBER OF ARGUMENTS FOR SERVER");
        } else {
            ipString = args[0];
            port = Integer.parseInt(args[1]);
        }
        try {
            inetAddress = InetAddress.getByName(ipString);
        } catch (UnknownHostException ex) {
            new DialogMessage("\"Spiel erstellen\" fehlgeschlagen. Host unbekannt ");
            ex.printStackTrace();
        }
        this.messageQueue = new LinkedList<>();
        sender = new Sender("SERVER SENDER");
    }

    public boolean provideServerSocket() {

        try {
            serverSocket = new ServerSocket(port, 2, inetAddress);
            System.out.println("SERVER SOCKET PROVIDED. ADDRESS: " + serverSocket.getLocalSocketAddress());
            socket = serverSocket.accept();
            System.out.println("CONNECTED WITH " + socket.getRemoteSocketAddress());
            new DialogMessage("Server - Verbinden mit " + socket.getRemoteSocketAddress() + " erfolgreich");
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
            new DialogMessage("Verbindung fehlgeschlagen ");
            return false;
        }
    }

    public void host() {

        if (provideServerSocket()) {
            receiver.start();
        }
    }

    @Override
    public void run(){
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
        serverSocket = null;
        System.out.println("SERVER ABORTED");
    }
}