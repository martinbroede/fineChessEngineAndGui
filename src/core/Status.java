package core;

public enum Status {

    UNDECIDED,
    WHITE_RESIGNED,
    WHITE_CHECKMATED,
    BLACK_RESIGNED,
    BLACK_CHECKMATED,
    DRAW_STALEMATE,
    DRAW_ACCEPTED,//    draw due to an accepted offer
    DRAW_MOVES,//       draw due to fifty moves rule
    DRAW_MATERIAL, //   draw due to lack of material on both sides
    DRAW_REPETITION;//  draw due to repetion of current position

    private final static String[] NOTICE = {
            "Das Spiel ist noch offen",
            "Schwarz gewinnt durch Aufgabe",
            "Schwarz gewinnt durch Schachmatt.",
            "Weiß gewinnt durch Aufgabe",
            "Weiß gewinnt durch Schachmatt.",
            "Remis nach Patt",
            "Remis nach Übereinkunft",
            "Remis durch Überschreitung der 50-Züge-Regel",
            "Remis durch Mangel an Material",
            "Remis durch dreifache Stellungswiederholung",
    };

    public String getNotice() {
        return NOTICE[this.ordinal()];
    }
}
