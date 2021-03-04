package core;

import java.util.InputMismatchException;

public class Move {
    int move; //todo can be short right?

    public Move(byte from, byte to) {
        move = from + to * 64;
    }

    public Move(String moveString) {
            if(moveString.length() > 7 | moveString.length() < 5){
                throw new InputMismatchException(moveString + " is not a move. Must consist of 5-7 Characters.");
            }
            byte from = Parser.parse(moveString.split(" ")[0]);
            byte to = Parser.parse(moveString.split(" ")[1]);
            move = from + to * 64;
    }

    public byte getFrom(){
        return (byte) (move%64);
    }

    public byte getTo(){
        return (byte) (move>>6);
    }

    public boolean isFrom(byte from){
        return move%64 == from;
    }

    public static int coordToInt(byte from, byte to) {
        return from + to * 64;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        Move other = (Move) obj;
        return other.move == this.move;
    }

    public boolean equalToMove(byte from, byte to) {
        return from + to * 64 == this.move;
    }

    @Override
    public int hashCode() {
        return move;
    }


    @Override
    public String toString() {
        return " | (" + Parser.parse(move % 64) + "->" + Parser.parse(move / 64) + ')';
    }
}
