package core;

import java.io.Serializable;
import java.util.Stack;

public class Chess extends MoveGenerator implements Serializable {

    public final String INIT_STANDARD_BOARD = ""
            + "RNBQKBNRPPPPPPPP                "
            + "                pppppppprnbqkbnr";

    private final String init = ""
            + "R   KBNRPPPPPPPPppp             "
            + "             PPPpppppppprnbqk  r";

    public History history;
    public Stack<Move> undoneMovesHistory;
    public GameStatus gameStatus;
    HashGenerator hashGenerator;
    private byte recentEnPassantPawn = -1; // todo enPassant

    public Chess() {

        whitePieces = new PieceCollectionWhite();
        blackPieces = new PieceCollectionBlack();
        castling = new Castling();
        history = new History();
        undoneMovesHistory = new Stack<>();
        hashGenerator = new HashGenerator();
        gameStatus = new GameStatus();

        newGame("K                                                              k", Castling.NO_RIGHTS);
    }

    public void newGame() {
        newGame(INIT_STANDARD_BOARD);
    }

    public void newGame(String init, int castlingRights) {
        newGame(init);
        hashGenerator.hashCastling(Castling.ALL_RIGHTS);
        castling.setRights((byte) castlingRights);
        hashGenerator.hashCastling((byte) castlingRights);
    }

    public void newGame(String init) { //todo new game from FEN

        moveCounter = 0;
        moveCounterLastCaptureOrPawnMove = 0;
        whiteToMove = true;
        castling.reset();
        whitePieces.clear();
        blackPieces.clear();
        history.clear();
        undoneMovesHistory.clear();
        hashGenerator.reset();
        gameStatus.reset();

        for (byte pos = 0; pos <= 63; pos++) {
            char c = (char) (init.getBytes()[pos]);
            board[pos] = c;
            if (is_white_piece(c)) {
                Piece piece = new Piece(pos, c);
                whitePieces.add(piece);
                if (c == 'K') whitePieces.setKing(piece);
            }
            if (is_black_piece(c)) {
                Piece piece = new Piece(pos, c);
                blackPieces.add(piece);
                if (c == 'k') blackPieces.setKing(piece);
            }
        }

        long h = hashGenerator.generateHashCode(board, whiteToMove, castling.getRights(), recentEnPassantPawn);
        hashGenerator.setHashCode(h); //todo unify after tests.
        System.out.println("HASH CALCULATED: " + hashGenerator.getHashCode());
        whitePieces.updateThreats();
        blackPieces.updateThreats();
    }

    public Moves getUserLegalMoves() {

        if (whiteToMove) {

            Moves pseudoMoves = whitePieces.getPseudoLegalMoves();
            Moves illegalMoves = new Moves();
            for (Move pMove : pseudoMoves) {
                movePiece(pMove);
                blackPieces.updateThreats();
                if (blackPieces.getThreats()[whitePieces.king.getPosition()] > 0) {
                    illegalMoves.add(pMove);
                }
                undo();
            }

            pseudoMoves.removeAll(illegalMoves);

            if (pseudoMoves.size() == 0) {
                if (blackPieces.getThreats()[whitePieces.king.getPosition()] > 0)
                    gameStatus.setStatusCode(GameStatus.WHITE_CHECKMATED);
                else
                    gameStatus.setStatusCode(GameStatus.DRAW_STALEMATE);
            }

            return pseudoMoves;

        } else {

            Moves pseudoMoves = blackPieces.getPseudoLegalMoves();
            Moves illegalMoves = new Moves();
            for (Move pMove : pseudoMoves) {
                movePiece(pMove);
                whitePieces.updateThreats();
                if (whitePieces.getThreats()[blackPieces.king.getPosition()] > 0) {
                    illegalMoves.add(pMove);
                }
                undo();
            }

            pseudoMoves.removeAll(illegalMoves);

            if (pseudoMoves.size() == 0) {
                if (whitePieces.getThreats()[blackPieces.king.getPosition()] > 0)
                    gameStatus.setStatusCode(GameStatus.BLACK_CHECKMATED);
                else
                    gameStatus.setStatusCode(GameStatus.DRAW_STALEMATE);
            }

            return pseudoMoves;

        }
    }

    /** checks if legal, if so move piece */
    public boolean movePieceUser(Move move) {
        if (move.getInformation() < 0) System.err.println("MOVE INFO < 0. CAN'T PROCESS HERE.");

        if (!getUserLegalMoves().contains(move)) return false;

        undoneMovesHistory.clear();
        movePiece(move);
        return true;
    }

