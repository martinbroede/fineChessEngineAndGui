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

    public ReceivingThread(BufferedReader bReader, int delayMilliSec, Queue<Move> moveQueue) {
        this.bReader = bReader;
        this.delayMilliSec = delayMilliSec;
        this.moveQueue = moveQueue;
    }

    public void run() {

        String message = "";

        while ((message != null) && (!message.equals("ciao"))) {
            try {
                message = bReader.readLine();

                System.out.println(message);

                String[] args = message.split(" ");
                if (args.length >= 2) {
                    if (args[0].equals("MOVE")) {

                        moveQueue.add(new Move(Short.parseShort(args[1])));
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