package chessNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

public class Receiver extends Thread {

    private final LinkedList<String> messageQueue;
    private final NetInstance initiator;
    private Subscriber subscriber;
    BufferedReader bufferedReader;

    public Receiver(String serviceName, NetInstance initiator) {

        setName(serviceName);
        this.initiator = initiator;
        this.messageQueue = initiator.getMessageQueue();
        bufferedReader = initiator.getBufferedReader();
    }

    public void register(Subscriber subscriber) {

        this.subscriber = subscriber;
        System.out.println(getName() + " GOT SUBSCRIBER");
    }

    public void unsubscribe() {

        if (subscriber != null) subscriber.unsubscribe();
        System.out.println(getName() + " SIGNED OFF SUBSCRIBER");
    }

    public void run() {

        String message = "";

        while (!message.equals("%CIAO")) {

            try {
                message = bufferedReader.readLine();
                if (message == null) break;
                else if (!message.equals("")) {
                    message = message.replace("\n","").replace("\r","");
                    //replace because newline characters may be distinct depending on OS
                    messageQueue.add(message);
                    if (subscriber != null) subscriber.react();
                }
            } catch (SocketException ex) {
                System.out.println("SOCKET EXCEPTION\nRECEIVER ABORTED");
                ex.printStackTrace();
                break;
            } catch (IOException ex) {
                System.out.println("I/O EXCEPTION");
                ex.printStackTrace();
            }
        }
        initiator.abort();
    }
}