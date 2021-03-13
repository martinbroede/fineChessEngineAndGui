package chessNetwork;

import core.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.Queue;

class ReceivingThread extends Thread {

    BufferedReader bReader;
    Queue<Move> moveQueue;
    private int delayMilliSec = 150;

    public ReceivingThread(BufferedReader bReader, int delayMilliSec, Queue<Move> moveQueue, String threadName) {
        setName(threadName);
        this.bReader = bReader;
        this.delayMilliSec = delayMilliSec;
        this.moveQueue = moveQueue;
    }

    public void run() {

        String message = "";

        while (!message.equals("ciao")) {
            try {
                message = bReader.readLine();
                if(message == null) break;
                String[] args = message.split(" ");
                if (args.length >= 2) {
                    System.out.println(getName() + ":"); //todo remove
                    if (args[0].equals("MOVE")) {
                        moveQueue.add(new Move(Short.parseShort(args[1])));
                        System.out.println("RECEIVED MOVE:" + Short.parseShort(args[1])); //todo remove
                    }else{
                        System.out.println("CHAT: " + message); //show chat messages
                    }
                }


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