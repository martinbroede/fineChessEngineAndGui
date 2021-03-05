package core;

import java.util.LinkedList;

public class Moves extends LinkedList<Move> {

    public void print(){
        for(Move move : this) System.out.print(move);
        System.out.println("\nTOTAL: " + this.size() +"+++++++++++");
    }

    public Moves getMovesFrom(byte from) {
        Moves movesFrom = new Moves();
        for (Move move : this) {
            if (move.isFrom(from)) {
                movesFrom.add(move);
            }
        }
        return movesFrom;
    }
}
