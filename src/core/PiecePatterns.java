package core;

import java.util.InputMismatchException;

public class PiecePatterns {

    final char[] board;
    Constants constants;

    WhitePawnPattern whitePawnPattern;
    WhiteKnightPattern whiteKnightPattern;
    WhiteRookPattern whiteRookPattern;
    WhiteBishopPattern whiteBishopPattern;
    WhiteQueenPattern whiteQueenPattern;
    WhiteKingPattern whiteKingPattern;
    BlackPawnPattern blackPawnPattern;
    BlackKnightPattern blackKnightPattern;
    BlackRookPattern blackRookPattern;
    BlackBishopPattern blackBishopPattern;
    BlackQueenPattern blackQueenPattern;
    BlackKingPattern blackKingPattern;


    public PiecePatterns() {

        constants = new Constants(); //precalculate moves
        board = new char[64];

        whitePawnPattern = new WhitePawnPattern();
        blackPawnPattern = new BlackPawnPattern();
        whiteKnightPattern = new WhiteKnightPattern();
        blackKnightPattern = new BlackKnightPattern();
        whiteRookPattern = new WhiteRookPattern();
        blackRookPattern = new BlackRookPattern();
        whiteBishopPattern = new WhiteBishopPattern();
        blackBishopPattern = new BlackBishopPattern();
        whiteQueenPattern = new WhiteQueenPattern();
        blackQueenPattern = new BlackQueenPattern();
        whiteKingPattern = new WhiteKingPattern();
        blackKingPattern = new BlackKingPattern();

        System.out.println("CHESS PIECES INITIALIZED");
    }

    public PiecePattern getWhitePiecePattern(char c) {

        switch (c) {
            case 'P':
                return whitePawnPattern;
            case 'N':
                return whiteKnightPattern;
            case 'R':
                return whiteRookPattern;
            case 'B':
                return whiteBishopPattern;
            case 'K':
                return whiteKingPattern;
            case 'Q':
                return whiteQueenPattern;
        }
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED IN WHITE PIECE PATTERNS");
    }

    public PiecePattern getBlackPiecePattern(char c) {

        switch (c) {
            case 'p':
                return blackPawnPattern;
            case 'n':
                return blackKnightPattern;
            case 'r':
                return blackRookPattern;
            case 'b':
                return blackBishopPattern;
            case 'k':
                return blackKingPattern;
            case 'q':
                return blackQueenPattern;
        }
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED IN BLACK PIECE PATTERNS");
    }

    public PiecePattern getPiecePattern(char c) {

        switch (c) {
            case 'p':
                return blackPawnPattern;
            case 'n':
                return blackKnightPattern;
            case 'r':
                return blackRookPattern;
            case 'b':
                return blackBishopPattern;
            case 'k':
                return blackKingPattern;
            case 'q':
                return blackQueenPattern;
            case 'P':
                return whitePawnPattern;
            case 'N':
                return whiteKnightPattern;
            case 'R':
                return whiteRookPattern;
            case 'B':
                return whiteBishopPattern;
            case 'K':
                return whiteKingPattern;
            case 'Q':
                return whiteQueenPattern;
        }
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED");
    }

    //shortcut for 'lower case' (black pieces)
    private boolean is_black_occupied(char c) {
        return (byte) c >= 97;
    }

    //shortcut for 'upper case' (white pieces)
    private boolean is_white_occupied(char c) {
        return (byte) c >= 65 && (byte) c <= 90;
    }

    // ' ' : not occupied
    private boolean is_not_occupied(char c) {
        return c == ' ';
    }

    // ' ' : not occupied
    private boolean is_occupied(char c) {
        return c != ' ';
    }

    /* redundant but improves readability */
    public boolean is_black_piece(char c) {
        return (byte) c >= 97;
    }

    public boolean is_white_piece(char c) {
        return (byte) c >= 65 && (byte) c <= 90;
    }

    class WhitePawnPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : constants.WHITE_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                if (to / 8 == 7) {// if getFile(to) == 7 : PROMOTION MOVE
                    legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
            }

            for (byte to : constants.WHITE_PAWN_CAPTURE_SQUARES[from]) {
                if (is_black_occupied(board[to])) {
                    if (to / 8 == 7) {// if getFile(to) == 7 : PROMOTION MOVE
                        legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                    } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
                }
            }

            if(enPassantRights >= 0){

                byte to = constants.WHITE_PAWN_EN_PASSANT_CAPTURE[enPassantRights][from];
                if (to >= 0) {
                    legalMoves.add(new Move(from, to, Move.EN_PASSANT));
                }
            }

            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : constants.WHITE_PAWN_CAPTURE_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    class BlackPawnPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : constants.BLACK_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                if (to / 8 == 0) {// if getFile(to) == 0 : PROMOTION MOVE
                    legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                    legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
            }

            for (byte to : constants.BLACK_PAWN_CAPTURE_SQUARES[from]) {
                if (is_white_occupied(board[to])) {
                    if (to / 8 == 0) {// if getFile(to) == 0 : PROMOTION MOVE
                        legalMoves.add(new Move(from, to, Move.PROMOTION_QUEEN));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_KNIGHT));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_BISHOP));
                        legalMoves.add(new Move(from, to, Move.PROMOTION_ROOK));
                    } else legalMoves.add(new Move(from, to)); // REGULAR MOVE
                }
            }

            if(enPassantRights >= 0){

                byte to = constants.BLACK_PAWN_EN_PASSANT_CAPTURE[enPassantRights][from];
                if (to >= 0) {
                    legalMoves.add(new Move(from,to, Move.EN_PASSANT));
                }
            }

            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : constants.BLACK_PAWN_CAPTURE_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    class WhiteKnightPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                if (!is_white_occupied(board[to])) {
                    legalMoves.add(new Move(from, to));
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    class BlackKnightPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                if (!is_black_occupied(board[to])) {
                    legalMoves.add(new Move(from, to));
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    class WhiteRookPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_occupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    class BlackRookPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_occupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public class WhiteBishopPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_occupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public class BlackBishopPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_occupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public class WhiteQueenPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : constants.QUEEN_SQUARES) {

                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_occupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public class BlackQueenPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {

            Moves legalMoves = new Moves();

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        legalMoves.add(new Move(from, to));
                    } else break;
                }
            }
            return legalMoves;
        }

        public void updateThreats(byte from, byte[] threats) {

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_occupied(board[to])) {
                        threats[to]++;
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        threats[to]++;
                    }
                }
            }
        }
    }

    public class WhiteKingPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {
            return new Moves(); //todo optimize...
        }

        public Moves getKingMoves(byte from, byte[] threats, boolean kingSideCastling, boolean queenSideCastling) {

            Moves legalMoves = new Moves();

            for (byte to : constants.KING_SQUARES[from]) {
                if (threats[to] == 0) {
                    if (!is_white_occupied(board[to])) {
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

            for (byte to : constants.KING_SQUARES[from]) {
                threats[to]++;
            }
        }
    }

    public class BlackKingPattern extends PiecePattern {

        public Moves getMoves(byte from, byte enPassantRights) {
            return new Moves();//todo optimize...
        }

        public Moves getKingMoves(byte from, byte[] threats, boolean kingSideCastling, boolean queenSideCastling) {

            Moves legalMoves = new Moves();

            for (byte to : constants.KING_SQUARES[from]) {
                if (threats[to] == 0) {
                    if (!is_black_occupied(board[to])) {
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

            for (byte to : constants.KING_SQUARES[from]) {
                threats[to]++;
            }
        }
    }
}
