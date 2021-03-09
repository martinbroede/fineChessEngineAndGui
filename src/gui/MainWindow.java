package gui;

import gui.chessBoard.AppearanceSettings;
import gui.chessBoard.Board;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow {

    final Frame frame;
    final Board board;
    final MenuBar menuBar;
    final AppearanceSettings appearanceSettings;

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

    public MainWindow(char[] boardArray) {

        frame = new Frame();
        colorScheme = new ColorScheme();
        appearanceSettings = new AppearanceSettings(colorScheme);
        board = new Board(SIZE_S, boardArray, appearanceSettings);

        frame.setTitle("Schach");
        frame.addWindowListener(new WindowListener());
        frame.add(board, BorderLayout.CENTER);

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

        Font promotionItemFont = new Font("Times", Font.PLAIN, 25);
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
            board.repaint();
        });
        item_size_2.addActionListener(e -> {
            adjustSize(SIZE_M);
            board.repaint();
        });
        item_size_3.addActionListener(e -> {
            adjustSize(SIZE_S);
            board.repaint();
        });

        item_color_standard.addActionListener(e -> {
            colorScheme.setColors('s');
            board.repaint();
        });
        item_color_plain.addActionListener(e -> {
            colorScheme.setColors('p');
            board.repaint();
        });
        item_color_dark.addActionListener(e -> {
            colorScheme.setColors('d');
            board.repaint();
        });

        item_change_piece_style.addActionListener(e -> {
            board.fontRoulette();
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

        show_dialog("ALTE AWT - VERSION!");
    }

    private void adjustSize(int size_factor) {
        board.adjustSize(size_factor);
        frame.pack();
    }

    public void show_dialog(String message) {

        Dialog dialog = new Dialog(frame, "", true);

        dialog.setSize(appearanceSettings.getSizeFactor() * 4,
                appearanceSettings.getSizeFactor() * 2);

        dialog.setUndecorated(true);
        Button b = new Button(message);
        b.addActionListener(e -> {
            dialog.setVisible(false);
            board.repaint();
        });
        b.setBackground(colorScheme.WHITE_SQUARES_COLOR);
        b.setForeground(colorScheme.PIECE_COLOR);
        dialog.add(b);

        Point location = frame.getLocation();
        //location.translate(panel.getLocation().x, panel.getLocation().y);
        location.translate(
                board.getLocation().x + board.getOffset() + 2 * appearanceSettings.getSizeFactor(),
                board.getLocation().y + board.getOffset() + 3 * appearanceSettings.getSizeFactor());
        dialog.setLocation(location);
        dialog.setVisible(true);
    }

    static class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose(); // Fenster schließen
            System.exit(0); // VM schließen
        }
    }
}
