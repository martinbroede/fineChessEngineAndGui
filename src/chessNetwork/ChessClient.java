package chessNetwork;

import core.Move;
import gui.DialogMessage;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ChessClient extends Thread {

    private final int delayMilliSec;
    LinkedList<String> messageQueue;
    private String ip = "0.0.0.0";
    private int port = 3777;
    private Socket socket;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;

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
    }

    public static void main(String[] ar) {

        ChessClient client = new ChessClient("192.168.178.27/3777", 150, new LinkedList<>());
        client.send("hello my friend"); //will cause error message
        client.start();
        try { // need some time to connect
            sleep(client.delayMilliSec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        client.send("hello server!"); // will work if connection successful
    }

    @Override
    public void run() {
        System.out.println("CLIENT STARTED. BUSY BUSY...");
        tryToConnect();
    }

    public void send(String message) {
        if (sendingThread != null) sendingThread.prepareToSend(message);
        else {
            System.err.println("CLIENT NOT READY TO SEND: " + message);
        }
    }

    public boolean prepareSocket(String ip, int port) {

        try {
            socket = new Socket(ip, port);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendingThread = new SendingThread(bufferedWriter, delayMilliSec,"CLIENT SENDER");
            receivingThread = new ReceivingThread(bufferedReader, delayMilliSec, messageQueue,"CLIENT RECEIVER");
            System.out.println("CONNECTED TO SERVER");
            new DialogMessage("Erfolgreich mit Server verbunden.");
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
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
            sendingThread.interrupt();
            receivingThread.interrupt();
        }

        if (socket != null) {
            try {
                socket.close();
                System.out.println("CLIENT CLOSED HIS SOCKET.");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }
        System.out.println("CLIENT SAYS GOODBYE");
    }
}