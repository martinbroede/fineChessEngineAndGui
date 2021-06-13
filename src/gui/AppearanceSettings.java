package gui;

import java.awt.*;
import java.util.LinkedList;

import static misc.FileHandling.createFontFromFile;

public class AppearanceSettings {

    public final ColorScheme colorScheme;
    public final LinkedList<Font> FONTS;
    public Font font;
    public int fontNumber;
    public int sizeFactor;
    public int offset;
    public int margin;

    public AppearanceSettings() {

        colorScheme = new ColorScheme();
        FONTS = new LinkedList<>();

        try {
            Font newFont;
            newFont = new Font(MainWindow.TIMES, Font.PLAIN, 1);
            FONTS.add(newFont);
            newFont = new Font(MainWindow.MS_GOTHIC, Font.PLAIN, 1);
            FONTS.add(newFont);
            newFont = createFontFromFile("DejaVuSans", 1);
            FONTS.add(newFont);
            newFont = createFontFromFile("Chess-Regular", 1);
            FONTS.add(newFont);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.font = FONTS.get(0);
    }

    public void setFont(int fontNumber) {
        this.fontNumber = fontNumber % FONTS.size();
        font = FONTS.get(this.fontNumber);
    }

    public int getFontNumer() {
        return fontNumber % FONTS.size();
    }

    public void adjustSize(int size_factor) {

        this.sizeFactor = size_factor;
        if (font.getFontName().equals(MainWindow.CHESS_REGULAR)) {
            this.font = font.deriveFont((float) size_factor * 128 / 100);
        } else
            this.font = font.deriveFont((float) size_factor * 78 / 100);
        this.offset = size_factor / 4;
        this.margin = size_factor * 8 + 2 * this.offset;
    }

    public void nextFont() {

        fontNumber++;
        font = FONTS.get(fontNumber % FONTS.size());
        adjustSize(this.sizeFactor);
    }
}