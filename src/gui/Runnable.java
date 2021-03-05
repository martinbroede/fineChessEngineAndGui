package gui;

import core.Chess;
import core.Move;
import core.Parser;
import fileHandling.ReadWrite;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Runnable {
    private final String storagePath = "chessUserData/currentGame.txt";
    private final MainWindow mainWindow;
    private Chess chess;
    private String moveString;
    private Move lastMove = null;

    public Runnable() {
        chess = new Chess();
        moveString = "";
        mainWindow = new MainWindow(chess.getBoard());

        /* add action listeners */
        mainWindow.item_new.addActionListener(e -> {
            System.out.println("NEW GAME!");
            chess.newGame();
            mainWindow.boardCanvas.repaint();
            mainWindow.boardCanvas.paintDiffus();
            lastMove = null;
            mainWindow.show_dialog("Spiel beginnen");
        });
        mainWindow.item_store.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });
        mainWindow.item_restore.addActionListener(e -> {
            chess.pieceAtSquare(0);
            Object obj = null;
            try {
                obj = ReadWrite.readFromFile(storagePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (obj != null) {
                chess = (Chess) obj;
                mainWindow.boardCanvas.setBoard(chess.getBoard());
                System.out.println("SUCCESSFULLY LOADED");
                mainWindow.boardCanvas.repaint();
            }
        });
        mainWindow.item_begin.addActionListener(e -> {
            System.out.println("NOT YET IMPLEMENTED");
            {}
        });
        mainWindow.boardCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int btn = mouseEvent.getButton();
                switch (btn) {
                    case 1: {
                        movePieceFromEvent(mouseEvent);
                        break;
                    }
                    case 2: {
                        chess.whitePieces.printThreats();
                        chess.blackPieces.printThreats();
                        System.out.println();
                        break;
                    }
                    case 3: {
                        movePieceFromEvent(mouseEvent);
                        chess.whitePieces.print();
                        chess.blackPieces.print();
                        System.out.println();
                        break;
                    }
                }
            }
        });
    }

    private void movePieceFromEvent(MouseEvent mouseEvent){
        byte pos = Parser.coordFromEvent(mouseEvent,
                mainWindow.boardCanvas.s.offset,
                mainWindow.boardCanvas.s.size_factor);

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

        mainWindow.boardCanvas.refresh(true, true,
                chess.getPseudoLegalMoves().getMovesFrom(pos), lastMove);
    }
}
