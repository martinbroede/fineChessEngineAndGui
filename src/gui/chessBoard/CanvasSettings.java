package gui.chessBoard;

import gui.Colors;

import java.awt.*;

import static fileHandling.ReadWrite.createFontFromFile;

public class CanvasSettings {
    public final Colors COLORS;
    public final Font[] FONT_ROULETTE = {
            createFontFromFile("DejaVuSans", 1),
            new Font("Times", Font.PLAIN, 1),
            new Font("MS Gothic", Font.PLAIN, 1),
            createFontFromFile("MAYAFONT", 1)};

    public int size_factor;
    public int offset;
    public int margin;
    public int fontNumber;
    public Font font;

    public CanvasSettings(Colors COLORS) {
        this.COLORS = COLORS;
        this.font = FONT_ROULETTE[0];
    }

    public void adjustSize(int size_factor) {
        this.size_factor = size_factor;
        this.font = font.deriveFont((float) size_factor * 78 / 100);
        this.offset = size_factor / 6;
        this.margin = size_factor * 8 + 2 * this.offset;
    }

    public void nextFont() {
        fontNumber++;
        font = FONT_ROULETTE[fontNumber % FONT_ROULETTE.length];
    }
}