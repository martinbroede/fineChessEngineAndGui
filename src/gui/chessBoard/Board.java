package gui.chessBoard;

import core.ChessModel;
import core.Moves;
import core.Util;
import gui.AppearanceSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Board extends JPanel {

    public final AppearanceSettings s;
    final boolean WHITE_PLAYER_SOUTH = true;
    final boolean WHITE_PLAYER_NORTH = false;
    final char[] boardArray;
    private final ChessModel chess;
    private boolean active;
    private BufferedImage img;
    private Graphics bufferGraphics;
    private boolean boardOrientation = WHITE_PLAYER_SOUTH;
    private boolean showHints = false;

    public Board(int size_factor, ChessModel chessModel, AppearanceSettings appearanceSettings) {

        active = true;
        s = appearanceSettings;
        s.adjustSize(size_factor);
        boardArray = chessModel.getBoard();
        adjustSize();
        this.chess = chessModel;
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

    public void fontRoulette() {

        s.nextFont();
        adjustSize();
        repaint();
    }

    public void toggleShowHints() {
        showHints = !showHints;
    }

    public void adjustSize() {

        img = new BufferedImage(s.margin, s.margin, BufferedImage.TYPE_INT_RGB);
        bufferGraphics = img.getGraphics();
        Dimension newDimension = new Dimension(s.margin, s.margin);
        setSize(newDimension);
        setPreferredSize(newDimension);
        paint(getGraphics());
    }

    public void refreshChessBoard(boolean showMoves, boolean showLastMove, Moves moves, short move) {

        active = true;

        Painter.paintBoard(bufferGraphics, s);
        if (showHints) Painter.paintHints(bufferGraphics, s, boardOrientation,
                chess.getCombinedThreats(), chess.getWhiteThreats(), chess.getBlackThreats());
        else if (showMoves) Painter.paintHighlights(bufferGraphics, s, moves, boardOrientation);
        if (showLastMove) Painter.paintLastMove(bufferGraphics, s, move, boardOrientation);
        Painter.paintPieces(bufferGraphics, s, boardArray, boardOrientation);
        Painter.paintFilesAndRanks(bufferGraphics, s, boardOrientation);
        getGraphics().drawImage(img, 0, 0, this);
    }

    public byte coordFromEvent(MouseEvent e) {

        int x = (e.getX() - s.offset) / s.sizeFactor;
        int y = 7 - (e.getY() - s.offset) / s.sizeFactor;

        x = boardOrientation ? x : 7 - x;
        y = boardOrientation ? y : 7 - y;

        return Util.parse(x, y);
    }

    @Override
    public int getWidth() {
        if (s != null)
            return s.margin;
        else return 0;
    }

    @Override
    public int getHeight() {
        if (s != null)
            return s.margin;
        else return 0;
    }

    @Override
    public void repaint() {
        paint(getGraphics());
    }

    @Override
    public void paint(Graphics g) {

        if (bufferGraphics != null && img != null) {
            Painter.paintBoard(bufferGraphics, s);
            Painter.paintPieces(bufferGraphics, s, boardArray, boardOrientation);
            Painter.paintFilesAndRanks(bufferGraphics, s, boardOrientation);
        }

        if (!active) {
            // paint board diffuse
            if (bufferGraphics != null && img != null) {
                bufferGraphics.setColor(s.colorScheme.LIGHT_COLOR);
                bufferGraphics.fillRect(0, 0, s.margin, s.margin);
            }
        }

        if (img != null && g != null) g.drawImage(img, 0, 0, this);
    }
}