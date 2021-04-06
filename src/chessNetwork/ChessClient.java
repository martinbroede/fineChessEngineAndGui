package chessNetwork;

import gui.dialogs.DialogMessage;

import java.io.*;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class ChessClient extends NetInstance{

    public ChessClient(String configIpAndPort) {

        System.out.println("CREATE CLIENT CONNECTING TO " + configIpAndPort);
        String[] args = configIpAndPort.split("/");
        if (args.length != 2) {
            System.err.println("WRONG NUMBER OF ARGUMENTS FOR CLIENT");
        } else {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        this.messageQueue = new LinkedList<>();
        sender = new Sender("CLIENT SENDER");
    }

    public boolean provideSocket(String ip, int port) {

        try {
            socket = new Socket(ip, port);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sender.setWriter(bufferedWriter);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            receiver = new Receiver("CLIENT RECEIVER", this);
            System.out.println("CONNECTED TO SERVER " + socket.getRemoteSocketAddress());
            new DialogMessage("Erfolgreich mit Server " + socket.getRemoteSocketAddress() + " verbunden.");
            send(""); //to send messages in queue
            connectionSuccessful = true;
            return true;
        } catch (UnknownHostException ex) {
            System.out.println(ex.getMessage());
            new DialogMessage("Verbindung fehlgeschlagen. Host unbekannt.");
            return false;
        } catch (NoRouteToHostException ex) {
            System.out.println(ex.getMessage());
            new DialogMessage("Verbindung fehlgeschlagen. Vermutlich besteht keine Internetverbindung.");
            return false;
        } catch (ConnectException ex) {
            System.out.println(ex.getMessage());
            new DialogMessage("Verbindung fehlgeschlagen.");
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void connect() {

        if (provideSocket(ip, port)) {
            receiver.start();
        }
    }

    @Override
    public void run(){
        connect();
    }

    @Override
    public void abort() {

        super.abort();
        System.out.println("CLIENT ABORTED");
    }
}