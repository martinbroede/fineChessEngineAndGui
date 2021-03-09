package gui.chessBoard;

import core.Move;
import core.Moves;
import core.Parser;

import java.awt.*;

public class Painter {

    protected static void paintBoard(Graphics g, appearanceSettings settings) {
        g.setFont(settings.font);
        g.setColor(settings.ColorScheme.MARGIN_COLOR);
        g.fillRect(0, 0, settings.margin, settings.margin);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (((x + y) % 2) == 0) {
                    g.setColor(settings.ColorScheme.WHITE_SQUARES_COLOR);
                } else g.setColor(settings.ColorScheme.BLACK_SQUARES_COLOR);
                g.fillRect(x * settings.sizeFactor + settings.offset,
                        y * settings.sizeFactor + settings.offset,
                        settings.sizeFactor, settings.sizeFactor);
                g.setColor(settings.ColorScheme.FILL_COLOR);
                g.drawRect(x * settings.sizeFactor + settings.offset, y * settings.sizeFactor + settings.offset,
                        settings.sizeFactor, settings.sizeFactor);
            }
        }
    }

    protected static void paintPieces(Graphics g, appearanceSettings s, char[] board) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(s.font);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pos = x + (7 - y) * 8;
                g2.setColor(s.ColorScheme.PIECE_COLOR);
                switch (s.font.getFontName()) {
                    case "DejaVu Sans":
                        g2.drawString("" + Parser.parseSymbol(board[pos]),
                                x * s.sizeFactor + s.offset + s.sizeFactor / 2 - s.font.getSize() / 2 + s.offset / 3,
                                y * s.sizeFactor + s.offset + s.sizeFactor / 2 + s.font.getSize() * 2 / 5 + s.offset / 5);
                        break;
                    case "Dialog.plain": // "Times"
                    case "MS Gothic":
                        g2.drawString("" + Parser.parseSymbol(board[pos]),
                                x * s.sizeFactor + s.offset + s.sizeFactor / 2 - s.font.getSize() / 2,
                                y * s.sizeFactor + s.offset + s.sizeFactor / 2 + s.font.getSize() * 2 / 5);
                        break;
                    case "Chess Maya":
                        g2.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * s.sizeFactor + s.offset + s.sizeFactor / 2 - s.font.getSize() / 2,
                                y * s.sizeFactor + s.offset + s.sizeFactor / 2 + s.font.getSize() * 4 / 11 + s.offset);
                        break;
                    default:
                        g2.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * s.sizeFactor + s.offset + s.sizeFactor / 2 - s.font.getSize() / 2,
                                y * s.sizeFactor + s.offset + s.sizeFactor / 2 + s.font.getSize() * 2 / 5);
                        break;
                }
            }
        }
    }

    protected static void paintFilesAndRanks(Graphics g, appearanceSettings s) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Font rankFont = new Font("Times", Font.PLAIN, s.sizeFactor * 15 / 100);
        g2.setFont(rankFont);
        for (int x = 0; x < 8; x++) {
            int y = 7;
            g2.setColor(s.ColorScheme.PIECE_COLOR);
            g2.drawString("" + Parser.getFileName(x),
                    x * s.sizeFactor + s.offset + s.sizeFactor / 2 - s.font.getSize() / 2,
                    y * s.sizeFactor + s.offset + s.sizeFactor / 2 + s.font.getSize() * 45 / 100);
        }
        for (int y = 1; y < 8; y++) {
            g2.setColor(s.ColorScheme.PIECE_COLOR);
            g2.drawString("" + Parser.getRankName(y),
                    s.offset + s.sizeFactor / 2 - s.font.getSize() / 2,
                    (7 - y) * s.sizeFactor + s.offset + s.sizeFactor / 2 + s.font.getSize() * 45 / 100);
        }
    }

    protected static void paintHighlights(Graphics g, appearanceSettings s, Moves squares, boolean Ok) {
        if (squares == null) return;
        Color highlight;
        if (Ok)
            highlight = s.ColorScheme.HIGHLIGHT_1_COLOR;
        else
            highlight = s.ColorScheme.HIGHLIGHT_2_COLOR;
        for (Move square : squares) {
            int x = square.getTo() % 8;
            int y = 7 - square.getTo() / 8;
            g.setColor(highlight);
            g.fillRect(x * s.sizeFactor + s.offset, y * s.sizeFactor + s.offset,
                    s.sizeFactor, s.sizeFactor);
            g.setColor(s.ColorScheme.FILL_COLOR);
            g.drawRect(x * s.sizeFactor + s.offset, y * s.sizeFactor + s.offset,
                    s.sizeFactor, s.sizeFactor);
        }
    }

    protected static void paintLastMove(Graphics g, appearanceSettings s, short moveInformation) {
        if (moveInformation < 0) return;
        int diminish = s.sizeFactor / 13;
        int from = Move.getFrom(moveInformation);
        int to = Move.getTo(moveInformation);
        int[] squares = { from, to};
        for (int square : squares) {
            int x = square % 8;
            int y = 7 - square / 8;
            g.setColor(s.ColorScheme.MARGIN_COLOR);
            g.drawRect(x * s.sizeFactor + s.offset + diminish,
                    y * s.sizeFactor + s.offset + diminish,
                    s.sizeFactor - 2 * diminish, s.sizeFactor - 2 * diminish);
            g.drawRect(x * s.sizeFactor + s.offset + diminish + 1,
                    y * s.sizeFactor + s.offset + diminish + 1,
                    s.sizeFactor - 2 * diminish - 2, s.sizeFactor - 2 * diminish - 2);
        }
    }
}
