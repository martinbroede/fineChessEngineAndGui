package core;

public enum Status {
    // do not change the order...
    UNDECIDED,
    WHITE_RESIGNED,
    WHITE_CHECKMATED,
    WHITE_TIME_EXCEEDED,
    BLACK_RESIGNED,
    BLACK_CHECKMATED,
    BLACK_TIME_EXCEEDED,
    DRAW_STALEMATE,
    DRAW_ACCEPTED,//    draw due to an accepted offer
    DRAW_MOVES,//       draw due to fifty moves rule
    DRAW_MATERIAL, //   draw due to lack of material on both sides // todo implement
    DRAW_REPETITION;//  draw due to repetition of current position


    private final String[] NOTICE = {
            "Das Spiel ist noch offen",
            "Schwarz gewinnt durch Aufgabe",
            "Schwarz gewinnt durch Schachmatt.",
            "Schwarz gewinnt durch Zeitüberschreitung",
            "Weiß gewinnt durch Aufgabe",
            "Weiß gewinnt durch Schachmatt.",
            "Weiß gewinnt durch Zeitüberschreitung",
            "Remis nach Patt",
            "Remis nach Übereinkunft",
            "Remis durch Überschreitung der 50-Züge-Regel",
            "Remis durch Mangel an Material",
            "Remis durch dreifache Stellungswiederholung",
    };

    public String getNotice() {
        return NOTICE[this.ordinal()];
    }

    public Scoring getResult() {
        switch (this) {
            case UNDECIDED:
                return Scoring.NO_SCORING;
            case WHITE_RESIGNED:
            case WHITE_TIME_EXCEEDED:
            case WHITE_CHECKMATED:
                return Scoring.BLACK_ONE;
            case BLACK_RESIGNED:
            case BLACK_TIME_EXCEEDED:
            case BLACK_CHECKMATED:
                return Scoring.WHITE_ONE;
            default:
                return Scoring.HALF;
        }
    }

    public enum Scoring {
        NO_SCORING,//   not decided
        HALF,//         white 1/2 black 1/2
        WHITE_ONE,//    white 1 black 0
        BLACK_ONE//     black 1 white 0
    }
}