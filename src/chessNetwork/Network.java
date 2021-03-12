package chessNetwork;

import core.Chess;
import core.Move;

import java.util.LinkedList;
import java.util.Queue;

public class Network {

    private Queue<Move> moveQueue;
    private ChessServer server;
    private ChessClient client;
    private boolean active = false;
    private int delayMilliSec = 150;
    private Chess chess;
    private MoveUpdater moveUpdater;
    private boolean adviseUpdate = false;

    public Network(Chess chess) {

        this.chess = chess;
        moveQueue = new LinkedList<>();
    }

    public void createServer() {

        if ((server == null) && (client == null)) {
            server = new ChessServer(delayMilliSec, moveQueue);
            server.start();
            active = true;

        } else {

            System.err.println("SERVER OR CLIENT ALREADY EXISTS");
        }
    }

    public void createClient() {

        if ((server == null) && (client == null)) {

            client = new ChessClient(delayMilliSec, moveQueue); //todo adjust delay
            client.start();
            active = true;

        } else {
            System.err.println("SERVER OR CLIENT ALREADY EXISTS");
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean updateAdvised(){
        return adviseUpdate;
    }

    public void reportUpdate(){
        adviseUpdate = false;
    }

    public void send(String message) {

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

    class MoveUpdater extends Thread {

        @Override
        public void run() {

            while (active) {
                try {
                    sleep(delayMilliSec);
                } catch (InterruptedException ex) {
                }
                if (moveQueue.size() > 0) {
                    chess.movePieceUser(moveQueue.poll());
                    adviseUpdate = true;
                }
            }
            System.out.println("NETWORK NOT ACTIVE. TURNED SYNC OFF.");
        }
    }
}
