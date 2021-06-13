package core;

public interface ChessModel {

    void newGame();

    void startFromFEN(String FEN);

    short getScore();

    char[] getBoard();

    boolean isStarted();

    String getStatusNotice();

    Status getStatus();

    void userRedo();

    void userUndo();

    int userRedoAll();

    boolean userMove(Move move, boolean userColor, boolean userPlaysBothColors);

    Moves getUserLegalMoves(boolean userPlaysColor, boolean userPlaysBothColors);

    boolean pieceAtSquare(int pos, boolean color);

    boolean getTurnColor();

    ChessClock getClock();

    void setTime(int minutes);

    Chess.PieceCollectionBlack getBlackPieces();

    Chess.PieceCollectionWhite getWhitePieces();

    byte[] getBlackThreats();

    byte[] getWhiteThreats();

    byte[] getCombinedThreats();

    History getHistory();
}
