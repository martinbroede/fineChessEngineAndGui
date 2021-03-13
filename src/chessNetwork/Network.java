package chessNetwork;

import core.Chess;
import core.Move;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class Network {

    private final Queue<Move> moveQueue;
    private ChessServer server;
    private ChessClient client;
    private boolean active = false;
    private final int delayMilliSec = 100;
    private final Chess chess;
    private MoveUpdater moveUpdater;
    private boolean adviseUpdate = false;

    public Network(Chess chess) {

        this.chess = chess;
        moveQueue = new LinkedList<>();
    }

    public static void main(String[] args) {
        Chess chess = new Chess();
        Network net = new Network(chess);
        net.showClientIpDialog(new Point(100,100));
    }

    public void createServer(String configIpAndPort) {

        if ((server == null) && (client == null)) {
            server = new ChessServer(configIpAndPort, delayMilliSec, moveQueue);
            server.start();
            active = true;
            startMoveUpdater();
        } else {
            System.err.println("SERVER OR CLIENT ALREADY EXISTS");
        }
    }

    public void createClient(String configIpAndPort) {

        if ((server == null) && (client == null)) {
            client = new ChessClient(configIpAndPort, delayMilliSec, moveQueue); //todo adjust delay
            client.start();
            active = true;
            startMoveUpdater();
        } else {
            System.err.println("SERVER OR CLIENT ALREADY EXISTS");
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean updateAdvised() {
        return adviseUpdate;
    }

    public void reportUpdate() {
        adviseUpdate = false;
    }

    public void sendToNet(String message) {

        if (client != null) {
            client.send(message);
        } else if (server != null) {
            server.send(message);
        } else System.err.println("NETWORK IS NOT ACTIVE.");
    }

    public void safeDeleteServerOrClient() {

        if (server != null) {
            server.killThreads();
            server.interrupt();
        }
        if (client != null) {
            client.killThreads();
            client.interrupt();
        }
        server = null;
        client = null;
        System.out.println("SHUT DOWN SERVER/CLIENT. THREADS KILLED.");
        active = false;
        adviseUpdate = false;
        moveQueue.clear();
    }

    public void startMoveUpdater() {

        if (moveUpdater == null) {
            moveQueue.clear();
            System.out.println("STARTED SYNC.");
            moveUpdater = new MoveUpdater();
            moveUpdater.start();
        } else if (!moveUpdater.isAlive()) {
            moveUpdater = new MoveUpdater();
            System.out.println("RESTARTED SYNC.");
            moveUpdater.start();
        } else {

            System.out.println("SYNC IS ALREADY ACTIVE.");
        }
    }

    public void startGame(){
        chess.newGame();
        adviseUpdate = true;
        sendToNet("LET'S START!");
        sendToNet("MOVE " + Move.START_GAME);
    }

    public void showServerIpDialog(Point location) {
        new ServerIpDialog(location);
    }

    public void showClientIpDialog(Point location) {
        new ClientIpDialog(location);
    }

    class ServerIpDialog extends IpAndPortDialog {
        public ServerIpDialog(Point location) {
            super(location);
            okButton.addActionListener(e -> {
                String config = ipField.getText() + "/" + portField.getText();
                createServer(config);
                dialog.dispose();
            });
        }
    }

    class ClientIpDialog extends IpAndPortDialog {
        public ClientIpDialog(Point location) {
            super(location);
            okButton.addActionListener(e -> {
                String config = ipField.getText() + "/" + portField.getText();
                createClient(config);
                dialog.dispose();
            });
        }
    }

    class MoveUpdater extends Thread {

        @Override
        public void run() {

            while (active) {
                try {
                    sleep(delayMilliSec);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (moveQueue.size() > 0) {
                    Move move = moveQueue.poll();
                    if(move.getInformation()==Move.START_GAME){
                        chess.newGame();
                    }else {
                        chess.movePieceUser(move);
                    }

                    adviseUpdate = true;
                }
            }
            System.out.println("NETWORK NOT ACTIVE. TURNED SYNC OFF.");
        }
    }
}
