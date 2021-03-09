package gui;

import core.Chess;
import core.Move;
import core.Parser;
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
            chess.newGame();
            board.repaint();
            board.paintDiffus();
            resetMoveString();
            show_dialog("Spiel beginnen");
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
                board.setBoard(chess.getBoard());
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

            refreshAllCanvasElements(-1); // -1 : don't want to paint legal moves.
        });

        item_castling_queenside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                if (!chess.movePieceUser(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o-o ILLEGAL");
            } else { //BLACK
                if (!chess.movePieceUser(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
                    System.err.println("CASTLING o-o-o ILLEGAL");
            }

            refreshAllCanvasElements(-1); // -1 : don't want to paint legal moves.
        });

        item_promotion_queen.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "Q"));
            refreshAllCanvasElements(-1);
        });

        item_promotion_rook.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "R"));
            refreshAllCanvasElements(-1);
        });

        item_promotion_knight.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "N"));
            refreshAllCanvasElements(-1);
        });

        item_promotion_bishop.addActionListener(e -> {
            chess.movePieceUser(new Move(moveStringSpecialMoves + "B"));
            refreshAllCanvasElements(-1);
        });

        item_undo.addActionListener(e -> {
            chess.userUndo();
            refreshAllCanvasElements(-1);
        });

        item_redo.addActionListener(e -> {
            chess.userRedo();
            refreshAllCanvasElements(-1);
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
                        chess.whitePieces.printThreats();
                        chess.blackPieces.printThreats();
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
                board.getOffset(),
                board.getSizeFactor());

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
                    System.err.println("MOVE ILLEGAL");
                    // in rank 2 ? might be a promotion move...
                    if ((nextMove.getFrom() >= Parser.parse("A2"))
                            && (nextMove.getFrom() <= Parser.parse("H2"))) {
                        System.out.println("Promotion move?");
                        promotion_menu.show(panel, panel.getWidth(), 0);
                    }// in rank 7 ? might be a promotion move...
                    else if ((nextMove.getFrom() >= Parser.parse("A7"))
                            && (nextMove.getFrom() <= Parser.parse("H7"))) {
                        System.out.println("Promotion move?");
                        promotion_menu.show(panel, panel.getWidth(), 0);
                    }
                }
            }
            moveStringSpecialMoves = moveString;
            moveString = "";
        }
        refreshAllCanvasElements(pos);
    }

    public void refreshAllCanvasElements(int pos) {
        short lastMove = chess.history.getLastMoveCoordinate();
        board.refresh(true, true,
                chess.getPseudoLegalMoves().getMovesFrom((byte) pos), lastMove);
    }

    public void resetMoveString() {
        moveString = "";
    }
}
