package core;

import java.io.Serializable;
import java.util.Arrays;

public class Chess implements Serializable {
    private final String init = "RNBQKBNRPPPPPPPP                                pppppppprnbqkbnr";
    private final char[] board;
    private final byte[] whiteThreats;
    private final byte[] blackThreats;
    private final MoveGenerator moveGenerator;
    private boolean whiteToMove;
    private boolean whiteKingSideCastling;
    private boolean whiteQueenSideCastling;
    private boolean blackKingSideCastling;
    private boolean blackQueenSideCastling;
    private int numberOfWhitePieces;
    private int numberOfBlackPieces;

    public Chess() {
        board = new char[64];
        whiteThreats = new byte[64];
        blackThreats = new byte[64];
        moveGenerator = new MoveGenerator(board);
        newGame();
    }

    public void newGame() {
        numberOfWhitePieces = 16; //todo anpassen. müssen ja nicht immer 16 sein. besser über hashset
        numberOfBlackPieces = 16;
        whiteToMove = true;
        Arrays.fill(whiteThreats, (byte) 0);
        Arrays.fill(blackThreats, (byte) 0);
        for (byte i = 0; i <= 63; i++) {
            char c = (char) (init.getBytes()[i]);
            board[i] = c;
        }
        instatiateThreats();
    }

    private void instatiateThreats() {
        Moves moves;

        moves = getAllMoves(Constants.WHITE);
        for (Move move : moves) {
            byte to = move.getTo();
            whiteThreats[to]++;
        }

        moves = getAllMoves(Constants.BLACK);
        for (Move move : moves) {
            byte to = move.getTo();
            blackThreats[to]++;
        }
    }

    public Moves getAllMoves() {
        if (whiteToMove) {
            return getAllMoves(Constants.WHITE);
        } else return getAllMoves(Constants.BLACK);
    }

    public Moves getAllMoves(boolean color) {
        Moves moves = new Moves();
        if (color == Constants.WHITE) {
            for (byte pos = 0; pos <= 63; pos++) { //todo respect number of pieces
                char type = board[pos];
                if (type == ' ') continue;
                else if (is_white_piece(type)) //todo remove later. will be redundant with list of pieces.
                    moves.addAll(moveGenerator.getWhitePiece(type).getMoves(pos));
            }
        } else {
            for (byte pos = 63; pos >= 0; pos--) { //todo respect number of pieces
                char type = board[pos];
                if (type == ' ') continue;
                else if (is_black_piece(type)) //todo remove later. will be redundant with list of pieces.
                    moves.addAll(moveGenerator.getBlackPiece(type).getMoves(pos));
            }
        }
        return moves;
    }

    private boolean is_black_piece(char c) {
        return (byte) c >= 97; //shortcut for 'lower case' (black pieces)
    }

    private boolean is_white_piece(char c) {
        return (byte) c >= 65 && (byte) c <= 90; //shortcut for 'upper case' (white pieces)
    }

    /** checks if legal, if so move piece */
    public boolean movePieceUser(Move move) {
        if (!getAllMoves().contains(move)) return false;
        movePiece(move);
        return true;
    }

    /** move piece without check. Computer won't try  illegal moves ;) */
    private void movePiece(Move move) {
        byte from = move.getFrom();
        byte to = move.getTo();
        updateThreats(from, to);
        char capture = board[to]; //doesn't need to be a capture => can also be ' '
        if (capture != ' ') {
            if (whiteToMove) System.out.println("CAPT. BLACK PIECE: " + capture);
            else System.out.println("CAPT. WHITE PIECE: " + capture);
        }
        board[to] = board[from];
        board[from] = ' ';

        whiteToMove = !whiteToMove;
    }

    private void updateThreats(byte from, byte to) {
        if (whiteToMove) {
            MoveGenerator.Piece piece = moveGenerator.getWhitePiece(board[from]);
            Moves moves = piece.getMoves(from);

        }
    }

    public boolean pieceAtSquare(int i) {
        return board[i] != ' ';
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

    public void printThreats(boolean color) {
        String outp = "";
        String line = "|";
        for (int i = 0; i <= 63; i++) {
            if (color == Constants.WHITE)
                line += whiteThreats[i] + "|";
            else line += blackThreats[i] + "|";
            if (i % 8 == 7) {
                outp = line + (i / 8 + 1) + "\n" + outp;
                line = "|";
            }
        }
        outp = outp + ".A.B.C.D.E.F.G.H." + "\n";
        if (color == Constants.WHITE) outp = "\nWHITE THREATS:\n" + outp;
        else outp = "\nBLACK THREATS:\n" + outp;
        System.out.println(outp);
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
