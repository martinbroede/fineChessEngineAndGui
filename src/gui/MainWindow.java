package gui;

import gui.chessBoard.AppearanceSettings;
import gui.chessBoard.Board;
import sun.swing.text.html.FrameEditorPaneTag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow {

    final JFrame frame;
    final Board board;
    final JPanel content;
    final JMenuBar menuBar;
    final JLabel labelCapturedpieces;
    final AppearanceSettings appearanceSettings;

    final JMenuItem item_new;
    final JMenuItem item_store;
    final JMenuItem item_restore;
    final JMenuItem item_begin;

    final JMenuItem item_size_1;
    final JMenuItem item_size_2;
    final JMenuItem item_size_3;
    final JMenuItem item_change_piece_style;

    final JMenuItem item_castling_queenside;
    final JMenuItem item_castling_kingside;

    final JMenuItem item_promotion_queen;
    final JMenuItem item_promotion_knight;
    final JMenuItem item_promotion_bishop;
    final JMenuItem item_promotion_rook;
    final JMenuItem item_promotion;
    final JPopupMenu promotion_menu;

    final JMenuItem item_undo;
    final JMenuItem item_redo;

    final ColorScheme colorScheme;
    final int SIZE_L = 90;
    final int SIZE_M = 65;
    final int SIZE_S = 45;

    public MainWindow(char[] boardArray) {

        frame = new JFrame();
        frame.setResizable(false);
        labelCapturedpieces = new JLabel(" ",JLabel.CENTER);
        colorScheme = new ColorScheme();
        appearanceSettings = new AppearanceSettings(colorScheme);
        board = new Board(SIZE_S, boardArray, appearanceSettings);


        frame.setTitle("Schach");
        frame.addWindowListener(new WindowListener());

        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        content.add(board,BorderLayout.BEFORE_FIRST_LINE);
        labelCapturedpieces.setFont(appearanceSettings.getFont()); //todo update
        labelCapturedpieces.setOpaque(true);
        labelCapturedpieces.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        content.add(labelCapturedpieces,BorderLayout.AFTER_LAST_LINE);

        frame.setContentPane(content);

        item_new = new JMenuItem("Neu");
        item_store = new JMenuItem("Speichern");
        item_restore = new JMenuItem("Wiederherstellen");
        item_begin = new JMenuItem("Siel starten");

        item_size_1 = new JMenuItem("groß");
        item_size_2 = new JMenuItem("mittel");
        item_size_3 = new JMenuItem("klein");
        item_change_piece_style = new JMenuItem("Schachfiguren");

        item_castling_kingside = new JMenuItem("kurz");
        item_castling_queenside = new JMenuItem("lang");

        Font promotionItemFont = new Font("Times", Font.PLAIN, 100);
        item_promotion = new JMenuItem("Umwandlung");
        item_promotion_queen = new JMenuItem("\u2655 \u265B");
        item_promotion_knight = new JMenuItem("\u2658 \u265E");
        item_promotion_bishop = new JMenuItem("\u2657 \u265D");
        item_promotion_rook = new JMenuItem("\u2656 \u265C");


        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask());
        KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        item_undo = new JMenuItem("<<");
        item_redo = new JMenuItem(">>");
        item_undo.setAccelerator(ctrlZ);
        item_redo.setAccelerator(ctrlR);

        JMenuItem item_color_standard = new JMenuItem("standard");
        JMenuItem item_color_plain = new JMenuItem("schlicht");
        JMenuItem item_color_dark = new JMenuItem("dunkel");

        item_size_1.addActionListener(e -> {
            adjustBoardAndFrameSize(SIZE_L);
            board.repaint();
        });
        item_size_2.addActionListener(e -> {
            adjustBoardAndFrameSize(SIZE_M);
            board.repaint();
        });
        item_size_3.addActionListener(e -> {
            adjustBoardAndFrameSize(SIZE_S);
            board.repaint();
        });

        item_color_standard.addActionListener(e -> {
            colorScheme.setColors('s');
            setStyleSettings();
            board.repaint();
        });
        item_color_plain.addActionListener(e -> {
            colorScheme.setColors('p');
            setStyleSettings();
            board.repaint();
        });
        item_color_dark.addActionListener(e -> {
            colorScheme.setColors('d');
            setStyleSettings();
            board.repaint();
        });

        item_change_piece_style.addActionListener(e -> {
            board.fontRoulette();
            {
            }
        });

        JMenu main_menu = new JMenu("Spiel...");
        JMenu size_menu = new JMenu("Größe...");
        JMenu style_menu = new JMenu("Stil...");
        JMenu move_menu = new JMenu("Zug...");
        JMenu castling_menu = new JMenu("Rochade...");
        promotion_menu = new JPopupMenu();
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

        castling_menu.add(item_castling_kingside);
        castling_menu.add(item_castling_queenside);

        promotion_menu.add(item_promotion);
        promotion_menu.add(item_promotion_queen);
        promotion_menu.add(item_promotion_rook);
        promotion_menu.add(item_promotion_bishop);
        promotion_menu.add(item_promotion_knight);

        menuBar = new JMenuBar();
        menuBar.add(main_menu);
        menuBar.add(castling_menu);
        menuBar.add(move_menu);
        menuBar.add(size_menu);
        menuBar.add(style_menu);
        frame.add(promotion_menu);
        frame.add(menuBar);
        frame.setJMenuBar(menuBar);
        frame.setLocation(50, 50);
        frame.setVisible(true);
        setStyleSettings();
        adjustBoardAndFrameSize(SIZE_S);
    }

    private void adjustBoardAndFrameSize(int size_factor) {

        board.adjustSize(size_factor);

        content.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        frame.pack();
    }

    public void show_popup(String message) {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        menu.setPopupSize(appearanceSettings.getMargin(),appearanceSettings.getSizeFactor());

        JMenuItem item = new JMenuItem(message);
        item.setFont(new Font("Times", Font.PLAIN, 20));
        item.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        menu.add(item);

        item.addActionListener(e -> {
            board.setActive(true);
        });

        menu.show(board, 0,board.getHeight());

    }

    public void show_promotion_popup() {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = promotion_menu;
        menu.setPopupSize(appearanceSettings.getSizeFactor() * 4/2, appearanceSettings.getMargin());

        item_promotion.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        item_promotion_bishop.setBackground(appearanceSettings.getColorScheme().BLACK_SQUARES_COLOR);
        item_promotion_knight.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        item_promotion_queen.setBackground(appearanceSettings.getColorScheme().BLACK_SQUARES_COLOR);
        item_promotion_rook.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);

        if(appearanceSettings.getFontNumber()!=3) {
            item_promotion_bishop.setFont(appearanceSettings.getFont());
            item_promotion_knight.setFont(appearanceSettings.getFont());
            item_promotion_queen.setFont(appearanceSettings.getFont());
            item_promotion_rook.setFont(appearanceSettings.getFont());
        }

        menu.show(board, board.getWidth(), 0);
    }

    public void setStyleSettings() {
        menuBar.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelCapturedpieces.setFont(appearanceSettings.getFont());
        labelCapturedpieces.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
    }

    static class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose(); //close window
            System.exit(0); //close virtual machine.
        }
    }
}
