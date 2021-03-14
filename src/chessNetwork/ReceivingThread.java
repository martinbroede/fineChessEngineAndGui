package chessNetwork;

import core.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

class ReceivingThread extends Thread {

    BufferedReader bReader;
    LinkedList<String> messageQueue;
    private int delayMilliSec = 150;

    public ReceivingThread(BufferedReader bReader, int delayMilliSec,LinkedList<String> messageQueue, String threadName) {
        setName(threadName);
        this.bReader = bReader;
        this.delayMilliSec = delayMilliSec;
        this.messageQueue = messageQueue;
    }

    public void run() {

        String message = "";

        while (!message.equals("ciao")) {
            try {

                message = bReader.readLine();
                if(message == null) break;
                else if(!message.equals("")) messageQueue.add(message);

                try {
                    sleep(delayMilliSec);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            } catch (SocketException ex) {
                System.err.println("SOCKET EXCEPTION. ABORT.");
                break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("RECIEVER SAYS GOODBYE");
    }
}