    /** move piece without check. Computer won't try  illegal moves ;) */
    private void movePiece(Move move) {

        byte from = move.getFrom();
        byte to = move.getTo();
        char typeMovingPiece = board[from];
        char typePromotedPiece = typeMovingPiece;
        char capture = board[to]; //doesn't need to be a capture => can also be ' '

        history.push(new State(hashGenerator.getHashCode(), move.getInformation(), board[to], capture,
                moveCounterLastCaptureOrPawnMove, recentEnPassantPawn, castling.getRights()));

        if (whiteToMove) {
            if (capture != ' ') {
                hashGenerator.hashPosition(capture, to);
                blackPieces.removePiece(to);
                moveCounterLastCaptureOrPawnMove = moveCounter;
            }

            Piece movingPiece = whitePieces.changePosition(from, to);
            board[to] = board[from];
            board[from] = ' ';

            if (castling.getRights() != 0) {

                if ((to == 56) && castling.blackQueenSide()) { // A8 rook captured...?
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableBlackQueenSide();
                    hashGenerator.hashCastling(castling.getRights());
                } else if ((to == 63) && castling.blackKingSide()) { // H8 rook captured...?
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableBlackKingSide();
                    hashGenerator.hashCastling(castling.getRights());
                }

                if (from == 0) { // A1 rook
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableWhiteQueenSide();
                    hashGenerator.hashCastling(castling.getRights());
                } else if (from == 7) { // H1 rook
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableWhiteKingSide();
                    hashGenerator.hashCastling(castling.getRights());
                } else if (from == 4) { //E1 king
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableWhiteKingSide();
                    castling.disableWhiteQueenSide();
                    hashGenerator.hashCastling(castling.getRights());
                }

                if (move.getSpecial() == Move.KING_SIDE_CASTLING) {
                    board[7] = ' '; // H1
                    board[5] = 'R'; // F1
                    whitePieces.changePosition((byte) 7, (byte) 5);
                    hashGenerator.hashPosition('R', (byte) 7); // H1
                    hashGenerator.hashPosition('R', (byte) 5); // F1
                } else if (move.getSpecial() == Move.QUEEN_SIDE_CASTLING) {
                    board[0] = ' '; // A1
                    board[3] = 'R'; // C1
                    whitePieces.changePosition((byte) 0, (byte) 3);
                    hashGenerator.hashPosition('R', (byte) 0); // A1
                    hashGenerator.hashPosition('R', (byte) 3); // C1
                }
            }

            if (move.getSpecial() == Move.PROMOTION_QUEEN) {
                board[to] = 'Q';
                typePromotedPiece = 'Q';
                movingPiece.setPattern(whiteQueenPattern);
            } else if (move.getSpecial() == Move.PROMOTION_BISHOP) {
                board[to] = 'B';
                typePromotedPiece = 'B';
                movingPiece.setPattern(whiteBishopPattern);
            } else if (move.getSpecial() == Move.PROMOTION_KNIGHT) {
                board[to] = 'N';
                typePromotedPiece = 'N';
                movingPiece.setPattern(whiteKnightPattern);
            } else if (move.getSpecial() == Move.PROMOTION_ROOK) {
                board[to] = 'R';
                typePromotedPiece = 'R';
                movingPiece.setPattern(whiteRookPattern);
            }

        } else { // "blackToMove"
            if (capture != ' ') {
                hashGenerator.hashPosition(capture, to);
                whitePieces.removePiece(to);
                moveCounterLastCaptureOrPawnMove = moveCounter;
            }

            Piece movingPiece = blackPieces.changePosition(from, to);
            board[to] = board[from];
            board[from] = ' ';

            if (castling.getRights() != 0) {

                if ((to == 0) && castling.whiteQueenSide()) { // A1 rook
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableWhiteQueenSide();
                    hashGenerator.hashCastling(castling.getRights());
                } else if ((to == 7) && castling.whiteKingSide()) { // H1 rook
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableWhiteKingSide();
                    hashGenerator.hashCastling(castling.getRights());
                }

                if (from == 56) {//A8 rook
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableBlackQueenSide();
                    hashGenerator.hashCastling(castling.getRights());
                } else if (from == 63) {//H8 rook
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableBlackKingSide();
                    hashGenerator.hashCastling(castling.getRights());
                } else if (from == 60) { // E8 king
                    hashGenerator.hashCastling(castling.getRights());
                    castling.disableBlackKingSide();
                    castling.disableBlackQueenSide();
                    hashGenerator.hashCastling(castling.getRights());
                }

                if (move.getSpecial() == Move.KING_SIDE_CASTLING) {
                    board[63] = ' '; // H8
                    board[61] = 'r'; // F8
                    blackPieces.changePosition((byte) 63, (byte) 61);
                    hashGenerator.hashPosition('r', (byte) 63);
                    hashGenerator.hashPosition('r', (byte) 61);
                } else if (move.getSpecial() == Move.QUEEN_SIDE_CASTLING) {
                    board[56] = ' '; // A8
                    board[59] = 'r'; // D8
                    blackPieces.changePosition((byte) 56, (byte) 59);
                    hashGenerator.hashPosition('r', (byte) 56);
                    hashGenerator.hashPosition('r', (byte) 59);
                }
            }

            if (move.getSpecial() == Move.PROMOTION_QUEEN) {
                board[to] = 'q';
                typePromotedPiece = 'q';
                movingPiece.setPattern(blackQueenPattern);
            } else if (move.getSpecial() == Move.PROMOTION_BISHOP) {
                board[to] = 'b';
                typePromotedPiece = 'b';
                movingPiece.setPattern(blackBishopPattern);
            } else if (move.getSpecial() == Move.PROMOTION_KNIGHT) {
                board[to] = 'n';
                typePromotedPiece = 'n';
                movingPiece.setPattern(blackKnightPattern);
            } else if (move.getSpecial() == Move.PROMOTION_ROOK) {
                board[to] = 'r';
                typePromotedPiece = 'r';
                movingPiece.setPattern(blackRookPattern);
            }

        }


        if (whiteToMove) whitePieces.updateThreats();
        else blackPieces.updateThreats();

        hashGenerator.hashPosition(typeMovingPiece, from);
        hashGenerator.hashPosition(typePromotedPiece, to);
        // equal to hashGenerator.hashPosition(typeMovingPiece, to); if no promotion happened!

        whiteToMove = !whiteToMove; // 1st
        moveCounter++;

        hashGenerator.hashTurn(); // 2nd

        assert testHashGenerator();
    }

