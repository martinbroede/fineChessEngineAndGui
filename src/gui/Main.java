package gui;

import core.Chess;
import fineChessUpdater.Updater;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0){
            switch (args[0]){
                case "UPDATE":
                    System.out.println("TRY DOWNLOADING LATEST RELEASE");
                    new Updater();
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
