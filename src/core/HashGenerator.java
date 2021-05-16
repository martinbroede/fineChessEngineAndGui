package core;

import java.util.HashMap;
import java.util.Random;

public class HashGenerator {

    static final char[] PIECE_TYPES = {'B', 'b', 'K', 'k', 'N', 'n', 'P', 'p', 'Q', 'q', 'R', 'r'};
    static final int CASTLING_OPTIONS = 16;
    static final int EN_PASSANT_OPTIONS = 8;
    private final long turnValue;
    private final long[] castlingValues;
    private final long[] enPassantValues;
    private final HashMap<Character, long[]> randomValues;
    private long hashCode;

    public HashGenerator() {

        randomValues = new HashMap<>();
        hashCode = 0;
        Random rnd = new Random(0);


        for (char type : PIECE_TYPES) {
            long[] values = new long[64];
            for (int i = 0; i <= 63; i++) {
                values[i] = rnd.nextLong();
            }
            randomValues.put(type, values);
        }

        {
            long[] values = new long[EN_PASSANT_OPTIONS];

            for (int i = 0; i < EN_PASSANT_OPTIONS; i++) {
                values[i] = rnd.nextLong();
            }
            enPassantValues = values;
        }

        {
            long[] values = new long[CASTLING_OPTIONS];

            for (int i = 0; i < CASTLING_OPTIONS; i++) {
                values[i] = rnd.nextLong();
            }
            castlingValues = values;
        }

        turnValue = rnd.nextLong();
    }

    public void reset() {
        hashCode = 0;
        System.out.println("HASH CLEARED");
    }

    public long generateHashCode(char[] board, boolean whiteToMove, byte castling, byte enPassant) {

        long h = 0;
        for (int i = 0; i <= 63; i++) {
            char type = board[i];
            if (type != ' ') {
                h ^= randomValues.get(type)[i];
            }
        }

        if (whiteToMove) h ^= turnValue;

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
        if (enP >= 0) hashCode ^= enPassantValues[enP];
    }

    public void hashCastling(byte cRights) {
        hashCode ^= castlingValues[cRights];
    }
}
