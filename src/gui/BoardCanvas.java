package gui;


import core.Move;
import core.Moves;
import core.Parser;

import java.awt.*;

import static fileHandling.ReadWrite.createFontFromFile;


class BoardCanvas extends Canvas {
    private final Colors COLORS;
    private final Font[] FONT_ROULETTE = {
            createFontFromFile("DejaVuSans", 1),
            new Font("Times", Font.PLAIN, 1),
            new Font("MS Gothic", Font.PLAIN, 1),
            createFontFromFile("MAYAFONT", 1)};
    protected int size_factor;
    protected int offset;
    protected int margin;
    char[] board;
    private Font font;
    private int fontNumber;


    protected BoardCanvas(int size_factor, char[] board, Colors COLORS) {
        this.board = board;
        this.COLORS = COLORS;
        this.font = FONT_ROULETTE[0];
        adjustSize(size_factor);
    }

    protected void adjustSize(int size_factor) {
        this.size_factor = size_factor;
        font = font.deriveFont((float) this.size_factor * 78 / 100);
        offset = size_factor / 6;
        margin = size_factor * 8 + 2 * offset;
        setSize(margin, margin);
    }

    protected void setBoard(char[] board) {
        this.board = board;
    }

    protected void fontRoulette() {
        fontNumber++;
        this.font = FONT_ROULETTE[fontNumber % FONT_ROULETTE.length];
        adjustSize(this.size_factor);
        repaint();
    }

    protected void paintBoard() {
        Graphics g = getGraphics();
        g.setFont(font);
        g.setColor(COLORS.MARGIN_COLOR);
        g.fillRect(0, 0, margin, margin);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (((x + y) % 2) == 0) {
                    g.setColor(COLORS.WHITE_SQUARES_COLOR);
                } else g.setColor(COLORS.BLACK_SQUARES_COLOR);
                g.fillRect(x * size_factor + offset, y * size_factor + offset,
                        size_factor, size_factor);
                g.setColor(COLORS.FILL_COLOR);
                g.drawRect(x * size_factor + offset, y * size_factor + offset,
                        size_factor, size_factor);
            }
        }
    }

    protected void paintHighlights(Moves squares, boolean Ok) {
        if (squares == null) return;
        Color highlight;
        Graphics g = getGraphics();
        if (Ok)
            highlight = COLORS.HIGHLIGHT_1_COLOR;
        else
            highlight = COLORS.HIGHLIGHT_2_COLOR;
        for (Move square : squares) {
            int x = square.getTo() % 8;
            int y = 7 - square.getTo() / 8;
            g.setColor(highlight);
            g.fillRect(x * size_factor + offset, y * size_factor + offset,
                    size_factor, size_factor);
            g.setColor(COLORS.FILL_COLOR);
            g.drawRect(x * size_factor + offset, y * size_factor + offset,
                    size_factor, size_factor);
        }
    }

    protected void paintLastMove(Move move) {
        if (move == null) return;
        Graphics g = getGraphics();
        int diminish = size_factor / 13;
        int[] squares = {move.getFrom(), move.getTo()};
        for (int square : squares) {
            int x = square % 8;
            int y = 7 - square / 8;
            g.setColor(COLORS.MARGIN_COLOR);
            g.drawRect(x * size_factor + offset + diminish, y * size_factor + offset + diminish,
                    size_factor - 2 * diminish, size_factor - 2 * diminish);
            g.drawRect(x * size_factor + offset + diminish + 1, y * size_factor + offset + diminish + 1,
                    size_factor - 2 * diminish - 2, size_factor - 2 * diminish - 2);
        }
    }

    protected void paintPieces() {
        Graphics2D g = (Graphics2D) getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(font);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pos = x + (7 - y) * 8;
                g.setColor(COLORS.PIECE_COLOR);
                switch (font.getFontName()) {
                    case "DejaVu Sans":
                        g.drawString("" + Parser.parseSymbol(board[pos]),
                                x * size_factor + offset + size_factor / 2 - font.getSize() / 2 + offset / 3,
                                y * size_factor + offset + size_factor / 2 + font.getSize() * 2 / 5 + offset / 5);
                        break;
                    case "Dialog.plain": // "Times"
                    case "MS Gothic":
                        g.drawString("" + Parser.parseSymbol(board[pos]),
                                x * size_factor + offset + size_factor / 2 - font.getSize() / 2,
                                y * size_factor + offset + size_factor / 2 + font.getSize() * 2 / 5);
                        break;
                    case "Chess Maya":
                        g.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * size_factor + offset + size_factor / 2 - font.getSize() / 2,
                                y * size_factor + offset + size_factor / 2 + font.getSize() * 4 / 11 + offset);
                        break;
                    default:
                        g.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * size_factor + offset + size_factor / 2 - font.getSize() / 2,
                                y * size_factor + offset + size_factor / 2 + font.getSize() * 2 / 5);
                        break;
                }
            }
        }
    }

    protected void paintFilesAndRanks() {
        Graphics2D g = (Graphics2D) getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Font rankFont = new Font("Times", Font.PLAIN, size_factor * 15 / 100);
        g.setFont(rankFont);
        for (int x = 0; x < 8; x++) {
            int y = 7;
            g.setColor(COLORS.PIECE_COLOR);
            g.drawString("" + Parser.getFileName(x),
                    x * size_factor + offset + size_factor / 2 - font.getSize() / 2,
                    y * size_factor + offset + size_factor / 2 + font.getSize() * 45 / 100);
        }
        for (int y = 1; y < 8; y++) {
            g.setColor(COLORS.PIECE_COLOR);
            g.drawString("" + Parser.getRankName(y),
                    offset + size_factor / 2 - font.getSize() / 2,
                    (7 - y) * size_factor + offset + size_factor / 2 + font.getSize() * 45 / 100);
        }
    }

    public void repaint() {
        paint(getGraphics());
    }

    public void refresh(boolean showMoves, boolean showLastMove, Moves moves, Move move) {
        paintBoard();
        if (showMoves) paintHighlights(moves, true);
        if (showLastMove) paintLastMove(move);
        paintPieces();
        paintFilesAndRanks();
    }

    public void paintDiffus() {
        paint(getGraphics());
        Graphics g = getGraphics();
        g.setColor(COLORS.LIGHT_COLOR);
        g.fillRect(0, 0, margin, margin);
    }

    @Override
    public void paint(Graphics g) {
        paintBoard();
        paintPieces();
        paintFilesAndRanks();
    }
}