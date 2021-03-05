package gui.chessBoard;

import core.Move;
import core.Moves;
import core.Parser;

import java.awt.*;

public class Painter {

    protected static void paintBoard(Graphics g, CanvasSettings settings) {
        g.setFont(settings.font);
        g.setColor(settings.COLORS.MARGIN_COLOR);
        g.fillRect(0, 0, settings.margin, settings.margin);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (((x + y) % 2) == 0) {
                    g.setColor(settings.COLORS.WHITE_SQUARES_COLOR);
                } else g.setColor(settings.COLORS.BLACK_SQUARES_COLOR);
                g.fillRect(x * settings.size_factor + settings.offset,
                        y * settings.size_factor + settings.offset,
                        settings.size_factor, settings.size_factor);
                g.setColor(settings.COLORS.FILL_COLOR);
                g.drawRect(x * settings.size_factor + settings.offset, y * settings.size_factor + settings.offset,
                        settings.size_factor, settings.size_factor);
            }
        }
    }

    protected static void paintPieces(Graphics g, CanvasSettings s, char[] board) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(s.font);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pos = x + (7 - y) * 8;
                g2.setColor(s.COLORS.PIECE_COLOR);
                switch (s.font.getFontName()) {
                    case "DejaVu Sans":
                        g2.drawString("" + Parser.parseSymbol(board[pos]),
                                x * s.size_factor + s.offset + s.size_factor / 2 - s.font.getSize() / 2 + s.offset / 3,
                                y * s.size_factor + s.offset + s.size_factor / 2 + s.font.getSize() * 2 / 5 + s.offset / 5);
                        break;
                    case "Dialog.plain": // "Times"
                    case "MS Gothic":
                        g2.drawString("" + Parser.parseSymbol(board[pos]),
                                x * s.size_factor + s.offset + s.size_factor / 2 - s.font.getSize() / 2,
                                y * s.size_factor + s.offset + s.size_factor / 2 + s.font.getSize() * 2 / 5);
                        break;
                    case "Chess Maya":
                        g2.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * s.size_factor + s.offset + s.size_factor / 2 - s.font.getSize() / 2,
                                y * s.size_factor + s.offset + s.size_factor / 2 + s.font.getSize() * 4 / 11 + s.offset);
                        break;
                    default:
                        g2.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * s.size_factor + s.offset + s.size_factor / 2 - s.font.getSize() / 2,
                                y * s.size_factor + s.offset + s.size_factor / 2 + s.font.getSize() * 2 / 5);
                        break;
                }
            }
        }
    }

    protected static void paintFilesAndRanks(Graphics g, CanvasSettings s) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Font rankFont = new Font("Times", Font.PLAIN, s.size_factor * 15 / 100);
        g2.setFont(rankFont);
        for (int x = 0; x < 8; x++) {
            int y = 7;
            g2.setColor(s.COLORS.PIECE_COLOR);
            g2.drawString("" + Parser.getFileName(x),
                    x * s.size_factor + s.offset + s.size_factor / 2 - s.font.getSize() / 2,
                    y * s.size_factor + s.offset + s.size_factor / 2 + s.font.getSize() * 45 / 100);
        }
        for (int y = 1; y < 8; y++) {
            g2.setColor(s.COLORS.PIECE_COLOR);
            g2.drawString("" + Parser.getRankName(y),
                    s.offset + s.size_factor / 2 - s.font.getSize() / 2,
                    (7 - y) * s.size_factor + s.offset + s.size_factor / 2 + s.font.getSize() * 45 / 100);
        }
    }

    protected static void paintHighlights(Graphics g, CanvasSettings s, Moves squares, boolean Ok) {
        if (squares == null) return;
        Color highlight;
        if (Ok)
            highlight = s.COLORS.HIGHLIGHT_1_COLOR;
        else
            highlight = s.COLORS.HIGHLIGHT_2_COLOR;
        for (Move square : squares) {
            int x = square.getTo() % 8;
            int y = 7 - square.getTo() / 8;
            g.setColor(highlight);
            g.fillRect(x * s.size_factor + s.offset, y * s.size_factor + s.offset,
                    s.size_factor, s.size_factor);
            g.setColor(s.COLORS.FILL_COLOR);
            g.drawRect(x * s.size_factor + s.offset, y * s.size_factor + s.offset,
                    s.size_factor, s.size_factor);
        }
    }

    protected static void paintLastMove(Graphics g, CanvasSettings s,Move move) {
        if (move == null) return;
        int diminish = s.size_factor / 13;
        int[] squares = {move.getFrom(), move.getTo()};
        for (int square : squares) {
            int x = square % 8;
            int y = 7 - square / 8;
            g.setColor(s.COLORS.MARGIN_COLOR);
            g.drawRect(x * s.size_factor + s.offset + diminish,
                    y * s.size_factor + s.offset + diminish,
                    s.size_factor - 2 * diminish, s.size_factor - 2 * diminish);
            g.drawRect(x * s.size_factor + s.offset + diminish + 1,
                    y * s.size_factor + s.offset + diminish + 1,
                    s.size_factor - 2 * diminish - 2, s.size_factor - 2 * diminish - 2);
        }
    }
}
