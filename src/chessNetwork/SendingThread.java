package chessNetwork;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

class SendingThread extends Thread {

    private final int delayMilliSec;
    LinkedList<String> sendingQueue;
    BufferedWriter bWriter;
    private boolean hasNews = false;

    public SendingThread(BufferedWriter bWriter, int delayMilliSec, String threadName) {
        setName(threadName);
        this.bWriter = bWriter;
        sendingQueue = new LinkedList<>();
        this.delayMilliSec = delayMilliSec;
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
                    hasNews = true;
                }
            } catch (IOException ex) {
                System.out.println("SENDING FROM " + getName() + " DID NOT GO WELL");
            }
        }

        for (String line : sendingQueue) System.out.println("#" + line + "#"); //todo remove
        sendingQueue.removeAll(successfullySent);
    }

    public void run() {

        try {
            while (true) {
                if (hasNews) {
                    bWriter.flush();
                    hasNews = false;
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
