package core;

import java.util.InputMismatchException;

public class PiecePatterns {

    protected final char[] board;
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

        constants = new Constants();
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

        System.out.println("PIECE PATTERNS: DONE.");

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
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED. E11");
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
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED. E12");
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
        throw new InputMismatchException(c + " IS NOT IMPLEMENTED. E13");
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

    public boolean pieceAtSquare(int i) {
        return board[i] != ' ';
    }

    class WhitePawnPattern extends PiecePattern {

        public Moves getMoves(byte from, byte[] threats) {

            Moves legalMoves = new Moves();

            for (byte to : constants.WHITE_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                Move move = new Move(from, to);
                legalMoves.add(move);
            }

            for (byte to : constants.WHITE_PAWN_CAPTURE_SQUARES[from]) {
                if (is_black_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {

            Moves legalMoves = new Moves();

            for (byte to : constants.BLACK_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                Move move = new Move(from, to);
                legalMoves.add(move);
            }

            for (byte to : constants.BLACK_PAWN_CAPTURE_SQUARES[from]) {
                if (is_white_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                if (!is_white_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                if (!is_black_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte to : constants.KING_SQUARES[from]) {
                if (threats[to] == 0) {
                    if (!is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                    }
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

        public Moves getMoves(byte from, byte[] threats) {
            Moves legalMoves = new Moves();

            for (byte to : constants.KING_SQUARES[from]) {
                if (threats[to] == 0) {
                    if (!is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legalMoves.add(move);
                    }
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
