package chessNetwork;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Receiver extends Thread {

    private final LinkedList<String> messageQueue;
    private final NetInstance initiator;
    private final int SOCK_BUFFER_SIZE = 256;
    Scanner scanner;
    private Subscriber subscriber;

    public Receiver(String serviceName, NetInstance initiator) {

        setName(serviceName);
        this.initiator = initiator;
        this.messageQueue = initiator.getMessageQueue();
        this.scanner = initiator.getScanner();
        this.scanner.useDelimiter(NetInstance.MESSAGE_DELIMITER);
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

        while (true) {

            try{
            message = scanner.next();}
            catch(NoSuchElementException ex){
                System.out.println("RECEIVER ABORTED");
                break;
            }

            messageQueue.add(message);
            if (subscriber != null) subscriber.react();
        }
        initiator.abort();
    }
}