package gui;

import chessGameServer.MultiClientServer;
import core.Chess;
import core.CoreASCII;
import fineChessUpdater.Updater;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0){
            switch (args[0]){
                case "UPDATE":
                    System.out.println("TRY DOWNLOADING LATEST RELEASE");
                    new Updater();
                    break;
                case "ASCII":
                    new CoreASCII().run();
                    break;
                case "SERVER":
                    if(args.length > 1){
                        MultiClientServer server = new MultiClientServer(args[1]);
                        server.start();
                        break;
                    }
                    MultiClientServer server = new MultiClientServer("0.0.0.0/50005");
                    server.start();
                    break;
                default:
                    System.err.println("DO NOT KNOW COMMAND " + args[0]);
            }
        }else{
            Chess chess = new Chess();
            Gui gui = new Gui(chess);
        }
    }
}
