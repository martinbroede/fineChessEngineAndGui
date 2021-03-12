package chessNetwork;

import com.sun.scenario.effect.impl.state.LinearConvolveKernel;
import core.Move;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ChessServer extends Thread {

    ServerSocket serverSocket;
    private int port = 3777;
    private int delayMilliSec = 150;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ReceivingThread receivingThread;
    private SendingThread sendingThread;
    private Queue<Move> moveQueue;

    public ChessServer(int delayMilliSec, Queue<Move> moveQueue) {
        this.moveQueue = moveQueue;
        System.out.println("SERVER CREATED. SERVER IS LAZY.");
        this.delayMilliSec = delayMilliSec;
    }

    public static void main(String[] ar) {
        ChessServer server = new ChessServer(1000, new LinkedList<Move>());

        server.start();

        try { // need some time to connect
            sleep(5000);
        } catch (Exception ex) {
        }
        server.send("SERVER SAYS HELLO TO THE WORLD!");
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void send(String message) {
        if (sendingThread != null) sendingThread.prepareToSend(message);
        else {
            System.err.println("SERVER NOT READY TO SEND: " + message);
        }
    }

    @Override
    public void run() {
        System.out.println("SERVER STARTED. BUSY BUSY...");
        tryToHost();
    }

    public boolean prepareServerSocket(int portToUse) {
        try {
            serverSocket = new ServerSocket(portToUse);
            System.out.println("SERVER SOCKET PROVIDED. LOCAL SOCKET ADRESS: " + serverSocket.getLocalSocketAddress());
            socket = serverSocket.accept();
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("SERVER WELCOMES CLIENT");
            bufferedWriter.newLine();
            sendingThread = new SendingThread(bufferedWriter, delayMilliSec);
            receivingThread = new ReceivingThread(bufferedReader, delayMilliSec,moveQueue);
            System.out.println("SERVER READY");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void killThreads() {
        if (receivingThread != null) receivingThread.interrupt();
        if (sendingThread != null) sendingThread.interrupt();
    }

    public void tryToHost() {

        if (prepareServerSocket(port)) {
            sendingThread.start();
            receivingThread.start();
        }

        if ((sendingThread != null) || (receivingThread != null)) {
            while (sendingThread.isAlive() && receivingThread.isAlive()) {
                try {
                    sleep(delayMilliSec);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            sendingThread.interrupt(); //close both if one thread is dead.
            receivingThread.interrupt();
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