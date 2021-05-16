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

        if (!message.equals("")) {
            message = message + NetInstance.MESSAGE_DELIMITER;
            String asciiMsg = message.replace("ü", "ue")
                    .replace("ö", "oe")
                    .replace("ä", "ae")
                    .replace("Ü", "UE")
                    .replace("Ö", "OE")
                    .replace("Ä", "AE")
                    .replace("ß", "ss");
            asciiMsg = asciiMsg.replaceAll("[^\\x00-\\x7F]", "");
            sendingQueue.add(asciiMsg);
        }
        LinkedList<String> successfullySent = new LinkedList<>();
        if (bufferedWriter != null) {
            try {
                for (String line : sendingQueue) {
                    bufferedWriter.write(line);
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