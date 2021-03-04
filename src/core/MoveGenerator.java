package core;


public class MoveGenerator {

    char[] board;
    Constants constants;

    WhitePawn whitePawn;
    WhiteKnight whiteKnight;
    WhiteRook whiteRook;
    WhiteBishop whiteBishop;
    WhiteQueen whiteQueen;
    WhiteKing whiteKing;
    BlackPawn blackPawn;
    BlackKnight blackKnight;
    BlackRook blackRook;
    BlackBishop blackBishop;
    BlackQueen blackQueen;
    BlackKing blackKing;

    public MoveGenerator(char[] board) {

        this.board = board;
        constants = new Constants();

        whitePawn = new WhitePawn();
        whiteKnight = new WhiteKnight();
        blackPawn = new BlackPawn();
        blackKnight = new BlackKnight();
        whiteRook = new WhiteRook();
        blackRook = new BlackRook();
        whiteBishop = new WhiteBishop();
        blackBishop = new BlackBishop();
        whiteQueen = new WhiteQueen();
        blackQueen = new BlackQueen();
        whiteKing = new WhiteKing();
        blackKing = new BlackKing();
        System.out.println("MOVE GENERATOR: DONE."); // todo remove
    }

    public static int createMove(byte from, byte to) {
        return from + 64 * to;
    }

    public Piece getWhitePiece(char c) {
        switch (c) {
            case 'P':
                return whitePawn;
            case 'N':
                return whiteKnight;
            case 'R':
                return whiteRook;
            case 'B':
                return whiteBishop;
            case 'K':
                return whiteKing;
            case 'Q':
                return whiteQueen;
        }
        return null;
    }

    public Piece getBlackPiece(char c) {
        switch (c) {
            case 'p':
                return blackPawn;
            case 'n':
                return blackKnight;
            case 'r':
                return blackRook;
            case 'b':
                return blackBishop;
            case 'k':
                return blackKing;
            case 'q':
                return blackQueen;
        }
        return null;
    }

    public Piece getPiece(char c) {
        switch (c) {
            case 'p':
                return blackPawn;
            case 'n':
                return blackKnight;
            case 'r':
                return blackRook;
            case 'b':
                return blackBishop;
            case 'k':
                return blackKing;
            case 'q':
                return blackQueen;
            case 'P':
                return whitePawn;
            case 'N':
                return whiteKnight;
            case 'R':
                return whiteRook;
            case 'B':
                return whiteBishop;
            case 'K':
                return whiteKing;
            case 'Q':
                return whiteQueen;
        }
        return null;
    }

    //shortcut for 'lower case' (black pieces)
    private boolean is_black_occupied(char c) {
        return (byte) c >= 97;
    }


    //shortcut for 'upper case' (white pieces)
    private boolean is_white_occupied(char c) {
        return (byte) c >= 65 && (byte) c <= 90;
    }

    // ' ' is empty : not occupied
    private boolean is_not_occupied(char c) {
        return c == ' ';
    }

    abstract class Piece {
        public Moves getThreateningMoves(byte from) {
            return getMoves(from);
        }
        abstract Moves getMoves(byte from);
    }

    class WhitePawn extends Piece {

        public Moves getMoves(byte from) {

            Moves legal_moves = new Moves();

            for (byte to : constants.WHITE_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                Move move = new Move(from, to);
                legal_moves.add(move);
            }

            for (byte to : constants.WHITE_PAWN_CAPTURE_SQUARES[from]) {
                if (is_black_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legal_moves.add(move);
                }
            }

            return legal_moves;
        }
    }

    class BlackPawn extends Piece {

        public Moves getMoves(byte from) {

            Moves legal_moves = new Moves();

            for (byte to : constants.BLACK_PAWN_STRAIGHT_SQUARES[from]) {
                if (board[to] != ' ') break;
                Move move = new Move(from, to);
                legal_moves.add(move);
            }

            for (byte to : constants.BLACK_PAWN_CAPTURE_SQUARES[from]) {
                if (is_white_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legal_moves.add(move);
                }
            }

            return legal_moves;
        }
    }

    class WhiteKnight extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                if (!is_white_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legal_moves.add(move);
                }
            }
            return legal_moves;
        }
    }

    class BlackKnight extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte to : constants.KNIGHT_SQUARES[from]) {
                if (!is_black_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legal_moves.add(move);
                }
            }
            return legal_moves;
        }
    }

    class WhiteRook extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                    } else break;
                }
            }
            return legal_moves;
        }
    }

    class BlackRook extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte[][] line : constants.ROOK_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                    } else break;
                }
            }
            return legal_moves;
        }
    }


    public class WhiteBishop extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                    } else break;
                }
            }
            return legal_moves;
        }
    }

    public class BlackBishop extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte[][] line : constants.BISHOP_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                    } else break;
                }
            }
            return legal_moves;
        }
    }

    public class WhiteQueen extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_black_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                    } else break;
                }
            }
            return legal_moves;
        }
    }

    public class BlackQueen extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte[][] line : constants.QUEEN_SQUARES) {
                for (byte to : line[from]) {
                    if (is_white_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                        break;
                    }
                    if (is_not_occupied(board[to])) {
                        Move move = new Move(from, to);
                        legal_moves.add(move);
                    } else break;
                }
            }
            return legal_moves;
        }
    }

    public class WhiteKing extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte to : constants.KING_SQUARES[from]) {
                if (!is_white_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legal_moves.add(move);
                }
            }
            return legal_moves;
        }
    }

    public class BlackKing extends Piece {
        public Moves getMoves(byte from) {
            Moves legal_moves = new Moves();

            for (byte to : constants.KING_SQUARES[from]) {
                if (!is_black_occupied(board[to])) {
                    Move move = new Move(from, to);
                    legal_moves.add(move);
                }
            }
            return legal_moves;
        }
    }
}
