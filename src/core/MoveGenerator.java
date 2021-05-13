package core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Stack;

public class MoveGenerator extends PiecePatterns {

    public PieceCollectionWhite whitePieces;
    public PieceCollectionBlack blackPieces;
    public Castling castling;
    protected boolean whiteToMove;
    protected short moveCounter;
    protected short lastCaptureOrPawnMove;
    /** the file number ('A' = 0) in which the pawn to be captured is located */
    protected byte enPassantPawn;


    public Moves getPseudoLegalMoves() {

        if (whiteToMove) {
            return whitePieces.getPseudoLegalMoves();
        } else return blackPieces.getPseudoLegalMoves();
    }

    class Piece {

        private final char type;
        private byte position;
        private PiecePattern pattern;

        public Piece(byte pos, char type) {
            this.position = pos;
            this.type = type;
            this.pattern = getPiecePattern(type);
        }

        public PiecePattern getPattern() {
            return pattern;
        }

        public void setPattern(PiecePattern pattern) {
            this.pattern = pattern;
        }

        public byte getPosition() {
            return position;
        }

        public void setPosition(byte position) {
            this.position = position;
        }

        public void updateThreats(byte[] threats) {
            pattern.updateThreats(this.position, threats);
        }

        public Moves getPseudoLegalMoves() {
            return pattern.getMoves(position, enPassantPawn);
        }

        public Moves getPseudoLegalKingMoves(byte[] threats, boolean kingSideCastling, boolean queenSideCastling) {
            return pattern.getKingMoves(position, threats, kingSideCastling, queenSideCastling);
        }

        public void print() {
            System.out.print(Util.parseSymbol(type) + " #" + Util.parse(position) + " | ");
        }

    }

    public class PieceCollection extends HashSet<Piece> {

        private final CapturedPieces capturedPieces;
        private final byte[] threats;
        protected Piece king;

        public PieceCollection() {
            threats = new byte[64];
            capturedPieces = new CapturedPieces();
        }

        public void setKing(Piece king) {
            this.king = king;
        }

        public byte[] getThreats() {
            return threats;
        }

        public void print() {
            for (Piece p : this) {
                p.print();
            }
            System.out.println("TOTAL: " + this.size() + " ACTIVE PIECES");
        }

        public void printCaptured() {
            capturedPieces.print();
        }

        public String getCapturedPiecesAsSymbols() {
            return capturedPieces.getCapturedPiecesAsSymbols();
        }

        /**
         * Change position of chess piece on position "from" to position "to". Return piece that changes position."
         *
         * @param from square the piece comes from
         * @param to   square the piece shall move to
         * @return piece that moves
         */
        public Piece changePosition(byte from, byte to) {
            for (Piece p : this) { //todo hashMap with squares should be faster than a loop...
                if (p.getPosition() == from) {
                    p.setPosition(to);
                    return p;
                }
            }
            throw new InputMismatchException("NO PIECE AT " + Util.parse(from));
        }

        /**
         * Remove piece located at given position
         *
         * @param pos Position of the piece to be removed
         * @return value of the removed piece
         */
        public short removePiece(byte pos) {

            Piece removePiece = null;
            for (Piece p : this) {
                if (p.getPosition() == pos) {
                    removePiece = p;
                    break;
                }
            }
            this.remove(removePiece);
            capturedPieces.add(removePiece);
            assert removePiece != null;
            return PIECE_VALUES.get(removePiece.getPattern());
        }

        public void activateLastCapturedPiece() {
            this.add(capturedPieces.pop());
        }

        public void updateThreats() {

            Arrays.fill(threats, (byte) 0);
            for (Piece p : this) p.updateThreats(threats);
        }

        public void printThreats() {

            StringBuilder outp = new StringBuilder();
            StringBuilder line = new StringBuilder("|");
            for (int i = 0; i <= 63; i++) {
                line.append(threats[i]).append("|");
                if (i % 8 == 7) {
                    outp.insert(0, line.toString() + (i / 8 + 1) + "\n");
                    line = new StringBuilder("|");
                }
            }
            outp.append(".A.B.C.D.E.F.G.H.").append("\n");

            System.out.println(outp);
        }

        @Override
        public void clear() {
            super.clear();
            capturedPieces.clear();
        }

        private class CapturedPieces extends Stack<Piece> {

            protected void print() {

                for (Piece p : this) {
                    p.print();
                }
                System.out.println("TOTAL: " + this.size() + " CAPTURED PIECES");
            }

            protected String getCapturedPiecesAsSymbols() {

                StringBuilder outp = new StringBuilder();

                for (Piece p : this) {
                    outp.append(Util.parseSymbol(p.type));
                }

                return outp.toString();
            }
        }
    }

    public class PieceCollectionWhite extends PieceCollection {

        public PieceCollectionWhite() {

            super();
        }

        public Moves getPseudoLegalMoves() {

            Moves moves = new Moves();

            for (Piece p : this) moves.addAll(p.getPseudoLegalMoves());

            moves.addAll(king.getPseudoLegalKingMoves(blackPieces.getThreats(),
                    castling.whiteKingSide(), castling.whiteQueenSide()));

            return moves;
        }

        @Override
        public void printThreats() {

            System.out.println("\nWHITE THREATS: \n");
            super.printThreats();
        }
    }

    public class PieceCollectionBlack extends PieceCollection {

        public PieceCollectionBlack() {
            super();
        }

        public Moves getPseudoLegalMoves() {

            Moves moves = new Moves();

            for (Piece p : this) moves.addAll(p.getPseudoLegalMoves());

            moves.addAll(king.getPseudoLegalKingMoves(whitePieces.getThreats(),
                    castling.blackKingSide(), castling.blackQueenSide()));
            return moves;
        }

        @Override
        public void printThreats() {

            System.out.println("\nBLACK THREATS: \n");
            super.printThreats();
        }
    }
}
