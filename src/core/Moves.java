package core;

import java.util.LinkedList;

public class Moves extends LinkedList<Move> {

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