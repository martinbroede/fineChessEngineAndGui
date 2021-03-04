package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Gui {
    protected final Panel panel;
    protected final BoardCanvas boardCanvas;
    protected final MenuItem item_new;
    protected final MenuItem item_store;
    protected final MenuItem item_restore;
    protected final MenuItem item_begin;
    protected final MenuItem item_size_1;
    protected final MenuItem item_size_2;
    protected final MenuItem item_size_3;
    protected final MenuItem item_change_piece_style;
    protected final Colors colors;
    private final int SIZE_L = 90;
    private final int SIZE_M = 65;
    private final int SIZE_S = 45;
    private final Frame frame;
    /** pixel per square */
    public int size_factor;

    public Gui(char[] board) {
        frame = new Frame();
        colors = new Colors();
        panel = new Panel();
        boardCanvas = new BoardCanvas(SIZE_S, board, colors);
        panel.add(boardCanvas);
        frame.setTitle("Schach");
        frame.addWindowListener(new WindowListener());
        frame.add(panel, BorderLayout.CENTER);
        item_new = new MenuItem("Neu");
        item_store = new MenuItem("Speichern");
        item_restore = new MenuItem("Wiederherstellen");
        item_begin = new MenuItem("Siel starten");
        item_size_1 = new MenuItem("groß");
        item_size_2 = new MenuItem("mittel");
        item_size_3 = new MenuItem("klein");
        item_change_piece_style = new MenuItem("Schachfiguren");
        MenuItem item_color_standard = new MenuItem("standard");
        MenuItem item_color_plain = new MenuItem("schlicht");
        MenuItem item_color_dark = new MenuItem("dunkel");
        item_size_1.addActionListener(e -> {
            adjustSize(SIZE_L);
            this.boardCanvas.repaint();
        });
        item_size_2.addActionListener(e -> {
            adjustSize(SIZE_M);
            this.boardCanvas.repaint();
        });
        item_size_3.addActionListener(e -> {
            adjustSize(SIZE_S);
            this.boardCanvas.repaint();
        });
        item_color_standard.addActionListener(e -> {
            this.colors.setColors('s');
            this.boardCanvas.repaint();
        });
        item_color_plain.addActionListener(e -> {
            this.colors.setColors('p');
            this.boardCanvas.repaint();
        });
        item_color_dark.addActionListener(e -> {
            this.colors.setColors('d');
            this.boardCanvas.repaint();
        });
        item_change_piece_style.addActionListener(e -> {
            this.boardCanvas.fontRoulette();
            {
            }
        });
        Menu main_menu = new Menu("Spiel...");
        Menu size_menu = new Menu("Größe...");
        Menu style_menu = new Menu("Stil...");
        main_menu.add(item_new);
        main_menu.add(item_store);
        main_menu.add(item_restore);
        main_menu.add(item_begin);
        size_menu.add(item_size_1);
        size_menu.add(item_size_2);
        size_menu.add(item_size_3);
        style_menu.add(item_color_standard);
        style_menu.add(item_color_plain);
        style_menu.add(item_color_dark);
        style_menu.addSeparator();
        style_menu.add(item_change_piece_style);
        MenuBar main_bar = new MenuBar();
        main_bar.add(main_menu);
        main_bar.add(size_menu);
        main_bar.add(style_menu);
        frame.setMenuBar(main_bar);
        frame.setLocation(100, 100);
        frame.setVisible(true);


        adjustSize(SIZE_S); // 2X !!
        try {
            Thread.sleep(50);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        adjustSize(SIZE_S); // 2X !!
    }

    private void adjustSize(int size_factor) {
        this.size_factor = size_factor;
        boardCanvas.adjustSize(size_factor);
        frame.pack();
    }

    public void show_dialog(String message) {
        Dialog dialog_game_over = new Dialog(frame, "", true);
        dialog_game_over.setSize(size_factor * 4, size_factor * 2);
        dialog_game_over.setUndecorated(true);
        Button b = new Button(message);
        b.addActionListener(e -> {
            dialog_game_over.setVisible(false);
            boardCanvas.repaint();
        });
        b.setBackground(colors.WHITE_SQUARES_COLOR);
        b.setForeground(colors.PIECE_COLOR);
        dialog_game_over.add(b);

        Point location = frame.getLocation();
        location.translate(panel.getLocation().x, panel.getLocation().y);
        location.translate(
                boardCanvas.getLocation().x + boardCanvas.offset + 2 * size_factor,
                boardCanvas.getLocation().y + boardCanvas.offset + 3 * size_factor);
        dialog_game_over.setLocation(location);
        dialog_game_over.setVisible(true);
    }

    static class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose(); // Fenster schließen
            System.exit(0); // VM schließen
        }
    }
}
