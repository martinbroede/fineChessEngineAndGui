import core.Chess;
import core.CoreASCII;
import gui.Gui;
import misc.Downloader;

public class Main {

    public static void main(String[] args) {

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
                case "PROVIDE":
                    gui = new Gui(new Chess());
                    gui.network.createServer(args[1]);
                    break;
                case "CONNECT":
                    gui = new Gui(new Chess());
                    gui.network.createClient(args[1]);
                    break;
                default:
                    System.out.println("DO NOT KNOW ARGUMENT " + args[0]);
            }
        } else {
            new Gui(new Chess());
        }
    }
}
