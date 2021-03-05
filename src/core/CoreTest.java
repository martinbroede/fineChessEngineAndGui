package core;

import java.awt.*;

public class CoreTest {

    public static void main(String[] args) {
        Chess chess = new Chess();
        chess.castling.print();
        chess.castling.disableWhiteQueenSide();
        chess.castling.print();
        chess.castling.disableWhiteKingSide();
        chess.castling.print();
        chess.castling.restore((byte)0b110001);
        chess.castling.print();
    }
}
