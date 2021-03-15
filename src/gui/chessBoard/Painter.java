package gui.chessBoard;

import core.Move;
import core.Moves;
import core.Parser;

import java.awt.*;

public class Painter {

    protected static void paintBoard(Graphics g, AppearanceSettings settings, boolean boardOrientation) {

        int orientation = boardOrientation ? 0 : 1;

        g.setColor(settings.getColorScheme().MARGIN_COLOR);
        g.fillRect(0, 0, settings.getMargin(), settings.getMargin());
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (((x + y) % 2) == orientation) {
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
                                     byte[] combinedThreats, byte[] whiteThreats, byte[] blackThreats){

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {

                int pos = boardOrientation ? x + (7 - y) * 8 : (7 - x) + y * 8;

                g.setColor(Color.black);//todo remove

                if (boardOrientation) { // white's point of view
                    byte val = combinedThreats[pos];
                    if(val == Byte.MAX_VALUE) continue; //threats balanced.
                    else if(val > 0) g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR.brighter());
                    else g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR);
                    if(blackThreats[pos] == 0) g.setColor(settings.getColorScheme().HIGHLIGHT_1_COLOR);
                } else{ //black's point of view
                    byte val = combinedThreats[pos];
                    if(val == Byte.MAX_VALUE) continue; //threats balanced.
                    else if(val < 0) g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR.brighter());
                    else g.setColor(settings.getColorScheme().HIGHLIGHT_2_COLOR);
                    if(whiteThreats[pos] == 0) g.setColor(settings.getColorScheme().HIGHLIGHT_1_COLOR);
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

        g2.setFont(settings.font);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int pos = boardOrientation ? x + (7 - y) * 8 : (7 - x) + y * 8;

                g2.setColor(settings.getColorScheme().PIECE_COLOR);
                switch (settings.font.getFontName()) {
                    case "DejaVu Sans":
                        g2.drawString("" + Parser.parseSymbol(board[pos]),
                                x * settings.getSizeFactor() + settings.getOffset() + settings.getSizeFactor() / 2
                                        - settings.font.getSize() / 2 + settings.getOffset() / 5,
                                y * settings.getSizeFactor() + settings.getOffset() + settings.getSizeFactor() / 2
                                        + settings.font.getSize() * 2 / 5 + settings.getOffset() / 5);
                        break;
                    case "Dialog.plain": // "Times"
                    case "MS Gothic":
                        g2.drawString("" + Parser.parseSymbol(board[pos]),
                                x * settings.getSizeFactor() + settings.getOffset() + settings.getSizeFactor() / 2
                                        - settings.font.getSize() / 2,
                                y * settings.getSizeFactor() + settings.getOffset() + settings.getSizeFactor() / 2
                                        + settings.font.getSize() * 2 / 5);
                        break;
                    default:
                        g2.drawString("" + Parser.parseSymbolFromChessFont(board[pos]),
                                x * settings.getSizeFactor() + settings.getOffset() + settings.getSizeFactor() / 2
                                        - settings.font.getSize() / 2,
                                y * settings.getSizeFactor() + settings.getOffset() + settings.getSizeFactor() / 2
                                        + settings.font.getSize() * 2 / 5);
                        break;
                }
            }
        }
    }

    protected static void paintFilesAndRanks(Graphics g, AppearanceSettings settings, boolean boardOrientation) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Font rankFont = new Font("Times", Font.PLAIN, settings.getSizeFactor() * 15 / 100);
        g2.setFont(rankFont);
        for (int x = 0; x < 8; x++) {
            int file = boardOrientation ? x : 7 - x;
            int y = 7;
            g2.setColor(settings.getColorScheme().PIECE_COLOR);
            g2.drawString("" + Parser.getFileName(file),
                    x * settings.getSizeFactor() + settings.getOffset()
                            + settings.getSizeFactor() / 2 - settings.font.getSize() / 2,
                    y * settings.getSizeFactor() + settings.getOffset()
                            + settings.getSizeFactor() / 2 + settings.font.getSize() * 45 / 100);
        }
        for (int y = 1; y < 8; y++) {
            int rank = boardOrientation ? y : 7 - y;
            g2.setColor(settings.getColorScheme().PIECE_COLOR);
            g2.drawString("" + Parser.getRankName(rank),
                    settings.getOffset() + settings.getSizeFactor() / 2 - settings.font.getSize() / 2,
                    (7 - y) * settings.getSizeFactor() + settings.getOffset()
                            + settings.getSizeFactor() / 2 + settings.font.getSize() * 45 / 100);
        }
    }

    protected static void paintHighlights(Graphics g, AppearanceSettings settings, Moves squares, boolean Ok, boolean boardOrientation) {

        if (squares == null) return;
        Color highlight;
        if (Ok)
            highlight = settings.getColorScheme().HIGHLIGHT_1_COLOR;
        else
            highlight = settings.getColorScheme().HIGHLIGHT_2_COLOR;
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

    protected static void paintLastMove(Graphics g, AppearanceSettings settings, short moveInformation, boolean boardOrientation) {

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