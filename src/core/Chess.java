package core;

import gui.dialogs.DialogMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Stack;

public class Chess extends MoveGenerator implements Serializable {

    public final String INIT_STANDARD_BOARD = ""
            + "RNBQKBNRPPPPPPPP                "
            + "                pppppppprnbqkbnr";
    public History history;
    public Stack<Move> undoneMovesHistory;
    public GameStatus gameStatus;
    HashGenerator hashGenerator;

    {
        whitePieces = new PieceCollectionWhite();
        blackPieces = new PieceCollectionBlack();
        castling = new Castling();
        history = new History();
        undoneMovesHistory = new Stack<>();
        hashGenerator = new HashGenerator();
        gameStatus = new GameStatus();
    }

    public Chess() {
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

    public void newGame(String init) {

        moveCounter = 0;
        lastCaptureOrPawnMove = 0;
        enPassantPawn = -1;
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

        long h = hashGenerator.generateHashCode(board, whiteToMove, castling.getRights(), enPassantPawn);
        hashGenerator.setHashCode(h);
        System.out.println("BOARD HASHED " + hashGenerator.getHashCode());
        whitePieces.updateThreats();
        blackPieces.updateThreats();

    }

    public void startFromFEN(String FEN) {

        whitePieces.clear();
        blackPieces.clear();
        history.clear();
        undoneMovesHistory.clear();
        hashGenerator.reset();
        gameStatus.reset();

        try {
            String[] args = FEN.split(" ");
            whiteToMove = args[1].equals("w");
            castling.setRights(Castling.ALL_RIGHTS);
            if (!args[2].contains("K")) castling.disableWhiteKingSide();
            if (!args[2].contains("Q")) castling.disableWhiteQueenSide();
            if (!args[2].contains("q")) castling.disableBlackQueenSide();
            if (!args[2].contains("k")) castling.disableBlackKingSide();

            enPassantPawn = -1;
            //todo get en Passant from FEN

            lastCaptureOrPawnMove = Short.parseShort(args[4]);
            moveCounter = (short) (Short.parseShort(args[5]) - 1);

            String init = args[0]
                    .replaceAll("8", "        ")
                    .replaceAll("7", "       ")
                    .replaceAll("6", "      ")
                    .replaceAll("5", "     ")
                    .replaceAll("4", "    ")
                    .replaceAll("3", "   ")
                    .replaceAll("2", "  ")
                    .replaceAll("1", " ")
                    .replaceAll("/", "");

            for (byte pos = 0; pos <= 63; pos++) {
                char c = init.toCharArray()[63 - pos];
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

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("FEN NOT ACCEPTED");
            new DialogMessage("FEN wurde nicht akzeptiert");
            return;
        }

        long h = hashGenerator.generateHashCode(board, whiteToMove, castling.getRights(), enPassantPawn);
        hashGenerator.setHashCode(h);
        System.out.println("BOARD HASHED " + hashGenerator.getHashCode());
        whitePieces.updateThreats();
        blackPieces.updateThreats();

        /*castling.print();
        System.out.println("MOVE COUNTER:" + moveCounter + "/LAST CAPTURE: " + lastCaptureOrPawnMove);*/
    }

    public Moves getUserLegalMoves(boolean userColor, boolean userPlaysBothColors) {

        return getPseudoLegalMoves(); //todo delete line and uncomment below

        /*if (userPlaysBothColors) return getUserLegalMoves();
        else if (whiteToMove == userColor) return getUserLegalMoves();
        else return new Moves();*/
    }

    /** return fully legal moves. Also detect Checkmate, Stalemate */
    private Moves getUserLegalMoves() {

        Moves pseudoMoves;
        Moves illegalMoves = new Moves();

        if (whiteToMove) {

            pseudoMoves = whitePieces.getPseudoLegalMoves();
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

        } else {

            pseudoMoves = blackPieces.getPseudoLegalMoves();
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

        }
        return pseudoMoves;
    }

    /** check if legal, if so move piece */
    public boolean userMove(Move move, boolean userColor, boolean userPlaysBoth) {

        if (gameStatus.getStatusCode() != GameStatus.UNDECIDED) return false;

        if (move.getInformation() < 0) {
            if (move.getInformation() == Move.RESIGN) {
                if (whiteToMove) {
                    gameStatus.setStatusCode(GameStatus.WHITE_RESIGNED);
                } else {
                    gameStatus.setStatusCode(GameStatus.BLACK_RESIGNED);
                }
                return true;

            } else if (move.getInformation() == Move.OFFER_DRAW) {
                gameStatus.setDrawOffered(true);
                return true;

            } else if (move.getInformation() == Move.ACCEPT_DRAW) {
                if (gameStatus.getDrawOffered()) {
                    gameStatus.setStatusCode(GameStatus.DRAW_ACCEPTED);
                    return true;
                }
                return false; // nothing to accept if not offered

            } else if (move.getInformation() == Move.DECLINE_DRAW) {
                gameStatus.setDrawOffered(false);
                return true;

            }
        } else if (!getUserLegalMoves(userColor, userPlaysBoth).contains(move)) {
            System.out.print("USER MOVE" + move + " IS ILLEGAL (CHESS) / ");
            return false;
        }
        gameStatus.setDrawOffered(false);
        undoneMovesHistory.clear();
        movePiece(move);
        //System.out.println("LAST CAPTURE OR PAWN MOVE:" + (moveCounter - lastCaptureOrPawnMove)); //todo remove
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
                lastCaptureOrPawnMove, enPassantPawn, castling.getRights()));

        if (whiteToMove) {
            if (capture != ' ') {
                hashGenerator.hashPosition(capture, to);
                blackPieces.removePiece(to);
                lastCaptureOrPawnMove = moveCounter;
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

            hashGenerator.hashEnPassant(enPassantPawn);
            enPassantPawn = -1;

            if (typeMovingPiece == 'P') {

                lastCaptureOrPawnMove = moveCounter;

                if (to - from == 16) {
                    enPassantPawn = (byte) (to % 8);
                    hashGenerator.hashEnPassant(enPassantPawn);
                } else if (move.getSpecial() == Move.PROMOTION_QUEEN) {
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
                } else if (move.getSpecial() == Move.EN_PASSANT) {
                    board[to - 8] = ' ';
                    hashGenerator.hashPosition('p', (byte) (to - 8));
                    blackPieces.removePiece((byte) (to - 8));
                }
            }
        } else { // "blackToMove"
            if (capture != ' ') {
                hashGenerator.hashPosition(capture, to);
                whitePieces.removePiece(to);
                lastCaptureOrPawnMove = moveCounter;
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

            hashGenerator.hashEnPassant(enPassantPawn);
            enPassantPawn = -1;

            if (typeMovingPiece == 'p') {

                lastCaptureOrPawnMove = moveCounter;

                if (from - to == 16) {
                    enPassantPawn = (byte) (to % 8);
                    hashGenerator.hashEnPassant(enPassantPawn);
                } else if (move.getSpecial() == Move.PROMOTION_QUEEN) {
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
                } else if (move.getSpecial() == Move.EN_PASSANT) {
                    board[to + 8] = ' ';
                    hashGenerator.hashPosition('P', (byte) (to + 8));
                    whitePieces.removePiece((byte) (to + 8));
                }
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

        if (history.drawDueToRepetition(new Long(hashGenerator.getHashCode()))) //todo redundant hashCode storage. (long and Long) only need Long.
            gameStatus.setStatusCode(GameStatus.DRAW_REPETITION);

        if (moveCounter - lastCaptureOrPawnMove >= Constants.MAX_MOVES)
            gameStatus.setStatusCode(GameStatus.DRAW_MOVES);

        assert testHashGenerator();
    }

    public boolean testHashGenerator() {

        long generateHashCode = hashGenerator.generateHashCode(board, whiteToMove, castling.getRights(), enPassantPawn);

        if (generateHashCode != hashGenerator.getHashCode()) {
            System.err.println("HASHES NOT IDENTICAL:");
            System.err.println("GENERATE:  \t\t" + generateHashCode);
            System.err.println("GET:\t\t\t" + hashGenerator.getHashCode());
            return false;
        }
        return true;
    }

    public void userUndo() {

        if (history.size() <= 0) return;
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
     * @return information: Information about the revoked move (see Move.java)
     */
    private short undo() {

        State state = history.pop();
        gameStatus.setStatusCode(GameStatus.UNDECIDED);
        enPassantPawn = state.enPassant;
        byte from = Move.getFrom(state.moveInformation);
        byte to = Move.getTo(state.moveInformation);

        if (state.capture != ' ') {
            if (whiteToMove) whitePieces.activateLastCapturedPiece(); //todo unify with "if" below
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
                } else if ((Move.getSpecial(state.moveInformation) == Move.EN_PASSANT)) {
                    board[to + 8] = 'P';
                    state.capture = ' ';
                    hashGenerator.hashPosition('P', (byte) (to + 8));
                    whitePieces.activateLastCapturedPiece();
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
                } else if ((Move.getSpecial(state.moveInformation) == Move.EN_PASSANT)) {
                    board[to - 8] = 'p';
                    state.capture = ' ';
                    hashGenerator.hashPosition('p', (byte) (to - 8));
                    blackPieces.activateLastCapturedPiece();
                }
            }

        }

        board[from] = board[to];
        board[to] = state.capture;

        whiteToMove = !whiteToMove;
        moveCounter--;
        lastCaptureOrPawnMove = state.moveCounterLastCaptureOrPawnMove;
        castling.setRights(state.castling);
        hashGenerator.setHashCode(state.hashCode);

        if (whiteToMove) blackPieces.updateThreats();
        else whitePieces.updateThreats();

        return state.moveInformation;
    }

    public byte[] getWhiteThreats() {
        whitePieces.updateThreats();
        return whitePieces.getThreats();
    }

    public byte[] getBlackThreats() {
        blackPieces.updateThreats();
        return blackPieces.getThreats();
    }

    public byte[] getCombinedThreats() {
        whitePieces.updateThreats();
        blackPieces.updateThreats();
        byte[] combinedThreats = new byte[64];
        for (int i = 0; i <= 63; i++) {
            combinedThreats[i] += whitePieces.getThreats()[i];
            combinedThreats[i] -= blackPieces.getThreats()[i];
        }
        for (int i = 0; i <= 63; i++) {
            if ((combinedThreats[i] == 0) && (whitePieces.getThreats()[i] != 0)) //threats balanced
                combinedThreats[i] = Byte.MAX_VALUE;
        }
        return combinedThreats;

        /*String outp = "";
        String line = "|";
        for (int i = 0; i <= 63; i++) {
            if (combinedThreats[i] >= 0) line += " ";
            line += combinedThreats[i] + "|";
            if (i % 8 == 7) {
                outp = line + " " + (i / 8 + 1) + "\n" + outp;
                line = "|";
            }
        }
        outp = outp + "| A. B. C. D. E. F. G. H|" + "\n";

        System.out.println(outp);*/
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

    public void print() {
        System.out.println(this.toString());
    }

    static class State {

        final long hashCode;
        final Long hashCodeLong;
        final short moveInformation;
        final short moveCounterLastCaptureOrPawnMove;
        final byte enPassant;
        final byte castling;
        final char pieceType;
        char capture;

        public State(long hashCode, short moveInformation, char pieceType, char capture,
                     short lastCapt, byte enPassant, byte castling) {
            this.hashCode = hashCode;
            this.moveInformation = moveInformation;
            this.pieceType = pieceType;
            this.capture = capture;
            this.moveCounterLastCaptureOrPawnMove = lastCapt;
            this.enPassant = enPassant;
            this.castling = castling;
            this.hashCodeLong = hashCode;
        }
    }

    public class History extends Stack<State> {

        private final Byte FIRST = 1;
        private final Byte SECOND = 2;
        private final Byte THIRD = 3;
        HashMap<Long, Byte> repetitionCountMap = new HashMap<>();
        Byte repetitionCount;

        public short getLastMoveCoordinates() {

            if (this.size() > 0) return this.lastElement().moveInformation;
            return -1;
        }

        public boolean drawDueToRepetition(Long hash) {
            if (repetitionCountMap.get(hash) == SECOND)
                return true;
            return false;
        }

        @Override
        public boolean add(State state) {

            System.err.println("PLEASE DON'T USE ADD WITH A STACK");
            return super.add(state);
        }

        @Override
        public void clear() {

            repetitionCountMap.clear();
            super.clear();
        }

        @Override
        public State push(State state) {

            repetitionCount = repetitionCountMap.get(state.hashCodeLong);
            //todo keep in mind even "Long" provides only "int", i.e. 32bit hashcode...

            if (repetitionCount == null) {
                //System.out.println("HASH 0 => 1"); // todo remove
                repetitionCountMap.put(state.hashCodeLong, FIRST);

            } else if (repetitionCount == FIRST) {
                //System.out.println("HASH 1 => 2"); //todo remove
                repetitionCountMap.put(state.hashCodeLong, SECOND);

            } else if (repetitionCount == SECOND) {
                //System.out.println("HASH 2 => 3"); //todo remove
                repetitionCountMap.put(state.hashCodeLong, THIRD);

            } else {
                System.err.println("SOMETHING EVIL HAPPENED HERE");
            }

            return super.push(state);
        }

        @Override
        public State pop() {

            State state = super.pop();
            repetitionCount = repetitionCountMap.get(state.hashCodeLong);

            if (repetitionCount == THIRD) {
                //System.out.println("HASH 3 => 2"); // todo remove
                repetitionCountMap.put(state.hashCodeLong, SECOND);

            } else if (repetitionCount == SECOND) {
                //System.out.println("HASH 2 => 1"); //todo remove
                repetitionCountMap.put(state.hashCodeLong, FIRST);

            } else if (repetitionCount == FIRST) {
                repetitionCountMap.remove(state.hashCodeLong);

            } else {
                System.err.println("THAT'S EVIL - HASH NOT IN HISTORY");
            }
            return state;
        }


        @Override
        public String toString() {

            StringBuilder outp = new StringBuilder();
            int count = 2;
            for (State state : this) {
                count++;
                if (count % 2 == 1) outp.append(count / 2).append(". ");
                else outp.append(" - ");
                outp.append(Util.parseSymbol(state.pieceType)).
                        append(" ").
                        append(Util.parseLowerCase(state.moveInformation % 64)).
                        append(" ").
                        append(Util.parseLowerCase(state.moveInformation >> 6));
                if (count % 2 == 0) outp.append("\n");
            }
            return outp.toString();
        }
    }
}
