package gui;

import gui.chessBoard.AppearanceSettings;
import gui.chessBoard.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow {

    final Board board;
    final AppearanceSettings appearanceSettings;
    final JFrame frame;
    final JPanel content;
    final JMenuBar menuBar;
    final JLabel labelCapturedWhitePieces;
    final JLabel labelCapturedBlackPieces;
    final JLabel labelPlaceHolderWest;
    final JLabel labelPlaceHolderEast;

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
    final JPopupMenu promotion_menu;

    final JMenuItem item_undo;
    final JMenuItem item_redo;

    final ColorScheme colorScheme;
    final int SIZE_L = 90;
    final int SIZE_M = 60;
    final int SIZE_S = 30;

    public MainWindow(char[] boardArray) {

        frame = new JFrame();
        frame.setResizable(false);
        labelCapturedWhitePieces = new JLabel(" ", JLabel.CENTER);
        labelCapturedBlackPieces = new JLabel(" ", JLabel.CENTER);
        labelPlaceHolderWest = new JLabel(" ", JLabel.CENTER);
        labelPlaceHolderEast = new JLabel(" ", JLabel.CENTER);
        colorScheme = new ColorScheme();
        appearanceSettings = new AppearanceSettings(colorScheme);
        board = new Board(SIZE_S, boardArray, appearanceSettings);

        frame.setTitle("Schach");
        frame.addWindowListener(new WindowListener());

        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        labelCapturedWhitePieces.setFont(appearanceSettings.getFont());
        labelCapturedBlackPieces.setFont(appearanceSettings.getFont());
        labelPlaceHolderWest.setFont(appearanceSettings.getFont());
        labelPlaceHolderEast.setFont(appearanceSettings.getFont());
        labelCapturedWhitePieces.setOpaque(true);
        labelCapturedBlackPieces.setOpaque(true);
        labelPlaceHolderWest.setOpaque(true);
        labelPlaceHolderEast.setOpaque(true);
        labelPlaceHolderWest.setVerticalAlignment(JLabel.TOP);
        labelPlaceHolderEast.setVerticalAlignment(JLabel.BOTTOM);
        labelCapturedWhitePieces.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelCapturedBlackPieces.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelPlaceHolderWest.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelPlaceHolderEast.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);

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
        item_promotion_queen = new JMenuItem("\u2655 \u265B");
        item_promotion_knight = new JMenuItem("\u2658 \u265E");
        item_promotion_bishop = new JMenuItem("\u2657 \u265D");
        item_promotion_rook = new JMenuItem("\u2656 \u265C");


        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
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
            colorScheme.setColors(ColorScheme.STANDARD);
            setStyleSettings();
            board.repaint();
        });
        item_color_plain.addActionListener(e -> {
            colorScheme.setColors(ColorScheme.PLAIN);
            setStyleSettings();
            board.repaint();
        });
        item_color_dark.addActionListener(e -> {
            colorScheme.setColors(ColorScheme.DARK);
            setStyleSettings();
            board.repaint();
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
        move_menu.addSeparator();
        move_menu.add(castling_menu);

        castling_menu.add(item_castling_kingside);
        castling_menu.add(item_castling_queenside);

        promotion_menu.add(item_promotion_queen);
        promotion_menu.add(item_promotion_rook);
        promotion_menu.add(item_promotion_bishop);
        promotion_menu.add(item_promotion_knight);

        menuBar = new JMenuBar();
        menuBar.add(main_menu);
        menuBar.add(move_menu);
        menuBar.add(size_menu);
        menuBar.add(style_menu);
        frame.add(promotion_menu);
        frame.add(menuBar);
        frame.setJMenuBar(menuBar);
        frame.setLocation(0, 0);
        frame.setVisible(true);
        setStyleSettings();


        content.add(labelCapturedWhitePieces, BorderLayout.PAGE_START);
        content.add(labelPlaceHolderWest, BorderLayout.WEST);
        content.add(board, BorderLayout.CENTER);
        content.add(labelPlaceHolderEast, BorderLayout.EAST);
        content.add(labelCapturedBlackPieces, BorderLayout.PAGE_END);
        adjustBoardAndFrameSize(SIZE_M);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'P') {
                    System.out.println("HELLO USER! :)");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void adjustBoardAndFrameSize(int size_factor) {

        appearanceSettings.adjustSize(size_factor);

        Dimension newDim = new Dimension(appearanceSettings.getSizeFactor() * 2, appearanceSettings.getMargin());
        labelPlaceHolderWest.setPreferredSize(newDim);
        labelPlaceHolderEast.setPreferredSize(newDim);

        newDim = new Dimension(appearanceSettings.getSizeFactor() * 2 + appearanceSettings.getMargin(),
                appearanceSettings.getSizeFactor() * 2);
        labelCapturedWhitePieces.setPreferredSize(newDim);
        labelCapturedBlackPieces.setPreferredSize(newDim);

        board.adjustSize();
        setStyleSettings();

        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        frame.pack();
    }

    public void show_popup(String message) {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        menu.setPopupSize(appearanceSettings.getMargin(), appearanceSettings.getSizeFactor());

        JMenuItem item = new JMenuItem(message);
        item.setFont(new Font("Times", Font.PLAIN, appearanceSettings.getSizeFactor() / 3));
        item.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        menu.add(item);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.setActive((true));
            }
        };

        item.addActionListener(actionListener);

        menu.show(board, 0, appearanceSettings.getMargin());
    }

    public void show_promotion_popup() {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = promotion_menu;
        menu.setPopupSize(appearanceSettings.getSizeFactor() * 4 / 2, appearanceSettings.getMargin());

        item_promotion_bishop.setBackground(appearanceSettings.getColorScheme().BLACK_SQUARES_COLOR);
        item_promotion_knight.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        item_promotion_queen.setBackground(appearanceSettings.getColorScheme().BLACK_SQUARES_COLOR);
        item_promotion_rook.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);

        item_promotion_bishop.setFont(appearanceSettings.getFont());
        item_promotion_knight.setFont(appearanceSettings.getFont());
        item_promotion_queen.setFont(appearanceSettings.getFont());
        item_promotion_rook.setFont(appearanceSettings.getFont());

        menu.show(board, board.getWidth(), 0);
    }

    public void setStyleSettings() {

        menuBar.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelCapturedWhitePieces.setFont(appearanceSettings.getFont());
        labelCapturedBlackPieces.setFont(appearanceSettings.getFont());
        labelPlaceHolderWest.setFont(appearanceSettings.getFont());
        labelPlaceHolderEast.setFont(appearanceSettings.getFont());
        labelCapturedWhitePieces.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelCapturedBlackPieces.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelPlaceHolderWest.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        labelPlaceHolderEast.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
    }

    static class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            System.out.println("GOODBYE AND HAVE A NICE DAY.");
            e.getWindow().dispose(); //close window
            System.exit(0); //close virtual machine.
        }
    }
}
