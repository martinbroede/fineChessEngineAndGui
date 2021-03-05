package core;


import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Stack;

public class MoveGenerator extends PiecePatterns {

    public WhitePieceCollection whitePieces;
    public BlackPieceCollection blackPieces;
    public Castling castling;
    protected boolean whiteToMove;
    protected int moveCounter;
    protected int moveCounterLastCaptureOrPawnMove;


    public Moves getPseudoLegalMoves() {
        if (whiteToMove) {
            return whitePieces.getPseudoLegalMoves();
        } else return blackPieces.getPseudoLegalMoves();
    }

    class Castling {

        public boolean white = true;
        public boolean black = true;
        public boolean whiteKingSide = true;
        public boolean whiteQueenSide = true;
        public boolean blackKingSide = true;
        public boolean blackQueenSide = true;
        /**
         * bit order : MSB- w, b, wKS, wQS, bKS, bQS -LSB
         * +              - 5  4   3    2    1    0
         */
        private byte hash = 0b111111;

        public byte getHash(){
            return hash;
        }

        public void setDefault() {
            hash = 0b111111;
            white = true;
            black = true;
            whiteKingSide = true;
            whiteQueenSide = true;
            blackKingSide = true;
            blackQueenSide = true;
        }

        public void disableWhiteKingSide() {
            whiteKingSide = false;
            hash &= ~(1 << 3);
            if (!whiteQueenSide) {
                white = false;
                hash &= ~(1 << 5);
            }
        }

        public void disableWhiteQueenSide() {
            whiteQueenSide = false;
            hash &= ~(1 << 2);
            if (!whiteKingSide) {
                white = false;
                hash &= ~(1 << 5);
            }
        }

        public void disableBlackKingSide() {
            blackKingSide = false;
            if (!blackQueenSide) black = false;
        }

        public void disableBlackQueenSide() {
            blackQueenSide = false;
            if (!blackKingSide) black = false;
        }

        public void restore(byte h) {
            if (h != 0) {
                if ((h & (1 << 5)) > 0) white = true;
                if ((h & (1 << 4)) > 0) black = true;
                if ((h & (1 << 3)) > 0) whiteKingSide = true;
                if ((h & (1 << 2)) > 0) whiteQueenSide = true;
                if ((h & (1 << 1)) > 0) blackKingSide = true;
                if ((h & (1 << 0)) > 0) blackQueenSide = true;
            }
            hash = h;
        }

        public void restoreCompletely(byte h) {
            white = false;
            black = false;
            whiteKingSide = false;
            whiteQueenSide = false;
            blackKingSide = false;
            blackQueenSide = false;
            restore(h);
        }

        public void print() {
            System.out.println("CASTLING-WHITE:\t" + white +
                    "\nW-KINGSIDE:\t\t" + whiteKingSide +
                    "\nW-QUEENSIDE:\t" + whiteQueenSide +
                    "\nBLACK:\t\t\t" + black +
                    "\nB-KINGSIDE:\t\t" + blackKingSide +
                    "\nB-QUEENSIDE:\t" + blackQueenSide + "\n" +
                    Integer.toBinaryString(hash) + "\n");
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

        public Moves getPseudoLegalMoves(byte[] threats) {
            return pattern.getMoves(position, threats);
        }

        public void print() {
            System.out.print(Parser.parseSymbol(type) + " #" + Parser.parse(position) + " | ");
        }

    }

    public class PieceCollection extends HashSet<Piece> {

        final CapturedPieces capturedPieces;
        private final byte[] threats;

        public PieceCollection() {
            threats = new byte[64];
            capturedPieces = new CapturedPieces();
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

        public void changePosition(byte from, byte to) {
            for (Piece p : this) { //todo this is not efficient. find better method later
                if (p.getPosition() == from) {
                    p.setPosition(to);
                    return;
                }
            }
            throw new InputMismatchException("NO PIECE AT " + Parser.parse(from) + ". E22");
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

        private class CapturedPieces extends Stack<Piece> {

            protected void print() {

                for (Piece p : this) {
                    p.print();
                }
                System.out.println("TOTAL: " + this.size() + " CAPTURED PIECES");
            }
        }
    }

    public class WhitePieceCollection extends PieceCollection {

        public WhitePieceCollection() {

            super();
        }

        public Moves getPseudoLegalMoves() {
            Moves moves = new Moves();
            for (Piece p : this) {
                moves.addAll(p.getPseudoLegalMoves(blackPieces.getThreats()));
            }
            return moves;
        }

        @Override
        public void printThreats() {

            System.out.println("\nWHITE THREATS: \n");
            super.printThreats();
        }
    }

    public class BlackPieceCollection extends PieceCollection {

        public BlackPieceCollection() {
            super();
        }

        public Moves getPseudoLegalMoves() {
            Moves moves = new Moves();
            for (Piece p : this) {
                moves.addAll(p.getPseudoLegalMoves(whitePieces.getThreats()));
            }
            return moves;
        }

        @Override
        public void printThreats() {

            System.out.println("\nBLACK THREATS: \n");
            super.printThreats();
        }
    }
}
