package core;

import java.io.Serializable;

public class Chess extends MoveGenerator implements Serializable {

    private final String init = "RNBQKBNRPPPPPPPP                                pppppppprnbqkbnr";


    public Chess() {
        whitePieces = new WhitePieceCollection();
        blackPieces = new BlackPieceCollection();
        castling = new Castling();
        newGame();
    }

    public void newGame() {
        moveCounter = 0;
        moveCounterLastCaptureOrPawnMove = 0;
        whiteToMove = true;
        castling.setDefault();
        whitePieces.clear();
        blackPieces.clear();
        for (byte pos = 0; pos <= 63; pos++) {
            char c = (char) (init.getBytes()[pos]);
            board[pos] = c;
            if (is_white_piece(c)) whitePieces.add(new Piece(pos,c));
            if (is_black_piece(c)) blackPieces.add(new Piece(pos,c));
        }
        whitePieces.updateThreats();
        blackPieces.updateThreats();
    }

    /** checks if legal, if so move piece */
    public boolean movePieceUser(Move move) {
        if (!getPseudoLegalMoves().contains(move)) return false;
        movePiece(move);
        return true;
    }

    /** move piece without check. Computer won't try  illegal moves ;) */
    private void movePiece(Move move) {
        byte from = move.getFrom();
        byte to = move.getTo();
        char capture = board[to]; //doesn't need to be a capture => can also be ' '

        if (capture != ' ') {
            if (whiteToMove) {
                blackPieces.removePiece(to);
            } else{
                whitePieces.removePiece(to);
            }
            moveCounterLastCaptureOrPawnMove = moveCounter;
        }

        if (whiteToMove) whitePieces.changePosition(from, to);
        else blackPieces.changePosition(from, to);

        board[to] = board[from];
        board[from] = ' ';

        if (whiteToMove) whitePieces.updateThreats();
        else blackPieces.updateThreats();

        whiteToMove = !whiteToMove;
        moveCounter++;
    }

    public boolean pieceAtSquare(int i, boolean color) {
        if (color == Constants.WHITE) return is_white_piece(board[i]);
        else return is_black_piece(board[i]);
    }

    public char[] getBoard() {
        return board;
    }

    public boolean getTurnColor() {
        return whiteToMove;
    }

    /* ++++++++++ ONLY STRING FUNCTIONS FOR DEBUGGING PURPOSES BELOW ++++++++++ */

    public void printASCII() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        String outp = "";
        String line = "|";
        for (int i = 0; i <= 63; i++) {
            line += board[i] + "|";
            if (i % 8 == 7) {
                outp = line + (i / 8 + 1) + "\n" + outp;
                line = "|";
            }
        }
        outp = "\n" + outp + ".A.B.C.D.E.F.G.H." + "\n";
        return outp;
    }
}