    public boolean testHashGenerator() {

        long generateHashCode = hashGenerator.generateHashCode(board, whiteToMove, castling.getRights(), recentEnPassantPawn);

        if (generateHashCode != hashGenerator.getHashCode()) {
            System.err.println("HASHES NOT IDENTICAL:");
            System.err.println("GENERATE:  \t\t" + generateHashCode);
            System.err.println("GET:\t\t\t" + hashGenerator.getHashCode());
            return false;
        }
        return true;
    }

    public void userUndo() {

        if (history.size() == 0) return;
        short information = undo();
        Move undone = new Move(information);
        undoneMovesHistory.push(undone);
    }

    public void userRedo() {

        if (undoneMovesHistory.size() == 0) return;
        Move redo = undoneMovesHistory.pop();
        movePiece(redo);
    }

    /**
     * Revoke last move.
     *
     * @return information: Information about the revoked move (see in Move.java)
     */
    public short undo() {

        gameStatus.setStatusCode(GameStatus.UNDECIDED);
        State state = history.pop();
        byte from = Move.getFrom(state.moveInformation);
        byte to = Move.getTo(state.moveInformation);

        if (state.capture != ' ') {
            if (whiteToMove) whitePieces.activateLastCapturedPiece();
            else blackPieces.activateLastCapturedPiece();
        }

        if (whiteToMove) {
            Piece movingPiece = blackPieces.changePosition(to, from);
            if (Move.getSpecial(state.moveInformation) > 0) {
                // special moves: castling, enPassant, promotion
                if (Move.getSpecial(state.moveInformation) == Move.KING_SIDE_CASTLING) {
                    board[63] = 'r'; // H8
                    board[61] = ' '; // F8
                    blackPieces.changePosition((byte) 61, (byte) 63);
                } else if (Move.getSpecial(state.moveInformation) == Move.QUEEN_SIDE_CASTLING) {
                    board[56] = 'r'; // A8
                    board[59] = ' '; // D8
                    blackPieces.changePosition((byte) 59, (byte) 56);
                }
                // PROMOTION MOVES:
                else if ((Move.getSpecial(state.moveInformation) == Move.PROMOTION_QUEEN) ||
                        (Move.getSpecial(state.moveInformation) == Move.PROMOTION_ROOK) ||
                        (Move.getSpecial(state.moveInformation) == Move.PROMOTION_KNIGHT) ||
                        (Move.getSpecial(state.moveInformation) == Move.PROMOTION_BISHOP)) {
                    board[to] = 'p';
                    movingPiece.setPattern(blackPawnPattern);
                }
            }
        } else {
            Piece movingPiece = whitePieces.changePosition(to, from);
            if (Move.getSpecial(state.moveInformation) > 0) {
                if (Move.getSpecial(state.moveInformation) == Move.KING_SIDE_CASTLING) {
                    board[7] = 'R'; // H1
                    board[5] = ' '; // F1
                    whitePieces.changePosition((byte) 5, (byte) 7);
                } else if (Move.getSpecial(state.moveInformation) == Move.QUEEN_SIDE_CASTLING) {
                    board[0] = 'R'; // A1
                    board[3] = ' '; // C1
                    whitePieces.changePosition((byte) 3, (byte) 0);
                }
                // PROMOTION MOVES:
                else if ((Move.getSpecial(state.moveInformation) == Move.PROMOTION_QUEEN) ||
                        (Move.getSpecial(state.moveInformation) == Move.PROMOTION_ROOK) ||
                        (Move.getSpecial(state.moveInformation) == Move.PROMOTION_KNIGHT) ||
                        (Move.getSpecial(state.moveInformation) == Move.PROMOTION_BISHOP)) {
                    board[to] = 'P';
                    movingPiece.setPattern(whitePawnPattern);
                }
            }

        }

        board[from] = board[to];
        board[to] = state.capture;

        whiteToMove = !whiteToMove;
        moveCounter--;
        moveCounterLastCaptureOrPawnMove = state.moveCounterLastCaptureOrPawnMove;
        castling.setRights(state.castling);
        hashGenerator.setHashCode(state.hashCode);

        if (whiteToMove) blackPieces.updateThreats();
        else whitePieces.updateThreats();

        return state.moveInformation;
    }

