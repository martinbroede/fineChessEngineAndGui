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
    protected short moveCounterLastCaptureOrPawnMove;
    protected byte enPassantPawn;


    public Moves getPseudoLegalMoves() {

        if (whiteToMove) {
            return whitePieces.getPseudoLegalMoves();
        } else return blackPieces.getPseudoLegalMoves();
    }

    public class Castling {

        public final static byte ALL_RIGHTS = 0b1111;
        public final static byte NO_RIGHTS = 0;

        private byte rights;

        public byte getRights() {
            return rights;
        }

        public void setRights(byte rights) {
            this.rights = rights;
        }

        public void reset() {
            /* MSB-wKS wQS bKS bQS-LSB */
            rights = 0b1111;
        }

        public void disableWhiteKingSide() {
            rights &= ~(1 << 3);
        }

        public void disableWhiteQueenSide() {
            rights &= ~(1 << 2);
        }

        public void disableBlackKingSide() {
            rights &= ~(1 << 1);
        }

        public void disableBlackQueenSide() {
            rights &= ~(1);
        }

        public boolean whiteKingSide() {
            return (rights & (1 << 3)) > 0;
        }

        public boolean whiteQueenSide() {
            return (rights & (1 << 2)) > 0;
        }

        public boolean blackKingSide() {
            return (rights & (1 << 1)) > 0;
        }

        public boolean blackQueenSide() {
            return (rights & 1) > 0;
        }

        public void print() {

            System.out.println("WHITE KINGSIDE:\t" + whiteKingSide() +
                    "\nWHITE QUEENSIDE:" + whiteQueenSide() +
                    "\nBLACK KINGSIDE:\t" + blackKingSide() +
                    "\nBLACK QUEENSIDE:" + blackQueenSide() +
                    "\nRIGHTS:\t\t\t" + Integer.toBinaryString(rights) + "\n");
        }
    }

    class Piece {

        private byte position;
        private char type;
        private PiecePattern pattern;

        public Piece(byte pos, char type) {
            this.position = pos;
            this.type = type;
            this.pattern = getPiecePattern(type);
        }

        public byte getPosition() {
            return position;
        }

        public void setPosition(byte position) {
            this.position = position;
        }

        public char getType() {
            return type;
        }

        public void setType(char type) {
            this.type = type;
        }

        public void setPattern(PiecePattern pattern) {
            this.pattern = pattern;
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
            System.out.print(Parser.parseSymbol(type) + " #" + Parser.parse(position) + " | ");
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
            throw new InputMismatchException("NO PIECE AT " + Parser.parse(from));
        }

        public void removePiece(byte position) {

            Piece removePiece = null;
            for (Piece p : this) {
                if (p.getPosition() == position) {
                    removePiece = p;
                    break;
                }
            }
            this.remove(removePiece);
            capturedPieces.add(removePiece);
        }

        public void activateLastCapturedPiece() {
            this.add(capturedPieces.pop());
        }

        public void updateThreats() {

            Arrays.fill(threats, (byte) 0);
            for (Piece p : this) p.updateThreats(threats);
        }

        public void printThreats() {

            String outp = "";
            String line = "|";
            for (int i = 0; i <= 63; i++) {
                line += threats[i] + "|";
                if (i % 8 == 7) {
                    outp = line + (i / 8 + 1) + "\n" + outp;
                    line = "|";
                }
            }
            outp = outp + ".A.B.C.D.E.F.G.H." + "\n";

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

                String outp = "";

                for (Piece p : this) {
                    outp += Parser.parseSymbol(p.type);
                }

                return outp;
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
