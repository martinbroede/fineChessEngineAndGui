package chessNetwork;

import gui.Window;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public abstract class NetInstance implements Runnable {

    public static final String MESSAGE_DELIMITER = "\u0003";
    protected String ip;
    protected int port;
    protected Socket socket;
    protected Receiver receiver;
    protected Sender sender;
    protected boolean connectionSuccessful;
    protected LinkedList<String> messageQueue;
    protected Scanner scanner;

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
            receiver.unsubscribe();
            System.out.println("unsubscribe"); //todo...
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

        Window.changeTitle("-ONLINE-", "-OFFLINE-");
    }
}
