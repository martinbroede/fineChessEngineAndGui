package chessNetwork;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

class SendingThread extends Thread {

    private Queue<String> sendQueue;
    private int delayMilliSec;
    BufferedWriter bWriter;

    public SendingThread(BufferedWriter bWriter, int delayMilliSec) {
        this.sendQueue = new LinkedList<String>();
        this.bWriter = bWriter;
        this.delayMilliSec = delayMilliSec;
    }

    public void prepareToSend(String message){
        sendQueue.add(message);
    }

    public void run() {
        String message = "";

        while (!message.equals("ciao")) {
            try {
                while(sendQueue.size() > 0){
                    message = sendQueue.poll();
                    bWriter.write(message);
                    bWriter.newLine();
                    bWriter.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                sleep(delayMilliSec);
            } catch (InterruptedException ex) {
                System.out.println("SENDER INTERRUPTED");
                break;
            }
        }
        System.out.println("SENDER SAYS GOODBYE");
    }
}
