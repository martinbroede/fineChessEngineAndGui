package core;

import java.util.HashMap;
import java.util.InputMismatchException;

import static core.Util.*;

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
        System.out.println("PIECE IS NOT KING. CAN NOT RETURN KingMoves");
        return new Moves();
    }
}

public class PiecePatterns {

    final static PiecePattern WHITE_PAWN = new WhitePawnPattern();
    final static PiecePattern BLACK_PAWN = new BlackPawnPattern();
    final static PiecePattern WHITE_KNIGHT = new WhiteKnightPattern();
    final static PiecePattern BLACK_KNIGHT = new BlackKnightPattern();
    final static PiecePattern WHITE_ROOK = new WhiteRookPattern();
    final static PiecePattern BLACK_ROOK = new BlackRookPattern();
    final static PiecePattern WHITE_BISHOP = new WhiteBishopPattern();
    final static PiecePattern BLACK_BISHOP = new BlackBishopPattern();
    final static PiecePattern WHITE_QUEEN = new WhiteQueenPattern();
    final static PiecePattern BLACK_QUEEN = new BlackQueenPattern();
    final static PiecePattern WHITE_KING = new WhiteKingPattern();
    final static PiecePattern BLACK_KING = new BlackKingPattern();
    final static HashMap<PiecePattern, Short> PIECE_VALUES = new HashMap<PiecePattern, Short>() {{
        /*      910*9 + 530*2 + 330*2 + 320*2 = 10550 = MAX from Pieces.
        => MAX_TOTAL = PIECE_VALUES + KING VALUE = 21101 / Short.MAX_VALUE is 32767 > 21101
        PIECE_VALUES seem to be reasonable...?      */
        put(WHITE_KING, (short) 10551);
        put(WHITE_QUEEN, (short) 910);
        put(WHITE_ROOK, (short) 530);
        put(WHITE_BISHOP, (short) 330);
        put(WHITE_KNIGHT, (short) 320);
        put(WHITE_PAWN, (short) 100);
        put(BLACK_KING, (short) (-10551));
        put(BLACK_QUEEN, (short) (-910));
        put(BLACK_ROOK, (short) (-530));
        put(BLACK_BISHOP, (short) (-330));
        put(BLACK_KNIGHT, (short) (-320));
        put(BLACK_PAWN, (short) (-100));
    }};
    private static final char[] board = new char[64];

    static {
        Constants.precalculateMoves();
    }

    private PiecePatterns() {
    }

    public static char[] getBoard() {
        return board;
    }

    public static PiecePattern getPiecePattern(char c) {

        switch (c) {
            case 'p':
                return BLACK_PAWN;
            case 'n':
                return BLACK_KNIGHT;
            case 'r':
                return BLACK_ROOK;
            case 'b':
                return BLACK_BISHOP;
            case 'k':
                return BLACK_KING;
            case 'q':
                return BLACK_QUEEN;
            case 'P':
                return WHITE_PAWN;
            case 'N':
                return WHITE_KNIGHT;
            case 'R':
                return WHITE_ROOK;
            case 'B':
                return WHITE_BISHOP;
            case 'K':
                return WHITE_KING;
            case 'Q':
                return WHITE_QUEEN;
        }
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED");
    }

    static class WhitePawnPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : Constants.WHITE_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                if (to / 8 == 7) {// if getFile(to) == 7 : PROMOTION MOVE
                    legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
            }

            for (byte to : Constants.WHITE_PAWN_CAPTURE_SQUARES[from]) {
                if (isBlackOccupied(board[to])) {
                    if (to / 8 == 7) {// if getFile(to) == 7 : PROMOTION MOVE
                        legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                    } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
                }
            }

            if (enPassantRights >= 0) {

                byte to = Constants.WHITE_PAWN_EN_PASSANT_CAPTURE[enPassantRights][from];
                if (to >= 0) {
                    legalMoves.add(new Move(from, to, Move.EN_PASSANT));
                }
            }

            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : Constants.WHITE_PAWN_CAPTURE_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    static class BlackPawnPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : Constants.BLACK_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                if (to / 8 == 0) {// if getFile(to) == 0 : PROMOTION MOVE
                    legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
            }

