package chessNetwork;

import core.Move;
import gui.DialogMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ChessServer extends Thread {

    private final int delayMilliSec;
    private ServerSocket serverSocket;
    private InetAddress inetAddress;
    private int port;
    private Socket socket;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;
    private LinkedList<String> messageQueue;


    public ChessServer(String configIpAndPort, int delayMilliSec, LinkedList<String> messageQueue) {

        setName("SERVER FOR " + configIpAndPort);

        System.out.println("START SERVER FOR " + configIpAndPort);
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
            ex.printStackTrace();
        }

        this.messageQueue = messageQueue;
        this.delayMilliSec = delayMilliSec;
        System.out.println("SERVER CREATED. SERVER IS LAZY.");
        sendingThread = new SendingThread(delayMilliSec, "SERVER SENDER");
    }

    public static void main(String[] ar) {


        ChessServer server = new ChessServer("2003:c8:9706:2700:d933:17e8:c402:0000/50005",
                2000, new LinkedList<>());

        server.start();

        try { // need some time to connect
            sleep(5000);
        } catch (Exception ex) {
        }
        server.send("SERVER SAYS HELLO TO THE WORLD!");
    }

    public void send(String message) {
        sendingThread.prepareToSend(message);
    }

    @Override
    public void run() {
        System.out.println("SERVER STARTED. BUSY BUSY...");
        tryToHost();
    }

    public boolean prepareServerSocket() {
        try {
            serverSocket = new ServerSocket(port, 2, inetAddress);
            System.out.println("SERVER SOCKET PROVIDED. ADRESS: " + serverSocket.getLocalSocketAddress());
            socket = serverSocket.accept();
            System.out.println("CONNECTED WITH " + socket.getRemoteSocketAddress());
            new DialogMessage("Server - Verbinden mit " + socket.getRemoteSocketAddress() + " erfolgreich");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sendingThread.setWriter(bufferedWriter);
            receivingThread = new ReceivingThread(bufferedReader, delayMilliSec, messageQueue, "SERVER RECEIVER");
            System.out.println("SERVER READY");

            send(""); //to send messages in queue

            serverSocket.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void killThreads() {

        if (receivingThread != null) receivingThread.interrupt();
        if (sendingThread != null) sendingThread.interrupt();
        receivingThread = null;
        sendingThread = null;

        try {
            serverSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex){
            System.err.println("SERVER SOCKET = NULL");
        }
        serverSocket = null;
    }

    public void tryToHost() {

        if (prepareServerSocket()) {
            sendingThread.start();
            receivingThread.start();
        }

        if ((sendingThread != null) || (receivingThread != null)) {
            try {
                while (sendingThread.isAlive() && receivingThread.isAlive()) {
                    try {
                        sleep(delayMilliSec);
                    } catch (InterruptedException ex) {
                    }
                }
                sendingThread.interrupt(); //close both if one thread is dead.
                receivingThread.interrupt();
            } catch(NullPointerException ex){
                System.err.println("SENDER/REVEIVER ALREADY DEAD");
            }
        }

        if (socket != null) {
            try {
                socket.close();
                serverSocket.close();
                System.out.println("SERVER CLOSED SOCKETS");
            } catch (IOException ex) {
            }
        }
        System.out.println("SERVER SAYS GOODBYE");
    }
}