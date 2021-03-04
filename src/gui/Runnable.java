package gui;

import core.Chess;
import core.Move;
import core.Parser;
import fileHandling.ReadWrite;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Runnable {
    private final String storagePath = "chessUserData/currentGame.txt";
    private final Gui gui;
    private Chess chess;
    private String moveString;
    private Move lastMove = null;

    public Runnable() {
        chess = new Chess();
        moveString = "";
        gui = new Gui(chess.getBoard());

        /* add action listeners */
        gui.item_new.addActionListener(e -> {
            System.out.println("NEW GAME!");
            chess.newGame();
            gui.boardCanvas.repaint();
            gui.boardCanvas.paintDiffus();
            lastMove = null;
            gui.show_dialog("Spiel beginnen");
        });
        gui.item_store.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });
        gui.item_restore.addActionListener(e -> {
            chess.pieceAtSquare(0);
            Object obj = null;
            try {
                obj = ReadWrite.readFromFile(storagePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (obj != null) {
                chess = (Chess) obj;
                gui.boardCanvas.setBoard(chess.getBoard());
                System.out.println("SUCCESSFULLY LOADED");
                gui.boardCanvas.repaint();
            }
        });
        gui.item_begin.addActionListener(e -> {
            System.out.println("NOT YET IMPLEMENTED");
            {}
        });
        gui.boardCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int btn = event.getButton();
                switch (btn) {
                    case 1: {
                        byte pos = Parser.coordFromEvent(event, gui.boardCanvas.offset, gui.boardCanvas.size_factor);

                        if (moveString.equals("")) {
                            if (chess.pieceAtSquare(pos, chess.getTurnColor())) {

                                moveString += Parser.parse(pos) + " ";
                            }
                        } //you already chose a square:
                        else if (!moveString.equals(Parser.parse(pos) + " ")) //avoids moves like A1->A1! :
                            moveString += Parser.parse(pos) + " ";

                        if (moveString.length() > 5) {
                            if (chess.movePieceUser(new Move(moveString))) {
                                lastMove = new Move(moveString);
                            } else System.out.println("MOVE ILLEGAL.");
                            moveString = "";
                        }

                        gui.boardCanvas.refresh(true, true,
                                chess.getAllMoves().getMovesFrom(pos), lastMove);
                        break;
                    }
                    case 2: {
                        break;
                    }
                    case 3: {
                        moveString = "";
                        gui.boardCanvas.repaint();
                        byte pos = Parser.coordFromEvent(event,
                                gui.boardCanvas.offset, gui.boardCanvas.size_factor);
                        System.out.println(event.getX() + "/" + event.getX());
                        System.out.println(Parser.parse(pos));
                        break;
                    }
                }
            }
        });
    }
}