            for (byte to : Constants.BLACK_PAWN_CAPTURE_SQUARES[from]) {
                if (isWhiteOccupied(board[to])) {
                    if (to / 8 == 0) {// if getFile(to) == 0 : PROMOTION MOVE
                        legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                    } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
                }
            }

            if (enPassantRights >= 0) {

                byte to = Constants.BLACK_PAWN_EN_PASSANT_CAPTURE[enPassantRights][from];
                if (to >= 0) {
                    legalMoves.add(new Move(from, to, Move.EN_PASSANT));
                }
            }

            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : Constants.BLACK_PAWN_CAPTURE_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    static class WhiteKnightPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : Constants.KNIGHT_SQUARES[from]) {
                if (!isWhiteOccupied(board[to])) {
                    legalMoves.add(new Move(from, to));
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : Constants.KNIGHT_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    static class BlackKnightPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : Constants.KNIGHT_SQUARES[from]) {
                if (!isBlackOccupied(board[to])) {
                    legalMoves.add(new Move(from, to));
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : Constants.KNIGHT_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    static class WhiteRookPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : Constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (isBlackOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : Constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (isOccupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    static class BlackRookPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : Constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (isWhiteOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : Constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (isOccupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public static class WhiteBishopPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : Constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (isBlackOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : Constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (isOccupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public static class BlackBishopPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : Constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (isWhiteOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : Constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (isOccupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public static class WhiteQueenPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : Constants.QUEEN_SQUARES) {

                for (byte to : line[from]) {
                    if (isBlackOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : Constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (isOccupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public static class BlackQueenPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : Constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (isWhiteOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : Constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (isOccupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (isNotOccupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public static class WhiteKingPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {
            return new Moves(); //todo optimize...
        }

        public Moves getKingMoves(byte from, byte[] threats, boolean kingSideCastling, boolean queenSideCastling) {

            Moves legalMoves = new Moves();

            for (byte to : Constants.KING_SQUARES[from]) {
                if (threats[to] == 0) {
                    if (!isWhiteOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    }
                }
            }

            if (kingSideCastling) {
                //no threats at H1 G1 F1 E1 ? :
                if ((threats[4] == 0) && (threats[5] == 0) && (threats[6] == 0) && (threats[7] == 0)) {
                    if ((board[5] == ' ') && (board[6] == ' ')) //check if occupied
                        legalMoves.add(new Move(from, (byte) 6, Move.KING_SIDE_CASTLING)); //goto G1
                }
            }

            if (queenSideCastling) {
                //no threats at A1 C1 D1 E1 ? :
                if ((threats[0] == 0) && (threats[2] == 0) && (threats[3] == 0) && (threats[4] == 0)) {
                    if ((board[1] == ' ') && (board[2] == ' ') && (board[3] == ' ')) // check if occupied
                        legalMoves.add(new Move(from, (byte) 2, Move.QUEEN_SIDE_CASTLING)); //goto C3
                }
            }

            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : Constants.KING_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    public static class BlackKingPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {
            return new Moves(); //todo optimize...
        }

        public Moves getKingMoves(byte from, byte[] threats, boolean kingSideCastling, boolean queenSideCastling) {

            Moves legalMoves = new Moves();

            for (byte to : Constants.KING_SQUARES[from]) {
                if (threats[to] == 0) {
                    if (!isBlackOccupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    }
                }
            }

            if (kingSideCastling) {
                //no threats at H8 G8 F8 E8 ? :
                if ((threats[63] == 0) && (threats[62] == 0) && (threats[61] == 0) && (threats[60] == 0)) {
                    if ((board[62] == ' ') && (board[61] == ' ')) // check if occupied
                        legalMoves.add(new Move(from, (byte) 62, Move.KING_SIDE_CASTLING)); //goto G8
                }
            }

            if (queenSideCastling) {
                //no threats at E8 D8 C8 A8 ? :
                if ((threats[60] == 0) && (threats[59] == 0) && (threats[58] == 0) && (threats[56] == 0)) {
                    if ((board[59] == ' ') && (board[58] == ' ') && (board[57] == ' ')) // check if occupied
                        legalMoves.add(new Move(from, (byte) 58, Move.QUEEN_SIDE_CASTLING)); //goto C8
                }
            }

            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : Constants.KING_SQUARES[from]) {
                threats[to]++;
            }
        }
    }
}
