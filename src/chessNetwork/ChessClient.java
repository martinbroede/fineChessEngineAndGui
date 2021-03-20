package chessNetwork;

import gui.dialogs.DialogMessage;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class ChessClient extends Thread {

    private final int delayMilliSec;
    LinkedList<String> messageQueue;
    private String ip = "0.0.0.0";
    private int port = 3777;
    private Socket socket;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;
    private boolean connectionSuccessful;

    public ChessClient(String configIpAndPort, int delayMilliSec, LinkedList<String> messageQueue) {

        setName("CLIENT FOR " + configIpAndPort);

        System.out.println("NEW CLIENT FOR " + configIpAndPort + " CLIENT NOT STARTED.");
        String[] args = configIpAndPort.split("/");
        if (args.length != 2) {
            System.err.println("WRONG NUMBER OF ARGUMENTS FOR CLIENT");
        } else {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        this.delayMilliSec = delayMilliSec;
        this.messageQueue = messageQueue;
        sendingThread = new SendingThread(delayMilliSec, "CLIENT SENDER");
    }

    public static void main(String[] ar) {

        ChessClient client = new ChessClient("0.0.0.0/50005", 150, new LinkedList<>());
        client.start();
        client.send("Tom");
        client.send("Hello World!");
        client.send("What's up!?");
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }

    @Override
    public void run() {
        System.out.println("CLIENT STARTED. BUSY BUSY...");
        tryToConnect();
    }

    public void send(String message) {
        sendingThread.prepareToSend(message);
    }

    public boolean prepareSocket(String ip, int port) {

        try {
            socket = new Socket(ip, port);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendingThread.setWriter(bufferedWriter);
            receivingThread = new ReceivingThread(bufferedReader, delayMilliSec, messageQueue, "CLIENT RECEIVER");
            System.out.println("CONNECTED TO SERVER " + socket.getRemoteSocketAddress());
            new DialogMessage("Erfolgreich mit Server " + socket.getRemoteSocketAddress() + " verbunden.");
            send(""); //to send messages in queue
            connectionSuccessful = true;
            return true;
        } catch (UnknownHostException ex) {
            System.err.println("DON'T KNOW HO(R)ST: TRY AGAIN.");
            return false;
        } catch (ConnectException ex) {
            System.err.println("CONNECTION FAILED. TRY AGAIN");
            return false;
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
    }

    public void tryToConnect() {

        if (prepareSocket(ip, port)) {
            sendingThread.start();
            receivingThread.start();
        }

        if ((sendingThread != null) || (receivingThread != null)) {
            try {
                while (sendingThread.isAlive() && receivingThread.isAlive()) {
                    sleep(1000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            try {
                sendingThread.interrupt();
                receivingThread.interrupt();
            } catch (NullPointerException ex) {
                System.err.println("SENDER/RECEIVER DEAD");
            }
        }

        connectionSuccessful = false;

        if (socket != null) {
            try {
                socket.close();
                System.out.println("CLIENT CLOSED SOCKET.");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
        System.out.println("CLIENT SAYS GOODBYE");
    }
}