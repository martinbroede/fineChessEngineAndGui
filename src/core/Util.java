package core;

import java.util.HashMap;
import java.util.InputMismatchException;

/** includes all parsing functions */
public final class Util {

    public static final HashMap<Character, Character> SYMBOLS = new HashMap<Character, Character>() {{
        put('K', '\u2654');
        put('Q', '\u2655');
        put('R', '\u2656');
        put('B', '\u2657');
        put('N', '\u2658');
        put('P', '\u2659');
        put('k', '\u265A');
        put('q', '\u265B');
        put('r', '\u265C');
        put('b', '\u265D');
        put('n', '\u265E');
        put('p', '\u265F');
        put(' ', ' ');
    }};

    public static final HashMap<Character, Character> SYMBOL_SCF = new HashMap<Character, Character>() {{
        put('K', 'k');
        put('Q', 'q');
        put('R', 'r');
        put('B', 'b');
        put('N', 'n');
        put('P', 'p');
        put('k', 'l');
        put('q', 'w');
        put('r', 't');
        put('b', 'v');
        put('n', 'm');
        put('p', 'o');
        put(' ', ' ');
    }};

    public static final HashMap<Character, Character> SYMBOL_SARAH = new HashMap<Character, Character>() {{
        put('K', 'E');
        put('Q', 'F');
        put('R', 'D');
        put('B', 'C');
        put('N', 'B');
        put('P', 'A');
        put('k', 'M');
        put('q', 'N');
        put('r', 'L');
        put('b', 'K');
        put('n', 'J');
        put('p', 'I');
        put(' ', ' ');
    }};

    private Util() {
    }

    public static char parseSarahFont(char c) {
        return SYMBOL_SARAH.get(c);
    }

    public static char parseSymbol(char c) {
        return SYMBOLS.get(c);
    }

    public static char parseSymbolFromChessFont(char c) {
        return SYMBOL_SCF.get(c);
    }

    public static byte parse(String inp) {

        if (inp.length() > 2 | inp.length() <= 1) {
            throw new InputMismatchException(inp + " is not a chess coordinate - must have 2 characters.");
        }
        if (inp.getBytes()[0] < (int) 'A' | inp.getBytes()[0] > (int) 'H') {
            throw new InputMismatchException(inp + " is not a chess coordinate - must be between A and H.");
        }
        if (inp.getBytes()[1] < (int) '1' | inp.getBytes()[1] > (int) '8') {
            throw new InputMismatchException(inp + " is not a chess coordinate - must be between 1 and 8.");
        }
        int outp = inp.getBytes()[0] - (int) ('A');
        outp += 8 * (inp.getBytes()[1] - (int) ('1'));
        return (byte) outp;
    }

    public static byte parse(int x, int y) {

        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new InputMismatchException(x + "/" + y + " is out of bounds.");
        }
        return (byte) (x + 8 * y);
    }

    public static String parse(int inp) {

        if ((inp < 0) | (inp > 63)) {
            throw new InputMismatchException(inp + " is not a chess coordinate - must be between 0 and 63.");
        }
        String outp = "" + (char) (inp % 8 + (int) 'A');
        outp += (char) (inp / 8 + (int) '1');
        return outp;
    }

    public static String parseLowerCase(int inp) {

        if ((inp < 0) | (inp > 63)) {
            throw new InputMismatchException(inp + " is not a chess coordinate - must be between 0 and 63.");
        }
        String outp = "" + (char) (inp % 8 + (int) 'a');
        outp += (char) (inp / 8 + (int) '1');
        return outp;
    }

    public static char getFileName(int i) {
        return (char) ((int) 'A' + i);
    }

    public static char getRankName(int i) {
        return (char) ((int) '1' + i);
    }

    //shortcut for 'lower case' (black pieces)
    public static boolean isBlackOccupied(char c) {
        return (byte) c >= 97;
    }

    //shortcut for 'upper case' (white pieces)
    public static boolean isWhiteOccupied(char c) {
        return (byte) c >= 65 && (byte) c <= 90;
    }

    // ' ' : not occupied
    public static boolean isNotOccupied(char c) {
        return c == ' ';
    }

    // ' ' : not occupied
    public static boolean isOccupied(char c) {
        return c != ' ';
    }

    // redundant but improves readability
    public static boolean isBlackPiece(char c) {
        return (byte) c >= 97;
    }

    public static boolean isWhitePiece(char c) {
        return (byte) c >= 65 && (byte) c <= 90;
    }
}