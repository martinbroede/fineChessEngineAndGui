package gui.chessBoard;

import core.Move;
import core.Moves;
import core.Util;

import java.awt.*;

public class Painter {

    protected static void paintBoard(Graphics g, AppearanceSettings settings) {

        g.setColor(settings.getColorScheme().MARGIN_COLOR);
        g.fillRect(0, 0, settings.getMargin(), settings.getMargin());
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (((x + y) % 2) == 0) {
                    g.setColor(settings.getColorScheme().WHITE_SQUARES_COLOR);
                } else g.setColor(settings.getColorScheme().BLACK_SQUARES_COLOR);

                g.fillRect(x * settings.getSizeFactor() + settings.getOffset(),
                        y * settings.getSizeFactor() + settings.getOffset(),
                        settings.getSizeFactor(), settings.getSizeFactor());

                g.setColor(settings.getColorScheme().FILL_COLOR);
                g.drawRect(x * settings.getSizeFactor() + settings.getOffset(),
                        y * settings.getSizeFactor() + settings.getOffset(),
                        settings.getSizeFactor(), settings.getSizeFactor());
            }
        }
    }

    protected static void paintHints(Graphics g, AppearanceSettings settings, boolean boardOrientation,
                                     byte[] combinedThreats, byte[] whiteThreats, byte[] blackThreats) {

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {

                int pos = boardOrientation ? x + (7 - y) * 8 : (7 - x) + y * 8;

                byte val = combinedThreats[pos];
                if (boardOrientation) { // white's point of view
                    if (val == Byte.MAX_VALUE) g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR.brighter());
                    else if (val > 0) g.setColor(settings.getColorScheme().HIGHLIGHT_1_COLOR.brighter());
                    else g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR);
                    if (blackThreats[pos] == 0) g.setColor(settings.getColorScheme().HIGHLIGHT_1_COLOR);
                } else { //black's point of view
                    if (val == Byte.MAX_VALUE) g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR.brighter());
                    else if (val < 0) g.setColor(settings.getColorScheme().HIGHLIGHT_1_COLOR.brighter());
                    else g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR);
                    if (whiteThreats[pos] == 0) g.setColor(settings.getColorScheme().HIGHLIGHT_1_COLOR);
                }

                g.fillRect(x * settings.getSizeFactor() + settings.getOffset(),
                        y * settings.getSizeFactor() + settings.getOffset(),
                        settings.getSizeFactor(), settings.getSizeFactor());

                g.setColor(settings.getColorScheme().FILL_COLOR);
                g.drawRect(x * settings.getSizeFactor() + settings.getOffset(),
                        y * settings.getSizeFactor() + settings.getOffset(),
                        settings.getSizeFactor(), settings.getSizeFactor());
            }
        }
    }

    protected static void paintPieces(Graphics g, AppearanceSettings settings, char[] board, boolean boardOrientation) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x_factor, y_factor, x_offset, y_offset;
        x_factor = settings.getSizeFactor();
        y_factor = settings.getSizeFactor();
        switch (settings.font.getFontName()) {
            case "DejaVu Sans":
                x_offset = +settings.getOffset() + settings.getSizeFactor() / 2
                        - settings.font.getSize() / 2 + settings.getOffset() / 5;
                y_offset = settings.getOffset() + settings.getSizeFactor() / 2
                        + settings.font.getSize() * 2 / 5 + settings.getOffset() / 5;
                break;
            case "Chess Regular":
                x_offset = settings.getOffset() + settings.getSizeFactor() * 29 / 100; // ++:=> ### --:<=
                y_offset = settings.getOffset() + settings.getSizeFactor() * 90 / 100; // --:up ### ++:down
                break;
            case "Dialog.plain": // "Times"
            case "MS Gothic":
            default:
                x_offset = settings.getOffset() + settings.getSizeFactor() / 2
                        - settings.font.getSize() / 2;
                y_offset = settings.getOffset() + settings.getSizeFactor() / 2
                        + settings.font.getSize() * 2 / 5;
                break;
        }

        Color dark = settings.getColorScheme().PIECE_COLOR;
        g2.setColor(dark);
        g2.setFont(settings.font);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pos = boardOrientation ? x + (7 - y) * 8 : (7 - x) + y * 8;
                String piece;
                switch (settings.font.getFontName()) {

                    case "DejaVu Sans":
                    case "Dialog.plain": // "Times"
                    case "MS Gothic":
                        piece = Character.toString(Util.parseSymbol(board[pos]));
                        break;
                    case "Chess Regular":
                        piece = Character.toString(Util.parseSarahFont(board[pos]));
                        break;
                    default:
                        piece = Character.toString(Util.parseSymbolFromChessFont(board[pos]));
                        break;
                }
                g2.drawString(piece,
                        x * x_factor + x_offset,
                        y * y_factor + y_offset);
            }
        }
    }

    protected static void paintFilesAndRanks(Graphics g, AppearanceSettings settings, boolean boardOrientation) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Font rankFont = new Font("Times", Font.PLAIN, settings.getSizeFactor() * 15 / 100);
        int factor = settings.getSizeFactor();
        int offset = factor * 78 / 100;
        g2.setFont(rankFont);
        for (int x = 0; x < 8; x++) {
            int file = boardOrientation ? x : 7 - x;
            int y = 7;
            g2.setColor(settings.getColorScheme().PIECE_COLOR);
            g2.drawString("" + Util.getFileName(file),
                    x * factor + settings.getOffset()
                            + factor / 2 - offset / 2,
                    y * factor + settings.getOffset()
                            + factor / 2 + offset * 45 / 100);
        }
        for (int y = 1; y < 8; y++) {
            int rank = boardOrientation ? y : 7 - y;
            g2.setColor(settings.getColorScheme().PIECE_COLOR);
            g2.drawString("" + Util.getRankName(rank),
                    settings.getOffset() + factor / 2 - offset / 2,
                    (7 - y) * factor + settings.getOffset()
                            + factor / 2 + offset * 45 / 100);
        }
    }

    protected static void paintHighlights(Graphics g, AppearanceSettings settings, Moves squares,
                                          boolean boardOrientation) {

        if (squares == null) return;
        Color highlight = settings.getColorScheme().HIGHLIGHT_1_COLOR;
        for (Move square : squares) {
            int x = boardOrientation ? square.getTo() % 8 : 7 - square.getTo() % 8;
            int y = boardOrientation ? 7 - square.getTo() / 8 : square.getTo() / 8;
            g.setColor(highlight);
            g.fillRect(x * settings.getSizeFactor() + settings.getOffset(),
                    y * settings.getSizeFactor() + settings.getOffset(),
                    settings.getSizeFactor(), settings.getSizeFactor());
            g.setColor(settings.getColorScheme().FILL_COLOR);
            g.drawRect(x * settings.getSizeFactor() + settings.getOffset(),
                    y * settings.getSizeFactor() + settings.getOffset(),
                    settings.getSizeFactor(), settings.getSizeFactor());
        }
    }

    protected static void paintLastMove(Graphics g, AppearanceSettings settings, short moveInformation,
                                        boolean boardOrientation) {

        if (moveInformation < 0) return;
        int diminish = settings.getSizeFactor() / 13;
        int from = Move.getFrom(moveInformation);
        int to = Move.getTo(moveInformation);
        int[] squares = {from, to};
        for (int square : squares) {
            int x = boardOrientation ? square % 8 : 7 - square % 8;
            int y = boardOrientation ? 7 - square / 8 : square / 8;
            g.setColor(settings.getColorScheme().MARGIN_COLOR);
            g.drawRect(x * settings.getSizeFactor() + settings.getOffset() + diminish,
                    y * settings.getSizeFactor() + settings.getOffset() + diminish,
                    settings.getSizeFactor() - 2 * diminish, settings.getSizeFactor() - 2 * diminish);
            g.drawRect(x * settings.getSizeFactor() + settings.getOffset() + diminish + 1,
                    y * settings.getSizeFactor() + settings.getOffset() + diminish + 1,
                    settings.getSizeFactor() - 2 * diminish - 2,
                    settings.getSizeFactor() - 2 * diminish - 2);
        }
    }
}