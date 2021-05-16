package core;


/**
 * Provides all the moves a piece is allowed to make.
 * Moves are pseudo legal, i.e. the king could be left in check.
 */
abstract class PiecePattern {

    /**
     * Get pseudo legal moves.
     *
     * @param from piece position
     * @return Moves
     */
    abstract Moves getMoves(byte from, byte enPassantRights);

    /**
     * Update squares the piece is threatening.
     *
     * @param from    piece position
     * @param threats byte[] threats. E.g. if the piece threatens H8, the array threats at 63==H8 will increase.
     */
    abstract void updateThreats(byte from, byte[] threats);

    /**
     * Get legal moves of king
     * To be overwritten by king's pattern
     *
     * @param from              piece position
     * @param threats           threats
     * @param KingSideCastling  right of castling
     * @param QueenSideCastling right of castling
     * @return Moves
     */
    public Moves getKingMoves(byte from, byte[] threats, boolean KingSideCastling, boolean QueenSideCastling) {
        System.err.println("PIECE IS NOT KING. CAN NOT RETURN KingMoves");
        return new Moves();
    }
}
