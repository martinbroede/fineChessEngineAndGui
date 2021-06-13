package network;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public abstract class NetInstance implements Runnable {

    public static final String MESSAGE_DELIMITER = "\u0003";
    protected final Sender sender;
    protected final LinkedList<String> messageQueue;
    protected String ip;
    protected int port;
    protected Socket socket;
    protected Receiver receiver;
    protected boolean connectionSuccessful;
    protected Scanner scanner;

    public NetInstance() {
        messageQueue = new LinkedList<>();
        sender = new Sender();
    }

    public void setAddr(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public Scanner getScanner() {
        return scanner;
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
            receiver.notify_subscriber();
            receiver.interrupt();
        } catch (NullPointerException ignored) {
        }

        connectionSuccessful = false;

        try {
            socket.close();
            System.out.println("CLOSED SOCKET");
        } catch (IOException | NullPointerException ignored) {
        }

        notifyOnlineToOffline();
    }

    abstract void notifyOnlineToOffline();
}
