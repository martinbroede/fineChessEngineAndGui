package chessNetwork;

import core.Move;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class ChessClient extends Thread {

    private String ip = "0.0.0.0";
    private int port = 3777;
    private int delayMilliSec = 150;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;
    Queue<Move> moveQueue;

    public ChessClient(int delayMilliSec,  Queue<Move> moveQueue) {
        System.out.println("CLIENT CREATED. CLIENT IS LAZY.");
        this.delayMilliSec = delayMilliSec;
        this.moveQueue = moveQueue;
    }

    public static void main(String[] ar) {
        ChessClient client = new ChessClient(150, new LinkedList<Move>());

        client.send("hello my friend"); //will cause error message

        client.start();

        try { // need some time to connect
            sleep(client.delayMilliSec);
        } catch (Exception ex) {
        }

        client.send("hello server!"); // will work if connection successful
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
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
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendingThread = new SendingThread(bufferedWriter, delayMilliSec);
            receivingThread = new ReceivingThread(bufferedReader, delayMilliSec, moveQueue);
            System.out.println("CONNECTED TO SERVER");
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
    }

    public void tryToConnect() {

        if (prepareSocket(ip, port)) {
            sendingThread.start();
            receivingThread.start();
        }

        if ((sendingThread != null) || (receivingThread != null)) {
            while (sendingThread.isAlive() && receivingThread.isAlive()) {
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
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