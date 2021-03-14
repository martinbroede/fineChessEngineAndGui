package gui.chessBoard;

import core.Moves;
import core.Parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Board extends JPanel {

    final boolean WHITE_PLAYER_SOUTH = true;
    final boolean WHITE_PLAYER_NORTH = false;
    public AppearanceSettings s;
    char[] boardArray;
    private boolean active;
    private BufferedImage img;
    private Graphics bufferGraphics;
    private boolean boardOrientation = WHITE_PLAYER_NORTH;

    public Board(int size_factor, char[] boardArray, AppearanceSettings appearanceSettings) {

        active = true;
        s = appearanceSettings;
        s.adjustSize(size_factor);
        this.boardArray = boardArray;
        adjustSize();
    }

    public void setActive(boolean active) {

        this.active = active;
        paint(getGraphics());
    }

    public void toggleBoardOrientation() {
        boardOrientation = !boardOrientation;
    }

    public void setWhitePlayerSouth() {
        boardOrientation = WHITE_PLAYER_SOUTH;
    }

    public void setWhitePlayerNorth() {
        boardOrientation = WHITE_PLAYER_NORTH;
    }

    public void setBoardArray(char[] boardArray) {
        this.boardArray = boardArray;
    }

    public void fontRoulette() {

        s.nextFont();
        adjustSize();
        repaint();
    }

    public void adjustSize() {

        img = new BufferedImage(s.getMargin(), s.getMargin(), BufferedImage.TYPE_INT_RGB);
        bufferGraphics = img.getGraphics();
        Dimension newDimension = new Dimension(s.getMargin(), s.getMargin());
        setSize(newDimension);
        setPreferredSize(newDimension);
    }

    public void refreshChessBoard(boolean showMoves, boolean showLastMove, Moves moves, short move) {
        active = true;

        Painter.paintBoard(bufferGraphics, s, boardOrientation);
        if (showMoves) Painter.paintHighlights(bufferGraphics, s, moves, true, boardOrientation);
        if (showLastMove) Painter.paintLastMove(bufferGraphics, s, move, boardOrientation);
        Painter.paintPieces(bufferGraphics, s, boardArray, boardOrientation);
        Painter.paintFilesAndRanks(bufferGraphics, s, boardOrientation);

        getGraphics().drawImage(img, 0, 0, this);
    }

    public byte coordFromEvent(MouseEvent e, int offset, int size_factor) {

        int x = (e.getX() - offset) / size_factor;
        int y = 7 - (e.getY() - offset) / size_factor;

        x = boardOrientation? x : 7 - x;
        y = boardOrientation? y : 7 - y;

        return Parser.parse(x, y);
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
            Painter.paintBoard(bufferGraphics, s, boardOrientation);
            Painter.paintPieces(bufferGraphics, s, boardArray, boardOrientation);
            Painter.paintFilesAndRanks(bufferGraphics, s, boardOrientation);
        }

        if (img != null) {
            if (!active) {
                // paint board diffuse
                bufferGraphics.setColor(s.getColorScheme().LIGHT_COLOR);
                bufferGraphics.fillRect(0, 0, s.getMargin(), s.getMargin());
            }
            g.drawImage(img, 0, 0, this);
        }
    }
}