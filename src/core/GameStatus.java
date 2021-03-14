package core;

public class GameStatus {
    public static final int WHITE_RESIGNED = -2;
    public static final int BLACK_CHECKMATED = -1;
    public static final int UNDECIDED = 0;
    public static final int WHITE_CHECKMATED = 1;
    public static final int BLACK_RESIGNED = 2;
    public static final int DRAW_STALEMATE = 10;
    public static final int DRAW_ACCEPTED = 11; //    draw due to an accepted offer
    public static final int DRAW_MOVES = 12; //       draw due to fifty moves rule
    public static final int DRAW_MATERIAL = 13; //    draw due to lack of material on both sides
    public static final int DRAW_REPETITION = 14; //  draw due to repetion of current position

    private int whiteScore;
    private int blackScore;
    private int statusCode;

    public GameStatus() {
        reset();
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public void setWhiteScore(int whiteScore) {
        this.whiteScore = whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public void setBlackScore(int blackScore) {
        this.blackScore = blackScore;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void reset() {
        statusCode = UNDECIDED;
    }

    public String getStatusNotice() {
        switch (statusCode) {
            case BLACK_CHECKMATED:
                return "Weiß gewinnt durch Schachmatt.";
            case UNDECIDED:
                return "Das Spiel ist noch offen";
            case WHITE_CHECKMATED:
                return "Schwarz gewinnt durch Schachmatt.";
            case DRAW_STALEMATE:
                return "Remis nach Patt";
            case DRAW_ACCEPTED:
                return "Remis nach Übereinkunft";
            case DRAW_MATERIAL:
                return "Remis durch Mangel an Material";
            case DRAW_MOVES:
                return "Remis durch Überschreitung der 50-Züge-Regel";
            case DRAW_REPETITION:
                return "Remis durch dreifache Stellungswiederholung";
            case BLACK_RESIGNED:
                return "Weiß gewinnt durch Aufgabe";
            case WHITE_RESIGNED:
                return "Schwarz gewinnt durch Aufgabe";
            default:
                System.err.println("STATUS CODE NOT KNOWN");
                return "NOT KNOWN";
        }
    }
}
