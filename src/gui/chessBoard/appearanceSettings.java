package gui.chessBoard;

import gui.ColorScheme;

import java.awt.*;

import static fileHandling.ReadWrite.createFontFromFile;

class appearanceSettings {
    final ColorScheme ColorScheme;
    final Font[] FONT_ROULETTE = {
            createFontFromFile("DejaVuSans", 1),
            new Font("Times", Font.PLAIN, 1),
            new Font("MS Gothic", Font.PLAIN, 1),
            createFontFromFile("MAYAFONT", 1)};

    int sizeFactor;
    int offset;
    int margin;
    int fontNumber;
    Font font;

    appearanceSettings(ColorScheme ColorScheme) {
        this.ColorScheme = ColorScheme;
        this.font = FONT_ROULETTE[0];
    }

    void adjustSize(int size_factor) {
        this.sizeFactor = size_factor;
        this.font = font.deriveFont((float) size_factor * 78 / 100);
        this.offset = size_factor / 6;
        this.margin = size_factor * 8 + 2 * this.offset;
    }

    void nextFont() {
        fontNumber++;
        font = FONT_ROULETTE[fontNumber % FONT_ROULETTE.length];
    }
}