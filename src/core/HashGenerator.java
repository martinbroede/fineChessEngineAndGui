package core;

import java.util.HashMap;
import java.util.Random;

public class HashGenerator {

    final char[] pieceTypes = {'B', 'b', 'K', 'k', 'N', 'n', 'P', 'p', 'Q', 'q', 'R', 'r'};
    final int castlingOptions = 16;
    final int enPassantOptions = 8;
    final long turnValue;
    final long[] castlingValues;
    final long[] enPassantValues;
    final HashMap<Character, long[]> randomValues;
    final HashMap<Long, Integer> counter; //to count positions
    private long hashCode;

    public HashGenerator() {

        randomValues = new HashMap<>();
        counter = new HashMap<>();

        hashCode = 0;
        Random rnd = new Random(0);


        for (char type : pieceTypes) {
            long[] values = new long[64];
            for (int i = 0; i <= 63; i++) {
                values[i] = rnd.nextLong();
            }
            randomValues.put(type, values);
        }

        {
            long[] values = new long[enPassantOptions];

            for (int i = 0; i < enPassantOptions; i++) {
                values[i] = rnd.nextLong();
            }
            enPassantValues = values;
        }

        {
            long[] values = new long[castlingOptions];

            for (int i = 0; i < castlingOptions; i++) {
                values[i] = rnd.nextLong();
            }
            castlingValues = values;
        }

        turnValue = rnd.nextLong();
    }

    public void reset() {
        counter.clear();
        hashCode = 0;
        System.out.println("HASH GENERATOR CLEARED. HASH CODE = 0");
    }

    public long generateHashCode(char[] board, boolean whiteToMove, byte castling, byte enPassant) {

        long h = 0;
        for (int i = 0; i <= 63; i++) {
            char type = board[i];
            if (type != ' ') {
                h ^= randomValues.get(type)[i];
            }
        }

        if(whiteToMove) h ^= turnValue;

        h ^= castlingValues[castling];

        if (enPassant >= 0) h ^= enPassantValues[enPassant];

        return h;
    }

    public long getHashCode() {
        return hashCode;
    }

    public void setHashCode(long h) {
        this.hashCode = h;
    }

    public void hashPosition(char type, byte position) {
        hashCode ^= randomValues.get(type)[position];
    }

    public void hashTurn() {
        hashCode ^= turnValue;
    }

    public void hashEnPassant(byte enP) {
        hashCode ^= enPassantValues[enP];
    }

    public void hashCastling(byte cRights) {
        hashCode ^= castlingValues[cRights];
    }
}
