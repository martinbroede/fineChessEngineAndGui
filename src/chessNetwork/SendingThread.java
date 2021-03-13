package chessNetwork;

import java.io.BufferedWriter;
import java.io.IOException;

class SendingThread extends Thread {

    BufferedWriter bWriter;
    private int delayMilliSec;
    private boolean hasNews = false;

    public SendingThread(BufferedWriter bWriter, int delayMilliSec, String threadName) {
        setName(threadName);
        this.bWriter = bWriter;
        this.delayMilliSec = delayMilliSec;
    }

    public void prepareToSend(String message) {
        try {
            bWriter.write(message);
            bWriter.newLine();
            hasNews = true;
        } catch (IOException ex) {
            System.out.println("PREPARING TO SEND FROM " + getName() + " DID NOT GO WELL");
        }
    }

    public void run() {
        String message = "";

        try{
            while(true){
                if(hasNews){
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
        } catch(IOException ex){
            ex.printStackTrace();
        }
        System.out.println("SENDER SAYS GOODBYE");
    }
}
