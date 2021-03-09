package gui.chessBoard;

import core.Moves;
import gui.ColorScheme;

import java.awt.*;

public class Board extends Canvas {

    public AppearanceSettings s;
    char [] board;

    public Board(int size_factor, char[] boardArray, AppearanceSettings appearanceSettings) {
        s = appearanceSettings;
        this.board = boardArray;
        adjustSize(size_factor);
    }

    public int getOffset(){
        return s.getOffset();
    }

    public int getSizeFactor(){
        return s.getSizeFactor();
    }

    public void adjustSize(int size_factor) {
        s.adjustSize(size_factor);
        setSize(s.getMargin(), s.getMargin());
    }

    public void setBoard(char[] board) {
        this.board = board;
    }

    public void fontRoulette() {
        s.nextFont();
        adjustSize(s.getSizeFactor());
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
        g.setColor(s.getColorScheme().LIGHT_COLOR);
        g.fillRect(0, 0, s.getMargin(), s.getMargin());
    }

    @Override
    public void paint(Graphics g) {
        Painter.paintBoard(g, s);
        Painter.paintPieces(g, s, board);
        Painter.paintFilesAndRanks(g, s);
    }
}