package chessNetwork;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

class Sender {

    private final String serviceName;
    private final LinkedList<String> sendingQueue;
    private BufferedWriter bufferedWriter;

    public Sender(String serviceName) {

        this.serviceName = serviceName;
        sendingQueue = new LinkedList<>();
    }

    public void setWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public void bufferedSend(String message) {

        if (!message.equals("")) sendingQueue.add(message);
        LinkedList<String> successfullySent = new LinkedList<>();
        if (bufferedWriter != null) {
            try {
                for (String line : sendingQueue) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    successfullySent.add(line);
                }
            } catch (IOException ex) {
                System.out.println("SENDING FROM " + serviceName + " DID NOT GO WELL");
            }
        } else {
            System.out.println(serviceName + " NOT READY TO SEND " + message);
        }
        sendingQueue.removeAll(successfullySent);
    }
}