    public boolean pieceAtSquare(int i, boolean color) {
        if (color == Constants.WHITE) return is_white_piece(board[i]);
        else return is_black_piece(board[i]);
    }

    public char[] getBoard() {
        return board;
    }

    public boolean getTurnColor() {
        return whiteToMove;
    }

    public void printASCII() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        StringBuilder outp = new StringBuilder();
        StringBuilder line = new StringBuilder("|");
        for (int i = 0; i <= 63; i++) {
            line.append(board[i]).append("|");
            if (i % 8 == 7) {
                outp.insert(0, line.toString() + (i / 8 + 1) + "\n");
                line = new StringBuilder("|");
            }
        }
        outp = new StringBuilder("\n" + outp + ".A.B.C.D.E.F.G.H." + "\n");
        return outp.toString();
    }

    static class State {

        long hashCode;
        short moveInformation;
        char pieceType;
        char capture;
        short moveCounterLastCaptureOrPawnMove;
        byte enPassant;
        byte castling;

        public State(long hashCode, short moveInformation, char pieceType, char capture,
                     short lastCapt, byte enPassant, byte castling) {
            this.hashCode = hashCode;
            this.moveInformation = moveInformation;
            this.pieceType = pieceType;
            this.capture = capture;
            this.moveCounterLastCaptureOrPawnMove = lastCapt;
            this.enPassant = enPassant;
            this.castling = castling;
        }

        public void print() {
            System.out.print(Parser.parse(moveInformation) +
                    "CAPTURED: " + capture);
        }
    }

    public class History extends Stack<State> {

        public String toString() {
            StringBuilder outp = new StringBuilder();
            int count = 2;
            for (State state : this) {
                count++;
                if (count % 2 == 1) outp.append(count / 2).append(". ");
                else outp.append(" - ");
                outp.append(Parser.parseSymbol(state.pieceType)).
                        append(" ").
                        append(Parser.parseLowerCase(state.moveInformation % 64)).
                        append(" ").
                        append(Parser.parseLowerCase(state.moveInformation >> 6));
                if (count % 2 == 0) outp.append("\n");
            }
            return outp.toString();
        }

        public short getLastMoveCoordinate() {

            if (this.size() > 0) return this.lastElement().moveInformation;
            return -1;
        }

        public void print() {
            System.out.println(this.toString());
        }
    }
}
