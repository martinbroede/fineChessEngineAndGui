package core;

public class CoreTest {

    public static void main(String[] args) {
        Chess chess = new Chess();
        chess.printASCII();
        chess.printThreats(Constants.WHITE);
        chess.printThreats(Constants.BLACK);
    }
}
