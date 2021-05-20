package core;

import gui.dialogs.DialogMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Stack;

import static core.PiecePatterns.*;
import static core.Util.isBlackPiece;
import static core.Util.isWhitePiece;

public class Chess {

    public final ChessClock clock;
    public final char[] board = PiecePatterns.getBoard();
    public final History history;
    public final Stack<Move> undoneMovesHistory;
    public final GameStatus gameStatus;
    private final HashGenerator hashGenerator;
    public PieceCollectionWhite whitePieces;
    public PieceCollectionBlack blackPieces;
    public Castling castling;
    protected boolean whiteToMove;
    protected short moveCounter;
    protected short lastCaptureOrPawnMove;
    protected byte enPassantPawn; // the file number ('A' = 0) in which the pawn to be captured is located
    private short score;

    {
        whitePieces = new PieceCollectionWhite();
        blackPieces = new PieceCollectionBlack();
        castling = new Castling();
        history = new History();
        undoneMovesHistory = new Stack<>();
        hashGenerator = new HashGenerator();
        gameStatus = new GameStatus();
        clock = new ChessClock(this);
    }

    public Chess() {
        newGame();
    }

    public void newGame() {
        String INIT_STANDARD_BOARD = ""
                + "RNBQKBNRPPPPPPPP                "
                + "                pppppppprnbqkbnr";
        newGame(INIT_STANDARD_BOARD);
    }

