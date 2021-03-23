package gui;

import java.awt.*;

public class ColorScheme {

    public final static int STANDARD = 1;
    public final static int DARK = 2;
    public final static int PLAIN = 3;
    public Color WHITE_SQUARES_COLOR;
    public Color BLACK_SQUARES_COLOR;
    public Color MARGIN_COLOR;
    public Color PIECE_COLOR;
    public Color FILL_COLOR;
    public Color HIGHLIGHT_1_COLOR;
    public Color HIGHLIGHT_2_COLOR;
    public Color LIGHT_COLOR;
    private int currentScheme;

    public ColorScheme() {
        setColors(STANDARD);
    }

    public int getCurrentScheme() {
        return currentScheme;
    }

    protected void setColors(int scheme) {

        currentScheme = scheme;
        switch (scheme) {

            case STANDARD:
                WHITE_SQUARES_COLOR = new Color(0xE2E3C8); /* WHITE SQUARES */
                BLACK_SQUARES_COLOR = new Color(0x709556);/* BLACK SQUARES */
                MARGIN_COLOR = new Color(0x4C4949); /* MARGIN */
                PIECE_COLOR = new Color(0x050505); /* PIECES */
                FILL_COLOR = new Color(0xF5F5F5); /* SPACE BETWEEN SQUARES */
                HIGHLIGHT_1_COLOR = new Color(0xB0DB7A); /*HIGHLIGHT "OK" */
                HIGHLIGHT_2_COLOR = new Color(0xdc9696); /*HIGHLIGHT "NOT OK" */
                LIGHT_COLOR = new Color(0x8B779556, true);
                break;

            case DARK:
                WHITE_SQUARES_COLOR = new Color(0xdfe3f3); /* WHITE SQUARES */
                BLACK_SQUARES_COLOR = new Color(0x88A0B4); /* BLACK SQUARES */
                MARGIN_COLOR = new Color(0x4394e); /* MARGIN */
                PIECE_COLOR = new Color(0x0); /* PIECES */
                FILL_COLOR = new Color(0xe9e9ff); /* SPACE BETWEEN SQUARES */
                HIGHLIGHT_1_COLOR = new Color(0xa7c2db); /*HIGHLIGHT "OK" */
                HIGHLIGHT_2_COLOR = new Color(0xdc9696); /*HIGHLIGHT "NOT OK" */
                LIGHT_COLOR = new Color(0x5C88A0B4, true);
                break;

            case PLAIN:
                WHITE_SQUARES_COLOR = new Color(0xF0F1F3); /* WHITE SQUARES */
                BLACK_SQUARES_COLOR = new Color(0xA9ADB3); /* BLACK SQUARES */
                MARGIN_COLOR = new Color(0x383849); /* MARGIN */
                PIECE_COLOR = new Color(0x020E0E); /* PIECES */
                FILL_COLOR = new Color(0xFFFFFF); /* SPACE BETWEEN SQUARES */
                HIGHLIGHT_1_COLOR = new Color(0xCBDAEC); /*HIGHLIGHT "OK" */
                HIGHLIGHT_2_COLOR = new Color(0xdbbcbc); /*HIGHLIGHT "NOT OK" */
                LIGHT_COLOR = new Color(0x193B3B3B, true);
                break;

        }
    }
}
