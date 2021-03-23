package chessNetwork;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

class SendingThread extends Thread {

    private final int delayMilliSec;
    LinkedList<String> sendingQueue;
    BufferedWriter bWriter;
    private boolean haveNews = false;

    public SendingThread(int delayMilliSec, String threadName) {

        setName(threadName);
        this.bWriter = bWriter;
        sendingQueue = new LinkedList<>();
        this.delayMilliSec = delayMilliSec;
    }

    public void setWriter(BufferedWriter bWriter) {
        this.bWriter = bWriter;
    }

    public void prepareToSend(String message) {

        sendingQueue.add(message);
        LinkedList<String> successfullySent = new LinkedList<>();
        if (bWriter != null) {
            try {
                for (String line : sendingQueue) {
                    bWriter.write(line);
                    successfullySent.add(line);
                    bWriter.newLine();
                    haveNews = true;
                }
            } catch (IOException ex) {
                System.out.println("SENDING FROM " + getName() + " DID NOT GO WELL");
            }
        } else {
            System.out.println(getName() + " NOT READY TO SEND " + message);
        }
        sendingQueue.removeAll(successfullySent);
    }

    public void run() {

        try {
            while (true) {
                if (haveNews) {
                    bWriter.flush();
                    haveNews = false;
                }

                try {
                    sleep(delayMilliSec);
                } catch (InterruptedException ex) {
                    System.out.println("SENDER INTERRUPTED");
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("SENDER SAYS GOODBYE");
    }
}