    public void newGame(String init, int castlingRights) {

        newGame(init);
        hashGenerator.hashCastling(Castling.ALL_RIGHTS);
        castling.setRights((byte) castlingRights);
        hashGenerator.hashCastling((byte) castlingRights);
        score = calculateScore();
        clock.initialize();
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

        char[] initArray = init.toCharArray();
        for (byte pos = 0; pos <= 63; pos++) {
            char c = initArray[pos];
            board[pos] = c;
            if (isWhitePiece(c)) {
                Piece piece = new Piece(pos, c);
                whitePieces.add(piece);
                if (c == 'K') whitePieces.setKing(piece);
            }
            if (isBlackPiece(c)) {
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
        score = calculateScore();
        clock.initialize();
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
                    .replace("8", "        ")
                    .replace("7", "       ")
                    .replace("6", "      ")
                    .replace("5", "     ")
                    .replace("4", "    ")
                    .replace("3", "   ")
                    .replace("2", "  ")
                    .replace("1", " ")
                    .replace("/", "");

            for (byte x = 0; x <= 7; x++) {
                for (byte y = 0; y <= 7; y++) {
                    byte posFEN = (byte) ((7 - x) + y * 8);
                    byte posBOARD = (byte) (63 - (x + y * 8));
                    char c = init.toCharArray()[posFEN];
                    board[posBOARD] = c;
                    if (isWhitePiece(c)) {
                        Piece piece = new Piece(posBOARD, c);
                        whitePieces.add(piece);
                        if (c == 'K') whitePieces.setKing(piece);
                    }
                    if (isBlackPiece(c)) {
                        Piece piece = new Piece(posBOARD, c);
                        blackPieces.add(piece);
                        if (c == 'k') blackPieces.setKing(piece);
                    }
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
        score = calculateScore();
        clock.initialize();
    }

    public String boardToString() {
        return new String(board).replace(' ', '_');
    }

    public short getScore() {
        return score;
    }

    public String getStatusNotice() {
        return gameStatus.getStatusNotice();
    }

    public Status getStatus() {
        return gameStatus.getStatus();
    }

    private short calculateScore() {

        short score = 0;

        for (Piece p : whitePieces)
            score += PIECE_VALUES.get(p.getPattern());

        for (Piece p : blackPieces)
            score += PIECE_VALUES.get(p.getPattern());

        return score;
    }

    public Moves getUserLegalMoves(boolean userColor, boolean userPlaysBothColors) {

        if (userPlaysBothColors) return getUserLegalMoves();
        else if (whiteToMove == userColor) return getUserLegalMoves();
        else return new Moves();
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
                    gameStatus.setStatus(Status.WHITE_CHECKMATED);
                else
                    gameStatus.setStatus(Status.DRAW_STALEMATE);
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
                    gameStatus.setStatus(Status.BLACK_CHECKMATED);
                else
                    gameStatus.setStatus(Status.DRAW_STALEMATE);
            }

        }
        return pseudoMoves;
    }

    /** check if legal, if so move piece */
    public boolean userMove(Move move, boolean userColor, boolean userPlaysBoth) {

        if (gameStatus.getStatus() != Status.UNDECIDED) return false;

        if (move.getInformation() < 0) {

            switch (move.getInformation()) {

                case Move.RESIGN:
                    if (whiteToMove) {
                        gameStatus.setStatus(Status.WHITE_RESIGNED);
                    } else {
                        gameStatus.setStatus(Status.BLACK_RESIGNED);
                    }
                    return true;

                case Move.OFFER_DRAW:
                    gameStatus.setDrawOffered(true);
                    return true;

                case Move.ACCEPT_DRAW:
                    if (gameStatus.isDrawOffered()) {
                        gameStatus.setStatus(Status.DRAW_ACCEPTED);
                        return true;
                    }
                    return false; // nothing to accept if not offered

                case Move.DECLINE_DRAW:
                    gameStatus.setDrawOffered(false);
                    return true;
            }

        } else if (!getUserLegalMoves(userColor, userPlaysBoth).contains(move)) {
            System.out.print("USER MOVE" + move + " IS ILLEGAL (CHESS) / ");
            return false;
        }
        gameStatus.setDrawOffered(false);
        undoneMovesHistory.clear();
        clock.update();
        movePiece(move);
        getUserLegalMoves(); //to make sure a checkmate will be detected
        return true;
    }

    /** move piece without check. Computer won't try illegal moves ;) */
    private void movePiece(Move move) {

        byte from = move.getFrom();
        byte to = move.getTo();
        char typeMovingPiece = board[from];
        char typePromotedPiece = typeMovingPiece;
        char capture = board[to]; //doesn't need to be a capture => can also be ' '

        history.push(new State(hashGenerator.getHashCode(), move.getInformation(), board[to], capture,
                lastCaptureOrPawnMove, enPassantPawn, castling.getRights(), score));

        if (whiteToMove) {
            if (capture != ' ') {
                hashGenerator.hashPosition(capture, to);
                score -= blackPieces.removePiece(to);
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
                    movingPiece.setPattern(WHITE_QUEEN);
                    score -= PIECE_VALUES.get(WHITE_PAWN);
                    score += PIECE_VALUES.get(WHITE_QUEEN);
                } else if (move.getSpecial() == Move.PROMOTION_BISHOP) {
                    board[to] = 'B';
                    typePromotedPiece = 'B';
                    movingPiece.setPattern(WHITE_BISHOP);
                    score -= PIECE_VALUES.get(WHITE_PAWN);
                    score += PIECE_VALUES.get(WHITE_BISHOP);
                } else if (move.getSpecial() == Move.PROMOTION_KNIGHT) {
                    board[to] = 'N';
                    typePromotedPiece = 'N';
                    movingPiece.setPattern(WHITE_KNIGHT);
                } else if (move.getSpecial() == Move.PROMOTION_ROOK) {
                    board[to] = 'R';
                    typePromotedPiece = 'R';
                    movingPiece.setPattern(WHITE_ROOK);
                    score -= PIECE_VALUES.get(WHITE_PAWN);
                    score += PIECE_VALUES.get(WHITE_ROOK);
                } else if (move.getSpecial() == Move.EN_PASSANT) {
                    board[to - 8] = ' ';
                    hashGenerator.hashPosition('p', (byte) (to - 8));
                    score += blackPieces.removePiece((byte) (to - 8));
                }
            }
        } else { // "blackToMove"
            if (capture != ' ') {
                hashGenerator.hashPosition(capture, to);
                score -= whitePieces.removePiece(to);
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
                    movingPiece.setPattern(BLACK_QUEEN);
                    score -= PIECE_VALUES.get(BLACK_PAWN);
                    score += PIECE_VALUES.get(BLACK_QUEEN);
                } else if (move.getSpecial() == Move.PROMOTION_BISHOP) {
                    board[to] = 'b';
                    typePromotedPiece = 'b';
                    movingPiece.setPattern(BLACK_BISHOP);
                    score -= PIECE_VALUES.get(BLACK_PAWN);
                    score += PIECE_VALUES.get(BLACK_BISHOP);
                } else if (move.getSpecial() == Move.PROMOTION_KNIGHT) {
                    board[to] = 'n';
                    typePromotedPiece = 'n';
                    movingPiece.setPattern(BLACK_KNIGHT);
                    score -= PIECE_VALUES.get(BLACK_PAWN);
                    score += PIECE_VALUES.get(BLACK_KNIGHT);
                } else if (move.getSpecial() == Move.PROMOTION_ROOK) {
                    board[to] = 'r';
                    typePromotedPiece = 'r';
                    movingPiece.setPattern(BLACK_ROOK);
                    score -= PIECE_VALUES.get(BLACK_PAWN);
                    score += PIECE_VALUES.get(BLACK_ROOK);
                } else if (move.getSpecial() == Move.EN_PASSANT) {
                    board[to + 8] = ' ';
                    hashGenerator.hashPosition('P', (byte) (to + 8));
                    score += whitePieces.removePiece((byte) (to + 8));
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

        if (history.drawDueToRepetition(hashGenerator.getHashCode()))
            gameStatus.setStatus(Status.DRAW_REPETITION);

        if (moveCounter - lastCaptureOrPawnMove >= Constants.MAX_MOVES)
            gameStatus.setStatus(Status.DRAW_MOVES);

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

    public int userRedoAll() {
        int size = undoneMovesHistory.size();
        for (int i = 0; i <= size; i++) {
            userRedo();
        }
        return size;
    }

    /**
     * Revoke last move.
     *
     * @return information: Information about the revoked move (see Move.java)
     */
    private short undo() {

        State state = history.pop();
        gameStatus.setStatus(Status.UNDECIDED);
        enPassantPawn = state.enPassant;
        byte from = Move.getFrom(state.moveInformation);
        byte to = Move.getTo(state.moveInformation);
        score = state.score;

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
                    movingPiece.setPattern(BLACK_PAWN);
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
                    movingPiece.setPattern(WHITE_PAWN);
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
        lastCaptureOrPawnMove = state.lastCaptureOrPawnMove;
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
    }

    public boolean pieceAtSquare(int i, boolean color) {

        if (color == Constants.WHITE) return isWhitePiece(board[i]);
        else return isBlackPiece(board[i]);
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

    public Moves getPseudoLegalMoves() {

        if (whiteToMove) {
            return whitePieces.getPseudoLegalMoves();
        } else return blackPieces.getPseudoLegalMoves();
    }

    public class PieceCollectionWhite extends PieceCollection {

        public PieceCollectionWhite() {

            super();
        }

        public Moves getPseudoLegalMoves() {

            Moves moves = new Moves();
            for (Piece p : this) moves.addAll(p.getPseudoLegalMoves(enPassantPawn));
            moves.addAll(king.getPseudoLegalKingMoves(blackPieces.getThreats(),
                    castling.whiteKingSide(), castling.whiteQueenSide()));
            return moves;
        }

        @Override
        public void printThreats() {

            System.out.println("\nWHITE THREATS: \n");
            super.printThreats();
        }
    }

    public class PieceCollectionBlack extends PieceCollection {

        public PieceCollectionBlack() {
            super();
        }

        public Moves getPseudoLegalMoves() {

            Moves moves = new Moves();
            for (Piece p : this) moves.addAll(p.getPseudoLegalMoves(enPassantPawn));
            moves.addAll(king.getPseudoLegalKingMoves(whitePieces.getThreats(),
                    castling.blackKingSide(), castling.blackQueenSide()));
            return moves;
        }

        @Override
        public void printThreats() {

            System.out.println("\nBLACK THREATS: \n");
            super.printThreats();
        }
    }
}

class State {

    final Long hashCode;
    final short moveInformation;
    final short lastCaptureOrPawnMove;
    final byte enPassant;
    final byte castling;
    final char pieceType;
    final short score;
    char capture;

    public State(long hashCode, short moveInformation, char pieceType, char capture,
                 short lastCapt, byte enPassant, byte castling, short score) {

        this.hashCode = hashCode;
        this.moveInformation = moveInformation;
        this.pieceType = pieceType;
        this.capture = capture;
        this.lastCaptureOrPawnMove = lastCapt;
        this.enPassant = enPassant;
        this.castling = castling;
        this.score = score;
    }
}

class GameStatus {

    private Status status;
    private boolean drawOffered;

    public GameStatus() {
        reset();
    }

    public boolean isDrawOffered() {
        return drawOffered;
    }

    public void setDrawOffered(boolean drawOffered) {
        this.drawOffered = drawOffered;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void reset() {
        status = Status.UNDECIDED;
    }

    public String getStatusNotice() {
        return status.getNotice();
    }
}


abstract class PieceCollection extends HashSet<Piece> {

    private final Stack<Piece> capturedPieces;
    private final byte[] threats;
    protected Piece king;

    public PieceCollection() {
        threats = new byte[64];
        capturedPieces = new Stack<>();
    }

    abstract public Moves getPseudoLegalMoves();

    public void setKing(Piece king) {
        this.king = king;
    }

    public byte[] getThreats() {
        return threats;
    }

    public void print() {
        for (Piece p : this) {
            p.print();
        }
        System.out.println("TOTAL: " + this.size() + " ACTIVE PIECES");
    }

    public String getCapturedPiecesString() {

        StringBuilder outp = new StringBuilder();
        for (Piece p : capturedPieces) {
            outp.append(p.type);
        }
        return outp.toString();
    }

    /**
     * Change position of chess piece on position "from" to position "to". Return piece that changes position."
     *
     * @param from square the piece comes from
     * @param to   square the piece shall move to
     * @return piece that moves
     */
    public Piece changePosition(byte from, byte to) {
        for (Piece p : this) { //todo hashMap with squares should be faster than a loop...
            if (p.getPosition() == from) {
                p.setPosition(to);
                return p;
            }
        }
        throw new InputMismatchException("NO PIECE AT " + Util.parse(from));
    }

    /**
     * Remove piece located at given position
     *
     * @param pos Position of the piece to be removed
     * @return value of the removed piece
     */
    public short removePiece(byte pos) {

        Piece removePiece = null;
        for (Piece p : this) {
            if (p.getPosition() == pos) {
                removePiece = p;
                break;
            }
        }
        this.remove(removePiece);
        capturedPieces.add(removePiece);
        assert removePiece != null;
        return PiecePatterns.PIECE_VALUES.get(removePiece.getPattern());
    }

    public void activateLastCapturedPiece() {
        this.add(capturedPieces.pop());
    }

    public void updateThreats() {

        Arrays.fill(threats, (byte) 0);
        for (Piece p : this) p.updateThreats(threats);
    }

    public void printThreats() {

        StringBuilder outp = new StringBuilder();
        StringBuilder line = new StringBuilder("|");
        for (int i = 0; i <= 63; i++) {
            line.append(threats[i]).append("|");
            if (i % 8 == 7) {
                outp.insert(0, line.toString() + (i / 8 + 1) + "\n");
                line = new StringBuilder("|");
            }
        }
        outp.append(".A.B.C.D.E.F.G.H.").append("\n");

        System.out.println(outp);
    }

    @Override
    public void clear() {
        super.clear();
        capturedPieces.clear();
    }
}

class Piece {

    public final char type;
    private byte position;
    private PiecePattern pattern;

    public Piece(byte pos, char type) {

        this.position = pos;
        this.type = type;
        this.pattern = PiecePatterns.getPiecePattern(type);
    }

    public PiecePattern getPattern() {
        return pattern;
    }

    public void setPattern(PiecePattern pattern) {
        this.pattern = pattern;
    }

    public byte getPosition() {
        return position;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public void updateThreats(byte[] threats) {
        pattern.updateThreats(this.position, threats);
    }

    public Moves getPseudoLegalMoves(byte enPassantPawn) {
        return pattern.getMoves(position, enPassantPawn);
    }

    public Moves getPseudoLegalKingMoves(byte[] threats, boolean kingSideCastling, boolean queenSideCastling) {
        return pattern.getKingMoves(position, threats, kingSideCastling, queenSideCastling);
    }

    public void print() {
        System.out.print(Util.parseSymbol(type) + " #" + Util.parse(position) + " | ");
    }
}