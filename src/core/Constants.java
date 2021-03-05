package core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;

public class Constants {
    static public final boolean WHITE = true;
    static public final boolean BLACK = false;
    public final byte[][] WHITE_PAWN_CAPTURE_SQUARES;
    public final byte[][] WHITE_PAWN_STRAIGHT_SQUARES;
    public final byte[][] BLACK_PAWN_CAPTURE_SQUARES;
    public final byte[][] BLACK_PAWN_STRAIGHT_SQUARES;
    public final byte[][][] ROOK_SQUARES;
    public final byte[][][] BISHOP_SQUARES;
    public final byte[][] KNIGHT_SQUARES;
    public final byte[][][] QUEEN_SQUARES;
    public final byte[][] KING_SQUARES;
    public final byte[][] FILE_DISTANCE;// TODO don't need these ... ?
    public final byte[][] RANK_DISTANCE;
    public final byte[][] DIAGONAL_DISTANCE;
    public final byte[][] ANTI_DIAGONAL_DISTANCE;// TODO don't need these ... ?
    public final char[] FILES = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'}; //"COLUMNS"
    public final char[] RANKS = {'1', '2', '3', '4', '5', '6', '7', '8'}; //"ROWS"
    private Square[] allSquares;

    public Constants() {

        allSquares = Square.createAllSquares();

        ROOK_SQUARES = new byte[4][64][];
        for (int i = 0; i <= 63; i++) {
            ROOK_SQUARES[0][i] = getNorth(i);
            ROOK_SQUARES[1][i] = getEast(i);
            ROOK_SQUARES[2][i] = getSouth(i);
            ROOK_SQUARES[3][i] = getWest(i);
        }

        BISHOP_SQUARES = new byte[4][64][];
        for (int i = 0; i <= 63; i++) {
            BISHOP_SQUARES[0][i] = getNorthEast(i);
            BISHOP_SQUARES[1][i] = getSouthEast(i);
            BISHOP_SQUARES[2][i] = getSouthWest(i);
            BISHOP_SQUARES[3][i] = getNorthWest(i);
        }

        KNIGHT_SQUARES = new byte[64][];
        for (int i = 0; i <= 63; i++) {
            KNIGHT_SQUARES[i] = getL(i);
        }

        WHITE_PAWN_CAPTURE_SQUARES = new byte[64][];
        for (int i = 0; i <= 63; i++) {
            byte pos, pos1, pos2;
            if (inRank('8', i) || inRank('1', i)) {
                WHITE_PAWN_CAPTURE_SQUARES[i] = new byte[0];
            } else if (inFile('A', i)) {
                pos = goNorth(i);
                pos = goEast(pos);
                byte[] POS = {pos};
                WHITE_PAWN_CAPTURE_SQUARES[i] = POS;
            } else if (inFile('H', i)) {
                pos = goNorth(i);
                pos = goWest(pos);
                byte[] POS = {pos};
                WHITE_PAWN_CAPTURE_SQUARES[i] = POS;
            } else {
                pos1 = goEast(goNorth(i));
                pos2 = goWest(goNorth(i));
                byte[] POS = {pos1, pos2};
                WHITE_PAWN_CAPTURE_SQUARES[i] = POS;
            }
        }

        WHITE_PAWN_STRAIGHT_SQUARES = new byte[64][];
        for (int i = 0; i <= 63; i++) {
            byte pos, pos1, pos2;
            if (inRank('8', i) || inRank('1', i)) {
                WHITE_PAWN_STRAIGHT_SQUARES[i] = new byte[0];
            } else if (inRank('2', i)) {
                pos1 = goNorth(i);
                pos2 = goNorth(goNorth(i));
                byte[] POS = {pos1, pos2};
                WHITE_PAWN_STRAIGHT_SQUARES[i] = POS;
            } else {
                pos = goNorth(i);
                byte[] POS = {pos};
                WHITE_PAWN_STRAIGHT_SQUARES[i] = POS;
            }
        }

        BLACK_PAWN_CAPTURE_SQUARES = new byte[64][];
        for (int i = 0; i <= 63; i++) {
            byte pos, pos1, pos2;
            if (inRank('8', i) || inRank('1', i)) {
                BLACK_PAWN_CAPTURE_SQUARES[i] = new byte[0];
            } else if (inFile('A', i)) {
                pos = goSouth(i);
                pos = goEast(pos);
                byte[] POS = {pos};
                BLACK_PAWN_CAPTURE_SQUARES[i] = POS;
            } else if (inFile('H', i)) {
                pos = goSouth(i);
                pos = goWest(pos);
                byte[] POS = {pos};
                BLACK_PAWN_CAPTURE_SQUARES[i] = POS;
            } else {
                pos1 = goEast(goSouth(i));
                pos2 = goWest(goSouth(i));
                byte[] POS = {pos1, pos2};
                BLACK_PAWN_CAPTURE_SQUARES[i] = POS;
            }
        }

        BLACK_PAWN_STRAIGHT_SQUARES = new byte[64][];
        for (int i = 0; i <= 63; i++) {
            byte pos, pos1, pos2;
            if (inRank('8', i) || inRank('1', i)) {
                BLACK_PAWN_STRAIGHT_SQUARES[i] = new byte[0];
            } else if (inRank('7', i)) {
                pos1 = goSouth(i);
                pos2 = goSouth(goSouth(i));
                byte[] POS = {pos1, pos2};
                BLACK_PAWN_STRAIGHT_SQUARES[i] = POS;
            } else {
                pos = goSouth(i);
                byte[] POS = {pos};
                BLACK_PAWN_STRAIGHT_SQUARES[i] = POS;
            }
        }

        QUEEN_SQUARES = new byte[8][64][];
        for (int i = 0; i <= 63; i++) {
            QUEEN_SQUARES[0][i] = getNorth(i);
            QUEEN_SQUARES[1][i] = getEast(i);
            QUEEN_SQUARES[2][i] = getSouth(i);
            QUEEN_SQUARES[3][i] = getWest(i);
            QUEEN_SQUARES[4][i] = getNorthEast(i);
            QUEEN_SQUARES[5][i] = getSouthEast(i);
            QUEEN_SQUARES[6][i] = getSouthWest(i);
            QUEEN_SQUARES[7][i] = getNorthWest(i);
        }

        KING_SQUARES = new byte[64][];
        {
            byte[] POS;
            HashSet<Byte> set = new HashSet<>();
            for (int i = 0; i <= 63; i++) {
                POS = getNorth(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getEast(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getSouth(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getWest(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getNorthEast(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getSouthEast(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getSouthWest(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                POS = getNorthWest(i);
                if (POS.length >= 1) {
                    set.add(POS[0]);
                }
                byte[] temp = new byte[set.size()];
                int j = 0;
                for (byte b : set) {
                    temp[j] = b;
                    j++;
                }
                set.clear();
                KING_SQUARES[i] = temp;
            }
        }

        RANK_DISTANCE = new byte[64][64];
        for (int i = 0; i <= 63; i++) {
            for (int j = 0; j <= 63; j++) {
                RANK_DISTANCE[i][j] = getRankDistance(i,j);
            }
        }

        FILE_DISTANCE = new byte[64][64];
        for (int i = 0; i <= 63; i++) {
            for (int j = 0; j <= 63; j++) {
                FILE_DISTANCE[i][j] = getFileDistance(i,j);
            }
        }

        DIAGONAL_DISTANCE = new byte[64][64];
        for (int i = 0; i <= 63; i++) {
            for (int j = 0; j <= 63; j++) {
                DIAGONAL_DISTANCE[i][j] = 0; // todo
            }
        }

        ANTI_DIAGONAL_DISTANCE = new byte[64][64];
        for (int i = 0; i <= 63; i++) {
            for (int j = 0; j <= 63; j++) {
                ANTI_DIAGONAL_DISTANCE[i][j] = 0; //todo
            }
        }
        System.out.println("CONSTANTS CALCULATED.");
        assert test();
    }

    public Square getSquare(byte pos){
        return allSquares[pos];
    }

    public static boolean getDiagonalDistance(int sq1, int sq2) {
        return true;
    }

    private static byte goNorth(int inp) {
        return (byte) (inp + 8);
    }

    private static byte goEast(int inp) {
        return (byte) (inp + 1);
    }

    private static byte goSouth(int inp) {
        return (byte) (inp - 8);
    }

    private static byte goWest(int inp) {
        return (byte) (inp - 1);
    }

    private static byte[] getNorth(int inp) {
        int size = 7 - inp / 8;
        byte[] outp = new byte[size];
        if (size == 0) return outp;

        byte pos = (byte) inp;
        for (int i = 0; i <= 7; i++) {
            pos = goNorth(pos);
            outp[i] = pos;
            if (inRank('8', pos)) break;
        }

        return outp;
    }

    private static byte[] getEast(int inp) {
        int size = 7 - inp % 8;
        byte[] outp = new byte[size];
        if (size == 0) return outp;

        byte pos = (byte) inp;
        for (int i = 0; i <= 7; i++) {
            pos = goEast(pos);
            outp[i] = pos;
            if (inFile('H', pos)) break;
        }

        return outp;
    }

    private static byte[] getSouth(int inp) {
        int size = inp / 8;
        byte[] outp = new byte[size];
        if (size == 0) return outp;

        byte pos = (byte) inp;
        for (int i = 0; i <= 7; i++) {
            pos = goSouth(pos);
            outp[i] = pos;
            if (inRank('1', pos)) break;
        }

        return outp;
    }

    private static byte[] getWest(int inp) {
        int size = inp % 8;
        byte[] outp = new byte[size];
        if (size == 0) return outp;

        byte pos = (byte) inp;
        for (int i = 0; i <= 7; i++) {
            pos = goWest(pos);
            outp[i] = pos;
            if (inFile('A', pos)) break;
        }

        return outp;
    }

    private static byte[] getNorthEast(int inp) {
        int size = Math.min(7 - inp / 8, 7 - inp % 8);
        byte[] outp = new byte[size];
        if (size == 0) return outp;
        byte newpos = (byte) inp;
        for (byte b = 0; b < size; b++) {
            newpos = goNorth(newpos);
            newpos = goEast(newpos);
            outp[b] = newpos;
        }
        return outp;
    }

    private static byte[] getSouthEast(int inp) {
        int size = Math.min(inp / 8, 7 - inp % 8);
        byte[] outp = new byte[size];
        if (size == 0) return outp;
        byte newpos = (byte) inp;
        for (byte b = 0; b < size; b++) {
            newpos = goSouth(newpos);
            newpos = goEast(newpos);
            outp[b] = newpos;
        }
        return outp;
    }

    private static byte[] getSouthWest(int inp) {
        int size = Math.min(inp / 8, inp % 8);
        byte[] outp = new byte[size];
        if (size == 0) return outp;
        byte newpos = (byte) inp;
        for (byte b = 0; b < size; b++) {
            newpos = goSouth(newpos);
            newpos = goWest(newpos);
            outp[b] = newpos;
        }
        return outp;
    }

    private static byte[] getNorthWest(int inp) {
        int size = Math.min(7 - inp / 8, inp % 8);
        byte[] outp = new byte[size];
        if (size == 0) return outp;
        byte newpos = (byte) inp;
        for (byte b = 0; b < size; b++) {
            newpos = goNorth(newpos);
            newpos = goWest(newpos);
            outp[b] = newpos;
        }
        return outp;
    }

    private static byte[] getL(int inp) {
        byte[] result = new byte[8];
        byte[][] L = {
                {-1, -1, -8}, {-1, -1, 8}, {1, 1, -8}, {1, 1, 8},
                {-8, -8, -1}, {-8, -8, 1}, {8, 8, -1}, {8, 8, 1}};
        int index = 0;
        for (byte[] ll : L) {
            byte r = (byte) inp;
            for (byte l : ll) {
                if (l == -1 && inFile('A', r)) {
                    r = -1;
                    break;
                }
                if (l == -8 && inRank('1', r)) {
                    r = -1;
                    break;
                }
                if (l == 1 && inFile('H', r)) {
                    r = -1;
                    break;
                }
                if (l == 8 && inRank('8', r)) {
                    r = -1;
                    break;
                }
                r += l;
            }
            if (r != -1) {
                result[index] = r;
                index++;
            }
        }
        int size = index;
        byte[] outp = new byte[size];
        if (size >= 0) System.arraycopy(result, 0, outp, 0, size);
        Arrays.sort(outp);
        return outp;
    }

    private static boolean inFile(char file, int inp) {
        if ((int) file - (int) 'A' < 0 | (int) file - (int) 'A' > 7) {
            throw new InputMismatchException("\"" + file + "\"" + " is not a file name.");
        }
        return inp % 8 == (int) file - (int) 'A';
    }

    private static boolean inRank(char rank, int inp) {
        if ((int) rank - (int) '1' < 0 | (int) rank - (int) '1' > 7) {
            throw new InputMismatchException("\"" + rank + "\"" + " is not a rank name.");
        }
        return inp >> 3 == (int) rank - (int) '1';
    }

    public boolean test() {
        assert getFileDistance(Parser.parse("A1"), Parser.parse("A4")) == 3;
        assert getFileDistance(Parser.parse("C2"), Parser.parse("C8")) == 6;
        assert getFileDistance(Parser.parse("A1"), Parser.parse("B4")) == -1;
        assert getFileDistance(Parser.parse("H8"), Parser.parse("H1")) == 7;

        assert getRankDistance(Parser.parse("A7"), Parser.parse("A2")) == -1;
        assert getRankDistance(Parser.parse("A7"), Parser.parse("F7")) == 5;
        assert getRankDistance(Parser.parse("A7"), Parser.parse("G6")) == -1;
        assert getRankDistance(Parser.parse("H7"), Parser.parse("A7")) == 7;

        assert RANK_DISTANCE[0][63] == -1;
        assert RANK_DISTANCE[8][12] == 4;

        assert FILE_DISTANCE[0][16] == 2;
        assert FILE_DISTANCE[7][15] == 1;
        assert FILE_DISTANCE[5][55] == -1;

        System.out.println("CONSTANTS TESTS ENABLED. TESTS OK.");
        return true;
    }

    /**
     * get distance between sq1 and sq2
     * return -1 if sq1 and sq2 are not in same file.
     *
     * @param sq1 Square1 between 0 and 63
     * @param sq2 Square2 between 0 and 63
     * @return Distance between sq1 and sq2
     */
    private byte getFileDistance(int sq1, int sq2) {
        if (sq1 % 8 == sq2 % 8) {
            return (byte) (Math.max(sq1 >> 3, sq2 >> 3) - Math.min(sq1 >> 3, sq2 >> 3));
        } else return -1;
    }

    /**
     * get distance between sq1 and sq2
     * return -1 if sq1 and sq2 are not in same rank.
     *
     * @param sq1 Square1 between 0 and 63
     * @param sq2 Square2 between 0 and 63
     * @return Distance between sq1 and sq2
     */
    private byte getRankDistance(int sq1, int sq2) {
        if (sq1 >> 3 == sq2 >> 3) {
            return (byte) (Math.max(sq1 % 8, sq2 % 8) - Math.min(sq1 % 8, sq2 % 8));
        } else return -1;
    }
}
