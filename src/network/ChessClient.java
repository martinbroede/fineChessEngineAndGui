package network;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static misc.Properties.resourceBundle;

public abstract class ChessClient extends NetInstance {

    public ChessClient(String configIpAndPort) {

        System.out.printf("CREATE CLIENT CONNECTING TO %s%n", configIpAndPort);
        String[] args = configIpAndPort.split("/");
        if (args.length != 2) {
            System.out.println("WRONG NUMBER OF ARGUMENTS FOR CLIENT");
        } else {
            setAddr(args[0], Integer.parseInt(args[1]));
        }
    }

    public boolean provideSocket(String ip, int port) {

        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 5000);
            BufferedWriter bufferedWriter =
                    new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            sender.setWriter(bufferedWriter);
            scanner = new Scanner(socket.getInputStream());
            receiver = new Receiver("CLIENT RECEIVER", this);
            System.out.println("CONNECTED TO SERVER " + socket.getRemoteSocketAddress());
            String msg = String.format(resourceBundle.getString("connect.success"), socket.getRemoteSocketAddress());
            showMessage(msg);
            send(""); // to send messages in queue
            connectionSuccessful = true;
            return true;
        } catch (Exception ex) {
            showMessage(ex.toString());
            return false;
        }
    }

    public void connect() {

        if (provideSocket(ip, port)) {
            receiver.start();
        }
    }

    abstract void showMessage(String msg);

    @Override
    public void run() {
        connect();
    }

    @Override
    public void abort() {

        super.abort();
        System.out.println("CLIENT ABORTED");
    }
}