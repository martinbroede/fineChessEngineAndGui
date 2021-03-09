package gui.chessBoard;

import core.Moves;
import gui.ColorScheme;

import java.awt.*;


public class Board extends Canvas {

    public appearanceSettings s;
    char [] board;

    public Board(int size_factor, char[] board, ColorScheme ColorScheme) {
        s = new appearanceSettings(ColorScheme); //todo refactor to MainWindow
        this.board = board;
        adjustSize(size_factor);
    }

    public int getOffset(){
        return s.offset;
    }

    public int getSizeFactor(){
        return s.sizeFactor;
    }

    public void adjustSize(int size_factor) {
        s.adjustSize(size_factor);
        setSize(s.margin, s.margin);
    }

    public void setBoard(char[] board) {
        this.board = board;
    }

    public void fontRoulette() {
        s.nextFont();
        adjustSize(s.sizeFactor);
        repaint();
    }

    public void repaint() {
        paint(getGraphics());
    }

    public void refresh(boolean showMoves, boolean showLastMove, Moves moves, short move) {
        Graphics g = getGraphics();
        Painter.paintBoard(g, s);
        if (showMoves) Painter.paintHighlights(g,s,moves, true);
        if (showLastMove) Painter.paintLastMove(g,s,move);
        Painter.paintPieces(g, s, board);
        Painter.paintFilesAndRanks(g, s);
    }

    public void paintDiffus() {
        paint(getGraphics());
        Graphics g = getGraphics();
        g.setColor(s.ColorScheme.LIGHT_COLOR);
        g.fillRect(0, 0, s.margin, s.margin);
    }

    @Override
    public void paint(Graphics g) {
        Painter.paintBoard(g, s);
        Painter.paintPieces(g, s, board);
        Painter.paintFilesAndRanks(g, s);
    }
}