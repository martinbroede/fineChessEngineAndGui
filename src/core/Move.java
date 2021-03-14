package core;

import java.util.InputMismatchException;

public class Move {
    public static final short KING_SIDE_CASTLING =/*    */0b0001000000000000; // readability
    public static final short QUEEN_SIDE_CASTLING =/*   */0b0010000000000000;
    public static final short EN_PASSANT =/*            */0b0011000000000000;
    public static final short PROMOTION_BISHOP =/*      */0b0100000000000000;
    public static final short PROMOTION_KNIGHT =/*      */0b0101000000000000;
    public static final short PROMOTION_ROOK =/*        */0b0110000000000000;
    public static final short PROMOTION_QUEEN =/*       */0b0111000000000000;

    /* negative code values reserved for for online games: */
    public static final short START_GAME = -1000;
    public static final short OPPONENT_BLACK =  -1001;
    public static final short OPPONENT_WHITE = -1002;
    public static final short OFFER_DRAW = -1010;
    public static final short RESIGN = -1011;

    private short information; //todo can be short right?

    public Move(byte from, byte to) {
        information = (short) (from + to * 64);
    }

    public Move(byte from, byte to, short special) {
        information = (short) ((short) (from + to * 64) | special);
    }

    public Move(String moveString) {

        if (moveString.length() > 7 | moveString.length() < 5) {
            throw new InputMismatchException(moveString + " is not a move. Must consist of 5-6 Characters.");
        }
        String[] moveExpressions = moveString.split(" ");
        if (moveExpressions.length <= 1) throw new InputMismatchException(moveString +
                " is not a move. Coordinates must be separated like: A1 A2");

        byte from = Parser.parse(moveExpressions[0]);
        byte to = Parser.parse(moveExpressions[1]);
        short special = 0;

        if (moveExpressions.length > 2) {
            switch (moveExpressions[2]) {
                case "Q":
                    special |= PROMOTION_QUEEN;
                    break;
                case "B":
                    special |= PROMOTION_BISHOP;
                    break;
                case "N":
                    special |= PROMOTION_KNIGHT;
                    break;
                case "R":
                    special |=PROMOTION_ROOK;
                    break;
            }
        }
        information = (short) ((short) (from + to * 64) | special);
    }

    public Move(short information){
        this.information = information;
    }

    public static byte getTo(short info) {
        return (byte) ((info / 64) % 64);
    }

    public static byte getFrom(short info) {
        return (byte) (info % 64);
    }

    public static short getSpecial(short info) {
        return (short) (info & 0b1111000000000000);
    }

    public short getInformation() {
        return information;
    }

    public short getSpecial() {
        return (short) (information & 0b1111000000000000);
    }

    public byte getFrom() {
        return (byte) (information % 64);
    }

    public byte getTo() {
        return (byte) ((information / 64) % 64);
    }

    public boolean isFrom(byte from) {
        return information % 64 == from;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        Move other = (Move) obj;
        return other.information == this.information;
    }

    public boolean equalToMove(byte from, byte to) {
        return from + to * 64 == this.information;
    }

    @Override
    public int hashCode() {
        return information;
    }


    @Override
    public String toString() {
        String outp = " | (" + Parser.parse(information % 64) + "->" + Parser.parse((information / 64) % 64) + ')';
        if (information < KING_SIDE_CASTLING) return outp; //no castling, enPassant or promotion
        else if ((information & 0b1111000000000000) == QUEEN_SIDE_CASTLING) return " | o-o-o";
        else if ((information & 0b1111000000000000) == KING_SIDE_CASTLING) return " | o-o";
        else {
            System.err.println("DON'T KNOW THIS MOVE: " + information + " (FROM CLASS: Move.java)");
            return "?";
        }
    }
}
