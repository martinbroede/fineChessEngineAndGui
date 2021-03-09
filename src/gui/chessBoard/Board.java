package gui.chessBoard;

import core.Moves;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Board extends JPanel {

    public AppearanceSettings s;
    char[] boardArray;
    private boolean active;
    private BufferedImage img;
    private Graphics bufferGraphics;

    public Board(int size_factor, char[] boardArray, AppearanceSettings appearanceSettings) {

        active = true;
        s = appearanceSettings;
        this.boardArray = boardArray;
        adjustSize(size_factor);
    }

    public void setActive(boolean active) {
        this.active = active;
        paint(getGraphics());
    }

    public int getOffset() {
        return s.getOffset();
    }

    public int getSizeFactor() {
        return s.getSizeFactor();
    }

    public void adjustSize(int size_factor) {

        s.adjustSize(size_factor);
        img = new BufferedImage(s.getMargin(), s.getMargin(), BufferedImage.TYPE_INT_RGB);
        bufferGraphics = img.getGraphics();
        Dimension newDimension = new Dimension(s.getMargin(), s.getMargin());
        setSize(newDimension);
        setPreferredSize(newDimension);
    }

    public void setBoardArray(char[] boardArray) {
        this.boardArray = boardArray;
    }

    public void fontRoulette() {
        s.nextFont();
        adjustSize(s.getSizeFactor());
        repaint();
    }

    public void refreshChessBoard(boolean showMoves, boolean showLastMove, Moves moves, short move) {
        active = true;

        Painter.paintBoard(bufferGraphics, s);
        if (showMoves) Painter.paintHighlights(bufferGraphics, s, moves, true);
        if (showLastMove) Painter.paintLastMove(bufferGraphics, s, move);
        Painter.paintPieces(bufferGraphics, s, boardArray);
        Painter.paintFilesAndRanks(bufferGraphics, s);

        getGraphics().drawImage(img, 0, 0, this);
    }

    @Override
    public int getWidth() {
        if (s != null)
            return s.getMargin();
        else return 0;
    }

    @Override
    public int getHeight() {
        if (s != null)
            return s.getMargin();
        else return 0;
    }

    @Override
    public void repaint() {
        paint(getGraphics());
    }

    @Override
    public void paint(Graphics g) {

        if (bufferGraphics != null) {

            Painter.paintBoard(bufferGraphics, s);
            Painter.paintPieces(bufferGraphics, s, boardArray);
            Painter.paintFilesAndRanks(bufferGraphics, s);
        } else System.err.println("bufferGraphics == null");

        if (img != null) {
            if (!active) {
                // paint board diffuse
                bufferGraphics.setColor(s.getColorScheme().LIGHT_COLOR);
                bufferGraphics.fillRect(0, 0, s.getMargin(), s.getMargin());
            }
            g.drawImage(img, 0, 0, this);

        } else System.err.println("img == null");
    }
}