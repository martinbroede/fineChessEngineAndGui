import chessGameServer.MultiClientServer;
import core.Chess;
import core.CoreASCII;
import fineChessUpdater.Downloader;
import gui.Gui;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {

        Chess chess;
        Gui gui;

        if (args.length > 0) {

            switch (args[0]) {
                case "UPDATE":
                    System.out.println("TRY DOWNLOADING LATEST RELEASE");
                    Downloader.download();
                    break;
                case "ASCII":
                    CoreASCII.play();
                    break;
                case "SERVER":
                    if (args.length > 1) {
                        MultiClientServer server = new MultiClientServer(args[1]);
                        server.start();
                        break;
                    }
                    MultiClientServer server = new MultiClientServer("0.0.0.0/50005");
                    server.start();
                    break;
                case "PROVIDE":
                    chess = new Chess();
                    gui = new Gui(chess);
                    gui.network.createServer(args[1]);
                    break;
                case "CONNECT":
                    chess = new Chess();
                    gui = new Gui(chess);
                    gui.network.createClient(args[1]);
                    break;
                default:
                    System.err.println("DO NOT KNOW ARGUMENT " + args[0]);
                    exit(0);
            }
        } else {
            chess = new Chess();
            gui = new Gui(chess);
        }
    }
}
