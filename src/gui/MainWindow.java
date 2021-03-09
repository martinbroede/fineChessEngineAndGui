package gui;

import gui.chessBoard.Board;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow {

    final Frame frame;
    final Panel panel;
    final Board board;
    final MenuBar menuBar;

    final MenuItem item_new;
    final MenuItem item_store;
    final MenuItem item_restore;
    final MenuItem item_begin;

    final MenuItem item_size_1;
    final MenuItem item_size_2;
    final MenuItem item_size_3;
    final MenuItem item_change_piece_style;

    final MenuItem item_castling_queenside;
    final MenuItem item_castling_kingside;

    final MenuItem item_promotion_queen;
    final MenuItem item_promotion_knight;
    final MenuItem item_promotion_bishop;
    final MenuItem item_promotion_rook;
    final PopupMenu promotion_menu;

    final MenuItem item_undo;
    final MenuItem item_redo;

    final ColorScheme colorScheme;
    final int SIZE_L = 90;
    final int SIZE_M = 65;
    final int SIZE_S = 45;

    /** pixel per square */
    int size_factor;

    public MainWindow(char[] board) {

        frame = new Frame();
        colorScheme = new ColorScheme();
        panel = new Panel();
        this.board = new Board(SIZE_S, board, colorScheme);

        panel.add(this.board);

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

        item_castling_kingside = new MenuItem("kurz");
        item_castling_queenside = new MenuItem("lang");

        Font promotionItemFont = new Font("Times", Font.PLAIN, 35);
        MenuItem item_promotion = new MenuItem("Umwandlung?");
        item_promotion.setEnabled(false);
        item_promotion_queen = new MenuItem("Dame");
        item_promotion_knight = new MenuItem("Springer");
        item_promotion_bishop = new MenuItem("Läufer");
        item_promotion_rook = new MenuItem("Turm");

        item_undo = new MenuItem("<<");
        item_redo = new MenuItem(">>");

        MenuItem item_color_standard = new MenuItem("standard");
        MenuItem item_color_plain = new MenuItem("schlicht");
        MenuItem item_color_dark = new MenuItem("dunkel");

        item_size_1.addActionListener(e -> {
            adjustSize(SIZE_L);
            this.board.repaint();
        });
        item_size_2.addActionListener(e -> {
            adjustSize(SIZE_M);
            this.board.repaint();
        });
        item_size_3.addActionListener(e -> {
            adjustSize(SIZE_S);
            this.board.repaint();
        });

        item_color_standard.addActionListener(e -> {
            this.colorScheme.setColors('s');
            this.board.repaint();
        });
        item_color_plain.addActionListener(e -> {
            this.colorScheme.setColors('p');
            this.board.repaint();
        });
        item_color_dark.addActionListener(e -> {
            this.colorScheme.setColors('d');
            this.board.repaint();
        });

        item_change_piece_style.addActionListener(e -> {
            this.board.fontRoulette();
            {
            }
        });

        Menu main_menu = new Menu("Spiel...");
        Menu size_menu = new Menu("Größe...");
        Menu style_menu = new Menu("Stil...");
        Menu move_menu = new Menu("Zug...");
        Menu castling_menu = new Menu("Rochade...");
        promotion_menu = new PopupMenu();
        promotion_menu.setFont(promotionItemFont);

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

        move_menu.add(item_undo);
        move_menu.add(item_redo);
        item_undo.setShortcut(new MenuShortcut('Z'));
        item_redo.setShortcut(new MenuShortcut('R'));

        castling_menu.add(item_castling_kingside);
        castling_menu.add(item_castling_queenside);

        promotion_menu.add(item_promotion);
        promotion_menu.addSeparator();
        promotion_menu.add(item_promotion_queen);
        promotion_menu.addSeparator();
        promotion_menu.addSeparator();
        promotion_menu.add(item_promotion_rook);
        promotion_menu.addSeparator();
        promotion_menu.addSeparator();
        promotion_menu.add(item_promotion_bishop);
        promotion_menu.addSeparator();
        promotion_menu.addSeparator();
        promotion_menu.add(item_promotion_knight);
        promotion_menu.addSeparator();

        menuBar = new MenuBar();
        menuBar.add(main_menu);
        menuBar.add(castling_menu);
        menuBar.add(move_menu);
        menuBar.add(size_menu);
        menuBar.add(style_menu);
        frame.add(promotion_menu);
        frame.setMenuBar(menuBar);
        frame.setLocation(100, 100);
        frame.setVisible(true);

        adjustSize(SIZE_S); // 2X !
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        adjustSize(SIZE_S); // 2X !

        show_dialog("DAS IST DIE ALTE AWT - VERSION!");
    }

    private void adjustSize(int size_factor) {
        this.size_factor = size_factor;
        board.adjustSize(size_factor);
        frame.pack();
    }

    public void show_dialog(String message) {
        Dialog dialog_game_over = new Dialog(frame, "", true);
        dialog_game_over.setSize(size_factor * 4, size_factor * 2);
        dialog_game_over.setUndecorated(true);
        Button b = new Button(message);
        b.addActionListener(e -> {
            dialog_game_over.setVisible(false);
            board.repaint();
        });
        b.setBackground(colorScheme.WHITE_SQUARES_COLOR);
        b.setForeground(colorScheme.PIECE_COLOR);
        dialog_game_over.add(b);

        Point location = frame.getLocation();
        location.translate(panel.getLocation().x, panel.getLocation().y);
        location.translate(
                board.getLocation().x + board.getOffset() + 2 * size_factor,
                board.getLocation().y + board.getOffset() + 3 * size_factor);
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
