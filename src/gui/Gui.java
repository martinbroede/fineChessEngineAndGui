package gui;

import chessNetwork.Network;
import core.*;
import fileHandling.ReadWrite;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends MainWindow {

    private final String storagePath = "chessUserData/currentGame.txt";

    final boolean WHITE = Constants.WHITE; //for readability reasons

    final Network network;
    private Chess chess;
    private String moveString;
    private String moveStringSpecialMoves; //for promotion, castling, enPassant

    public Gui(Chess chessGame) {

        super(chessGame.getBoard());

        this.chess = chessGame;
        moveString = "";
        moveStringSpecialMoves = "";

        {
            network = new Network(this.chess);
            class BoardUpdater extends Thread {
                public void run() {
                    System.out.println("GUI WILL UPDATE MOVES FROM NETWORK.");
                    while (true) {
                        if (network.updateAdvised()) {
                            refreshFrameContent(-1);
                            network.reportUpdate();
                        }
                        try {
                            sleep(100);
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
            BoardUpdater boardUpdater = new BoardUpdater();
            boardUpdater.start();
        }


        /* add action listeners */
        itemNew.addActionListener(e -> {
            System.out.println("NEW GAME!");
            chess.newGame(chess.INIT_STANDARD_BOARD);
            refreshFrameContent(-1);
            resetMoveString();
            showPopup("Spiel beginnen");
        });

        itemStore.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });

        itemRestore.addActionListener(e -> {
            chess.pieceAtSquare(0);
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
                if (!chess.movePieceUser(new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o ILLEGAL");
            } else { //BLACK
                if (!chess.movePieceUser(new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o ILLEGAL");
            }

            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemCastlingQueenside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                if (!chess.movePieceUser(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o-o ILLEGAL");
            } else { //BLACK
                if (!chess.movePieceUser(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o-o ILLEGAL");
            }

            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemPromotionQueen.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "Q"));
            refreshFrameContent(-1);
        });

        itemPromotionRook.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "R"));
            refreshFrameContent(-1);
        });

        itemPromotionKnight.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "N"));
            refreshFrameContent(-1);
        });

        itemPromotionBishop.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "B"));
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

        itemStartClient.addActionListener(e -> network.createClient());
        itemStartServer.addActionListener(e -> network.createServer());
        itemSynchronize.addActionListener(e -> network.startMoveUpdater());
        itemNetworkDestroy.addActionListener(e -> network.safeDeleteServerOrClient());

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'C') {
                    network.createClient();
                } else if (e.getKeyChar() == 'D') {
                    network.safeDeleteServerOrClient();
                } else if (e.getKeyChar() == 'S') {
                    network.createServer();
                } else if (e.getKeyChar() == 'M') {
                    network.startMoveUpdater();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

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

    private void movePieceFromEvent(MouseEvent mouseEvent) {

        byte pos = Parser.coordFromEvent(mouseEvent,
                appearanceSettings.getOffset(),
                appearanceSettings.getSizeFactor());

        if (moveString.equals("")) {
            if (chess.pieceAtSquare(pos, chess.getTurnColor())) {

                moveString += Parser.parse(pos) + " ";
            }
        } //you already chose a square:
        else if (!moveString.equals(Parser.parse(pos) + " ")) //avoids moves like A1->A1! :
            moveString += Parser.parse(pos) + " ";

        if (moveString.length() > 5) {
            Move nextMove = new Move(moveString);

            if (network.isActive()) { //todo remove
                network.send("MOVE " + nextMove.getInformation() + ' ');
            } //todo remove


            if (!chess.movePieceUser(nextMove)) {
                /* move illegal? try castling moves */
                if (nextMove.isFrom(Parser.parse("E1")) && nextMove.getTo() == Parser.parse("G1")) {
                    if (!chess.movePieceUser(new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o ILLEGAL");
                } else if (nextMove.isFrom(Parser.parse("E1")) && nextMove.getTo() == Parser.parse("C1")) {
                    if (!chess.movePieceUser(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o-o ILLEGAL");
                } else if (nextMove.isFrom(Parser.parse("E8")) && nextMove.getTo() == Parser.parse("G8")) {
                    if (!chess.movePieceUser(new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING)))
                        System.err.println("CASTLING o-o ILLEGAL");
                } else if (nextMove.isFrom(Parser.parse("E8")) && nextMove.getTo() == Parser.parse("C8")) {
                    if (!chess.movePieceUser(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
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
                    System.err.println("MOVE ILLEGAL");
                }
            }
            /* moveStringSpecialMoves = moveString;*/
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
                    chess.getUserLegalMoves().getMovesFrom((byte) pos), lastMove);

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
}
