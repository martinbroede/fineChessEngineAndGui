package gui;

import chessNetwork.Network;
import chessNetwork.TestReachability;
import core.*;
import fileHandling.ReadWrite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.NoSuchElementException;

public class Gui extends MainWindow {

    private final int DELAY_MILLISEC = 100; //update interval
    final boolean WHITE = Constants.WHITE; //for readability reasons
    final boolean BLACK = Constants.BLACK;
    final Network network;
    private final String storagePath = "chessUserData/currentGame.txt";
    private final ChatDialog chatDialog;
    private Chess chess;
    private String moveString;
    private String moveStringSpecialMoves; //for promotion, castling, enPassant
    private boolean allowUndoMoves = true;
    private boolean userPlaysColor = WHITE;
    private boolean userPlaysBothColors = true;
    private boolean hiddenFeature = false;

    public Gui(Chess chessGame) {

        super(chessGame);
        chatDialog = new ChatDialog();
        this.chess = chessGame;
        moveString = "";
        moveStringSpecialMoves = "";
        network = new Network(VERSION);

        class NetworkUpdater extends Thread {

            @Override
            public void run() {
                while (true) {

                    if (network.messageQueue.size() > 0) {
                        String message;
                        try {
                            message = network.messageQueue.getFirst();
                            network.messageQueue.removeFirst();

                            String[] args = message.split(" ");
                            if (args[0].equals("MOVE")) {
                                Move nextMove = new Move(Short.parseShort(args[1]));
                                switch(nextMove.getInformation()) {
                                    case Move.START_GAME:
                                        chess.newGame();
                                        showPopup("Spiel beginnen");
                                        break;
                                    case Move.OPPONENT_BLACK:
                                        userPlaysColor = BLACK;
                                        board.setWhitePlayerNorth();
                                        userPlaysBothColors = false;
                                        showPopup("Du spielst SCHWARZ");
                                        break;
                                    case Move.OPPONENT_WHITE:
                                        userPlaysColor = WHITE;
                                        board.setWhitePlayerSouth();
                                        userPlaysBothColors = false;
                                        showPopup("Du spielst WEISS");
                                        break;
                                    default:
                                        System.out.println(nextMove.getInformation());
                                        chess.userMove(nextMove, userPlaysColor, true); // todo should work with false
                                }
                                refreshFrameContent(-1);
                            }else if(args[0].equals("NOTE")) {
                                showPopup(message.replace("NOTE ",""));
                            }else if(args[0].equals("ERROR")){
                                new DialogMessage(message.replace("ERROR ",""));
                            }
                            else chatOutput.append("\t: " + message + "\n");
                        } catch (NoSuchElementException ex) {
                            System.err.println("MESSAGE QUEUE IS EMPTY");
                        }
                    }

                    try {
                        sleep(DELAY_MILLISEC);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        NetworkUpdater networkUpdater = new NetworkUpdater();
        networkUpdater.start();


        /* add action listeners */
        itemNewGame.addActionListener(e -> {
            System.out.println("NEW GAME");
            chess.newGame(chess.INIT_STANDARD_BOARD);
            userPlaysBothColors = true;
            refreshFrameContent(-1);
            resetMoveString();
            showPopup("Spiel beginnen");
        });

        itemStore.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });

        itemRestore.addActionListener(e -> {
            Object obj = null;
            try {
                obj = ReadWrite.readFromFile(storagePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (obj != null) {
                chess = (Chess) obj;
                board.setBoardArray(chess.getBoard());
                System.out.println("SUCCESSFULLY LOADED");
                board.repaint();
            }
        });

        itemBegin.addActionListener(e -> System.err.println("NOT YET IMPLEMENTED"));

        itemCastlingKingside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                Move nextMove = new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.err.println("CASTLING o-o ILLEGAL");

            } else { //BLACK
                Move nextMove = new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.err.println("CASTLING o-o ILLEGAL");

            }

            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemCastlingQueenside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                Move nextMove = new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.err.println("CASTLING o-o-o ILLEGAL");

            } else { //BLACK
                Move nextMove = new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.err.println("CASTLING o-o-o ILLEGAL");

            }

            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemPromotionQueen.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "Q"));
            refreshFrameContent(-1);
        });

        itemPromotionRook.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "R"));
            refreshFrameContent(-1);
        });

        itemPromotionKnight.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "N"));
            refreshFrameContent(-1);
        });

        itemPromotionBishop.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "B"));
            refreshFrameContent(-1);
        });

        itemUndo.addActionListener(e -> {
            if (network.isActive()) {
                System.out.println("UNDO NOT ALLOWED WHEN NETWORK IS ACTIVE");
            } else {
                chess.userUndo();
                refreshFrameContent(-1);
            }
        });

        itemRedo.addActionListener(e -> {
            if (network.isActive()) {
                System.out.println("REDO NOT ALLOWED WHEN NETWORK IS ACTIVE");
            } else {
                chess.userRedo();
                refreshFrameContent(-1);
            }
        });

        itemChangePieceStyle.addActionListener(e -> {
            board.fontRoulette();
            setStyleSettings();
            refreshFrameContent(-1);
        });

        itemShowChat.addActionListener(e -> chatDialog.toggleVisibility());

        itemStartClient.addActionListener(e -> network.showClientIpDialog(frame.getLocation()));
        itemStartServer.addActionListener(e -> network.showServerIpDialog(frame.getLocation()));
        itemNewNetworkGame.addActionListener(e -> {
            chess.newGame();
            network.startGame();
            showPopup("Spiel beginnen");
        });
        itemNetworkDestroy.addActionListener(e -> network.safeDeleteServerOrClient());

        itemRotateBoard.addActionListener(e -> {
            board.toggleBoardOrientation();
            refreshFrameContent(-1);
        });

        itemAssignOpponentBlack.addActionListener(e -> {
            network.sendToNet("MOVE " + Move.OPPONENT_BLACK);
            userPlaysColor = WHITE;
            userPlaysBothColors = false;
            board.setWhitePlayerSouth();
            refreshFrameContent(-1);
            showPopup("Du spielst WEISS");
        });

        itemAssignOpponentWhite.addActionListener(e -> {
            network.sendToNet("MOVE " + Move.OPPONENT_WHITE);
            userPlaysColor = BLACK;
            userPlaysBothColors = false;
            board.setWhitePlayerNorth();
            refreshFrameContent(-1);
            showPopup("Du spielst SCHWARZ");
        });

        itemResign.addActionListener(e -> {
            if(chess.getTurnColor() == userPlaysColor) {
                network.sendToNet("MOVE " + Move.RESIGN);
                chess.userMove(new Move(Move.RESIGN), userPlaysColor, userPlaysBothColors);
                showPopup(chess.gameStatus.getStatusNotice());
            }else{
                showPopup("Gib auf, wenn du am Zug bist.");
            }
        });

        chatInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    network.sendToNet(chatInput.getText());
                    chatOutput.append(chatInput.getText() + "\n");
                    chatInput.setText("");
                }
            }
        });

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'R')
                    new TestReachability();
                if(hiddenFeature && e.getKeyChar()=='\\') {
                    System.out.println("TRAINER MODE");
                    board.toggleShowHints();
                    refreshFrameContent(-1);
                }

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.isControlDown() && e.isAltDown()) hiddenFeature = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                hiddenFeature = false;
            }
        });


        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int btn = mouseEvent.getButton();
                switch (btn) {
                    case 1: {
                        movePieceFromEvent(mouseEvent);
                        break;
                    }
                    case 2: {
                        chess.castling.print();
                        chess.blackPieces.printCaptured();
                        chess.whitePieces.printCaptured();
                        break;
                    }
                    case 3: {
                        chess.whitePieces.printThreats();
                        chess.blackPieces.printThreats();
                        break;
                    }
                }
            }
        });
    }

    private boolean movePiece(Move move) {
        if (chess.userMove(move, userPlaysColor, userPlaysBothColors)) {
            if (network.isActive()) network.sendToNet("MOVE " + move.getInformation());
            return true;
        } else return false;
    }

    private void movePieceFromEvent(MouseEvent mouseEvent) {

        byte pos = board.coordFromEvent(mouseEvent);

        if (moveString.equals("")) { // no square chosen yet
            if (chess.pieceAtSquare(pos, chess.getTurnColor())) {

                moveString += Parser.parse(pos) + " ";
            }
        } //you already chose a square:
        else if (!moveString.equals(Parser.parse(pos) + " ")) //avoids moves like A1->A1! :
            moveString += Parser.parse(pos) + " ";

        if (moveString.length() > 5) { // "A1 A2 ": from A1 to A2
            Move nextMove = new Move(moveString);

            if (!movePiece(nextMove)) {
                /* move illegal? try castling moves */
                if (nextMove.isFrom(Parser.parse("E1")) && nextMove.getTo() == Parser.parse("G1")) {
                    if (!movePiece(new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o ILLEGAL");
                } else if (nextMove.isFrom(Parser.parse("E1")) && nextMove.getTo() == Parser.parse("C1")) {
                    if (!movePiece(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o-o ILLEGAL");
                } else if (nextMove.isFrom(Parser.parse("E8")) && nextMove.getTo() == Parser.parse("G8")) {
                    if (!movePiece(new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o ILLEGAL");
                } else if (nextMove.isFrom(Parser.parse("E8")) && nextMove.getTo() == Parser.parse("C8")) {
                    if (!movePiece(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o-o ILLEGAL");
                } else {
                    byte from = nextMove.getFrom();
                    byte to = nextMove.getTo();
                    Move promotionMove = new Move(from, to, Move.PROMOTION_QUEEN);

                    if (chess.getTurnColor() == WHITE) {
                        Moves legalMoves = chess.whitePieces.getPseudoLegalMoves();
                        if (legalMoves.contains(promotionMove)) {
                            moveStringSpecialMoves = moveString;
                            moveString = "";
                            showPromotionPopup();
                            return;
                        }
                    } else {
                        Moves legalMoves = chess.blackPieces.getPseudoLegalMoves();
                        if (legalMoves.contains(promotionMove)) {
                            moveStringSpecialMoves = moveString;
                            moveString = "";
                            showPromotionPopup();
                            return;
                        }
                    }
                    System.err.println("MOVE ILLEGAL (GUI)");
                }
            }
            moveString = "";
        }
        refreshFrameContent(pos);
    }

    public void refreshFrameContent(int pos) {

        if (chess.gameStatus.getStatusCode() != GameStatus.UNDECIDED) {
            showPopup(chess.gameStatus.getStatusNotice());
        } else {
            short lastMove = chess.history.getLastMoveCoordinate();
            board.refreshChessBoard(true, true,
                    chess.getUserLegalMoves(userPlaysColor, userPlaysBothColors).getMovesFrom((byte) pos), lastMove);

            labelCapturedWhitePieces.setText(" " + chess.whitePieces.getCapturedPiecesAsSymbols() + " ");
            labelCapturedBlackPieces.setText(" " + chess.blackPieces.getCapturedPiecesAsSymbols() + " ");

            {
                String textWest = "";
                String textEast = "";

                int white = chess.whitePieces.getCapturedPiecesAsSymbols().length();
                int black = chess.blackPieces.getCapturedPiecesAsSymbols().length();

                int difference = white - black;

                if (difference > 0) textWest += "+" + Math.abs(difference) + " ";
                else if (difference < 0) textEast += "+" + Math.abs(difference) + " ";

                labelPlaceHolderWest.setText(textWest);
                labelPlaceHolderEast.setText(textEast);
            }
            frame.pack();
        }

        if (chess.gameStatus.getStatusCode() != GameStatus.UNDECIDED)
            showPopup(chess.gameStatus.getStatusNotice());
    }

    public void resetMoveString() {
        moveString = "";
    }

    class ChatDialog extends JDialog {

        public ChatDialog() {
            setLayout(new BorderLayout());
            add(chatOutput, BorderLayout.NORTH);
            add(chatInput, BorderLayout.SOUTH);
        }

        public void toggleVisibility() {
            Dimension newDim = new Dimension(appearanceSettings.getMargin(), appearanceSettings.getMargin());
            chatOutput.setPreferredSize(newDim);

            chatOutput.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);

            newDim = new Dimension(appearanceSettings.getMargin(), appearanceSettings.getSizeFactor());
            chatInput.setPreferredSize(newDim);

            chatInput.setBackground(appearanceSettings.getColorScheme().HIGHLIGHT_1_COLOR);

            Point location = frame.getLocation();
            location.translate(frame.getWidth(), 0);
            setLocation(location);

            pack();
            setVisible(!isVisible());
        }
    }
}
