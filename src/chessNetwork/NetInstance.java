package chessNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public abstract class NetInstance implements Runnable {

    protected String ip;
    protected int port;
    protected Socket socket;
    protected Receiver receiver;
    protected Sender sender;
    protected boolean connectionSuccessful;
    protected LinkedList<String> messageQueue;
    protected BufferedReader bufferedReader;

    public Receiver getReceiver() {
        return receiver;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public LinkedList<String> getMessageQueue() {
        return messageQueue;
    }

    public boolean isConnectionSuccessful() {
        return connectionSuccessful;
    }

    public void send(String message) {
        sender.bufferedSend(message);
    }

    abstract public void run();

    public void abort() {

        try {
            receiver.unregister();
            receiver.interrupt();
        } catch (NullPointerException ignored) {
        }

        receiver = null;
        connectionSuccessful = false;

        try {
            socket.close();
            System.out.println("CLOSED SOCKET");
        } catch (IOException | NullPointerException ignored) {
        }
    }
}
