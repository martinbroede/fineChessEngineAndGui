package gui;

import java.awt.*;

public class Colors {
    protected Color WHITE_SQUARES_COLOR;
    protected Color BLACK_SQUARES_COLOR;
    protected Color MARGIN_COLOR;
    protected Color PIECE_COLOR;
    protected Color FILL_COLOR;
    protected Color HIGHLIGHT_1_COLOR;
    protected Color HIGHLIGHT_2_COLOR;
    protected Color LIGHT_COLOR;

    public Colors() {
        setColors('p');
    }

    protected void setColors(char col) {
        switch (col) {
            case 's': {
                WHITE_SQUARES_COLOR = new Color(0xebecd0); /* WHITE SQUARES */
                BLACK_SQUARES_COLOR = new Color(0x779556);/* BLACK SQUARES */
                MARGIN_COLOR = new Color(0x464342); /* MARGIN */
                PIECE_COLOR = new Color(0x000000); /* PIECES */
                FILL_COLOR = new Color(0xFFFFFF); /* SPACE BETWEEN SQUARES */
                HIGHLIGHT_1_COLOR = new Color(0xB0DB7A); /*HIGHLIGHT "OK" */
                HIGHLIGHT_2_COLOR = new Color(0xdc9696); /*HIGHLIGHT "NOT OK" */
                LIGHT_COLOR = new Color(0x8B779556, true);
                break;
            }
            case 'd': {
                WHITE_SQUARES_COLOR = new Color(0xdfe3f3); /* WHITE SQUARES */
                BLACK_SQUARES_COLOR = new Color(0x88A0B4); /* BLACK SQUARES */
                MARGIN_COLOR = new Color(0x4394e); /* MARGIN */
                PIECE_COLOR = new Color(0x0); /* PIECES */
                FILL_COLOR = new Color(0xe9e9ff); /* SPACE BETWEEN SQUARES */
                HIGHLIGHT_1_COLOR = new Color(0xa7c2db); /*HIGHLIGHT "OK" */
                HIGHLIGHT_2_COLOR = new Color(0xdc9696); /*HIGHLIGHT "NOT OK" */
                LIGHT_COLOR = new Color(0x5C88A0B4, true);
                break;
            }
            case 'p': {
                WHITE_SQUARES_COLOR = new Color(0xffffff); /* WHITE SQUARES */
                BLACK_SQUARES_COLOR = new Color(0xD4D3D3); /* BLACK SQUARES */
                MARGIN_COLOR = new Color(0x010F2F); /* MARGIN */
                PIECE_COLOR = new Color(0x010918); /* PIECES */
                FILL_COLOR = new Color(0xFFFFFF); /* SPACE BETWEEN SQUARES */
                HIGHLIGHT_1_COLOR = new Color(0xCBDAEC); /*HIGHLIGHT "OK" */
                HIGHLIGHT_2_COLOR = new Color(0xdbbcbc); /*HIGHLIGHT "NOT OK" */
                LIGHT_COLOR = new Color(0x8A3B3B3B, true);
                break;
            }
        }
    }
}
