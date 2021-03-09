package gui.chessBoard;

import gui.ColorScheme;

import java.awt.*;

import static fileHandling.ReadWrite.createFontFromFile;

public class AppearanceSettings {

    private final ColorScheme ColorScheme;
    private final Font[] FONT_ROULETTE = {
            createFontFromFile("DejaVuSans", 1),
            new Font("Times", Font.PLAIN, 1),
            new Font("MS Gothic", Font.PLAIN, 1),
            createFontFromFile("MAYAFONT", 1)};

    private int sizeFactor;
    private int offset;
    private int margin;

    public gui.ColorScheme getColorScheme() {
        return ColorScheme;
    }

    public Font[] getFONT_ROULETTE() {
        return FONT_ROULETTE;
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

    public int getFontNumber() {
        return fontNumber;
    }

    public Font getFont() {
        return font;
    }

    int fontNumber;
    Font font;

    public AppearanceSettings(ColorScheme ColorScheme) {

        this.ColorScheme = ColorScheme;
        this.font = FONT_ROULETTE[0];
    }

    public void adjustSize(int size_factor) {

        this.sizeFactor = size_factor;
        this.font = font.deriveFont((float) size_factor * 78 / 100);
        this.offset = size_factor / 6;
        this.margin = size_factor * 8 + 2 * this.offset;
    }

    public void nextFont() {

        fontNumber++;
        font = FONT_ROULETTE[fontNumber % FONT_ROULETTE.length];
    }
}