package gui;

import core.*;
import fileHandling.ReadWrite;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Gui extends MainWindow {

    final boolean WHITE = true; //for readability reasons...

    private final String storagePath = "chessUserData/currentGame.txt";
    private Chess chess;
    private String moveString;
    private String moveStringSpecialMoves;

    public Gui(Chess chessGame) {
        super(chessGame.getBoard());
        this.chess = chessGame;
        moveString = "";
        moveStringSpecialMoves = "";


        /* add action listeners */
        item_new.addActionListener(e -> {
            System.out.println("NEW GAME!");
            chess.newGame(chess.INIT_STANDARD_BOARD);
            refreshFrameContent(-1);
            resetMoveString();
            show_popup("Spiel beginnen");
        });

        item_store.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });

        item_restore.addActionListener(e -> {
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

        item_begin.addActionListener(e -> {
            System.err.println("NOT YET IMPLEMENTED");
            {
            }
        });

        item_castling_kingside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                if (!chess.movePieceUser(new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o ILLEGAL");
            } else { //BLACK
                if (!chess.movePieceUser(new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o ILLEGAL");
            }

            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        item_castling_queenside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                if (!chess.movePieceUser(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o-o ILLEGAL");
            } else { //BLACK
                if (!chess.movePieceUser(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o-o ILLEGAL");
            }

            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        item_promotion_queen.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "Q"));
            refreshFrameContent(-1);
        });

        item_promotion_rook.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "R"));
            refreshFrameContent(-1);
        });

        item_promotion_knight.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "N"));
            refreshFrameContent(-1);
        });

        item_promotion_bishop.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "B"));
            refreshFrameContent(-1);
        });

        item_undo.addActionListener(e -> {
            chess.userUndo();
            refreshFrameContent(-1);
        });

        item_redo.addActionListener(e -> {
            chess.userRedo();
            refreshFrameContent(-1);
        });

        item_change_piece_style.addActionListener(e -> {
            board.fontRoulette();
            setStyleSettings();
            refreshFrameContent(-1);
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

                        /*
                        chess.whitePieces.printThreats();
                        chess.blackPieces.printThreats();*/
                        break;
                    }
                    case 3: {
                        /*chess.userUndo();
                        boardCanvas.repaint();*/
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
                            show_promotion_popup();
                            return;
                        }
                    } else {
                        Moves legalMoves = chess.blackPieces.getPseudoLegalMoves();
                        if (legalMoves.contains(promotionMove)) {
                            moveStringSpecialMoves = moveString;
                            moveString = "";
                            show_promotion_popup();
                            return;
                        }
                    }
                    System.err.println("MOVE ILLEGAL");
                }
            }
            //moveStringSpecialMoves = moveString;
            moveString = "";
        }
        refreshFrameContent(pos);
    }

    public void refreshFrameContent(int pos) {

        if (chess.gameStatus.getStatusCode() != GameStatus.UNDECIDED) {
            show_popup(chess.gameStatus.getStatusNotice());
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
            show_popup(chess.gameStatus.getStatusNotice());
    }

    public void resetMoveString() {
        moveString = "";
    }
}
