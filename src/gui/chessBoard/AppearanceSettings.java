package gui.chessBoard;

import gui.ColorScheme;

import java.awt.*;

import static fileHandling.ReadWrite.createFontFromFile;

public class AppearanceSettings {

    private final ColorScheme ColorScheme;
    private final Font[] FONT_ROULETTE = {
            createFontFromFile("DejaVuSans", 1),
            createFontFromFile("Chess-Regular", 1),
            new Font("Times", Font.PLAIN, 1),
            new Font("MS Gothic", Font.PLAIN, 1)};
    int fontNumber;
    Font font;
    private int sizeFactor;
    private int offset;
    private int margin;

    public AppearanceSettings(ColorScheme ColorScheme) {

        this.ColorScheme = ColorScheme;
        this.font = FONT_ROULETTE[0];
    }

    public gui.ColorScheme getColorScheme() {
        return ColorScheme;
    }

    public int getSizeFactor() {
        return sizeFactor;
    }

    public int getOffset() {
        return offset;
    }

    public int getMargin() {
        return margin;
    }

    public Font getFont() {
        return font;
    }

    public void adjustSize(int size_factor) {

        this.sizeFactor = size_factor;
        if (font.getFontName().equals("Chess Regular")) {
            this.font = font.deriveFont((float) size_factor * 128 / 100);
        } else
            this.font = font.deriveFont((float) size_factor * 78 / 100);
        this.offset = size_factor / 4;
        this.margin = size_factor * 8 + 2 * this.offset;
    }

    public void nextFont() {

        fontNumber++;
        font = FONT_ROULETTE[fontNumber % FONT_ROULETTE.length];
        adjustSize(this.sizeFactor);
    }
}