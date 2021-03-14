package gui;

import gui.chessBoard.AppearanceSettings;
import gui.chessBoard.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

    final JTextField chatInput;
    final JTextArea chatOutput;

    final JMenuItem itemStartServer;
    final JMenuItem itemStartClient;
    final JMenuItem itemNetworkDestroy;
    final JMenuItem itemSynchronize;
    final JMenuItem itemShowChat;

    final JMenuItem itemNew;
    final JMenuItem itemStore;
    final JMenuItem itemRestore;
    final JMenuItem itemBegin;

    final JMenuItem itemSize1;
    final JMenuItem itemSize2;
    final JMenuItem itemSize3;
    final JMenuItem itemChangePieceStyle;

    final JMenuItem itemCastlingQueenside;
    final JMenuItem itemCastlingKingside;

    final JMenuItem itemPromotionQueen;
    final JMenuItem itemPromotionKnight;
    final JMenuItem itemPromotionBishop;
    final JMenuItem itemPromotionRook;
    final JPopupMenu menuPromotion;

    final JMenuItem itemUndo;
    final JMenuItem itemRedo;

    final ColorScheme colorScheme;
    final int SIZE_L = 70;
    final int SIZE_M = 45;
    final int SIZE_S = 30;
    String VERSION = "VERSION UNKNOWN";

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
        try {
            String title = "Schach ";
            FileInputStream stream = new FileInputStream("version.txt");
            Scanner scanner = new Scanner(stream);
            VERSION = scanner.nextLine();
            title += VERSION;
            frame.setTitle(title);
        }catch(FileNotFoundException ex){
            System.err.println("VERSION FILE NOT FOUND");
        }
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

        itemStartServer = new JMenuItem("Spiel erstellen");
        itemStartClient = new JMenuItem("Spiel beitreten");
        itemSynchronize = new JMenuItem("Spiel beginnen");
        itemNetworkDestroy = new JMenuItem("Verbindung trennen");
        itemShowChat = new JMenuItem("Chat anzeigen");

        itemNew = new JMenuItem("Neu");
        itemStore = new JMenuItem("Speichern");
        itemRestore = new JMenuItem("Wiederherstellen");
        itemBegin = new JMenuItem("Siel starten");

        itemSize1 = new JMenuItem("groß");
        itemSize2 = new JMenuItem("mittel");
        itemSize3 = new JMenuItem("klein");
        itemChangePieceStyle = new JMenuItem("Schachfiguren");

        itemCastlingKingside = new JMenuItem("kurz");
        itemCastlingQueenside = new JMenuItem("lang");

        Font promotionItemFont = new Font("Times", Font.PLAIN, 100);
        itemPromotionQueen = new JMenuItem("\u2655 \u265B");
        itemPromotionKnight = new JMenuItem("\u2658 \u265E");
        itemPromotionBishop = new JMenuItem("\u2657 \u265D");
        itemPromotionRook = new JMenuItem("\u2656 \u265C");


        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        itemUndo = new JMenuItem("<<");
        itemRedo = new JMenuItem(">>");
        itemUndo.setAccelerator(ctrlZ);
        itemRedo.setAccelerator(ctrlR);

        JMenuItem itemColorStandard = new JMenuItem("standard");
        JMenuItem itemColorPlain = new JMenuItem("schlicht");
        JMenuItem itemColorDark = new JMenuItem("dunkel");

        JMenu mainMenu = new JMenu("Spiel...");
        JMenu sizeMenu = new JMenu("Größe...");
        JMenu styleMenu = new JMenu("Stil...");
        JMenu moveMenu = new JMenu("Zug...");
        JMenu castlingMenu = new JMenu("Rochade...");
        JMenu networkMenu =new JMenu("Netzwerk...");
        menuPromotion = new JPopupMenu();
        menuPromotion.setFont(promotionItemFont);

        mainMenu.add(itemNew);
        mainMenu.add(itemStore);
        mainMenu.add(itemRestore);
        mainMenu.add(itemBegin);

        sizeMenu.add(itemSize1);
        sizeMenu.add(itemSize2);
        sizeMenu.add(itemSize3);

        styleMenu.add(itemColorStandard);
        styleMenu.add(itemColorPlain);
        styleMenu.add(itemColorDark);
        styleMenu.addSeparator();
        styleMenu.add(itemChangePieceStyle);

        moveMenu.add(itemUndo);
        moveMenu.add(itemRedo);
        moveMenu.addSeparator();
        moveMenu.add(castlingMenu);

        networkMenu.add(itemStartServer);
        networkMenu.add(itemStartClient);
        networkMenu.add(itemSynchronize);
        networkMenu.addSeparator();
        networkMenu.add(itemNetworkDestroy);
        networkMenu.addSeparator();
        networkMenu.addSeparator();
        networkMenu.add(itemShowChat);

        castlingMenu.add(itemCastlingKingside);
        castlingMenu.add(itemCastlingQueenside);

        chatInput = new JTextField();
        chatOutput = new JTextArea();

        menuPromotion.add(itemPromotionQueen);
        menuPromotion.add(itemPromotionRook);
        menuPromotion.add(itemPromotionBishop);
        menuPromotion.add(itemPromotionKnight);

        menuBar = new JMenuBar();
        menuBar.add(mainMenu);
        menuBar.add(moveMenu);
        menuBar.add(sizeMenu);
        menuBar.add(styleMenu);
        menuBar.add(networkMenu);
        frame.add(menuPromotion);
        frame.setJMenuBar(menuBar);
        frame.setLocation(0, 0);
        frame.setVisible(true);
        setStyleSettings();

        content.add(labelCapturedWhitePieces, BorderLayout.NORTH);
        content.add(labelPlaceHolderWest, BorderLayout.WEST);
        content.add(board, BorderLayout.CENTER);
        content.add(labelPlaceHolderEast, BorderLayout.EAST);
        content.add(labelCapturedBlackPieces, BorderLayout.SOUTH);
        adjustBoardAndFrameSize(SIZE_S);

        /*  ###################################### add action listeners ############################################# */

        itemSize1.addActionListener(e -> {
            adjustBoardAndFrameSize(SIZE_L);
            board.repaint();
        });
        itemSize2.addActionListener(e -> {
            adjustBoardAndFrameSize(SIZE_M);
            board.repaint();
        });
        itemSize3.addActionListener(e -> {
            adjustBoardAndFrameSize(SIZE_S);
            board.repaint();
        });

        itemColorStandard.addActionListener(e -> {
            colorScheme.setColors(ColorScheme.STANDARD);
            setStyleSettings();
            board.repaint();
        });
        itemColorPlain.addActionListener(e -> {
            colorScheme.setColors(ColorScheme.PLAIN);
            setStyleSettings();
            board.repaint();
        });
        itemColorDark.addActionListener(e -> {
            colorScheme.setColors(ColorScheme.DARK);
            setStyleSettings();
            board.repaint();
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

        newDim = new Dimension(appearanceSettings.getSizeFactor()*4, appearanceSettings.getMargin()*2);
        chatOutput.setPreferredSize(newDim);

        board.adjustSize();
        setStyleSettings();

        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        frame.pack();
    }

    public void showPopup(String message) {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        menu.setPopupSize(appearanceSettings.getMargin(), appearanceSettings.getSizeFactor());

        JMenuItem item = new JMenuItem(message);
        item.setFont(new Font("Times", Font.PLAIN, appearanceSettings.getSizeFactor() / 3));
        item.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        menu.add(item);

        ActionListener actionListener = e -> board.setActive((true));

        item.addActionListener(actionListener);

        menu.show(board, 0, appearanceSettings.getMargin());
    }

    public void showPromotionPopup() {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = menuPromotion;
        menu.setPopupSize(appearanceSettings.getSizeFactor() * 4 / 2, appearanceSettings.getMargin());

        itemPromotionBishop.setBackground(appearanceSettings.getColorScheme().BLACK_SQUARES_COLOR);
        itemPromotionKnight.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);
        itemPromotionQueen.setBackground(appearanceSettings.getColorScheme().BLACK_SQUARES_COLOR);
        itemPromotionRook.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);

        itemPromotionBishop.setFont(appearanceSettings.getFont());
        itemPromotionKnight.setFont(appearanceSettings.getFont());
        itemPromotionQueen.setFont(appearanceSettings.getFont());
        itemPromotionRook.setFont(appearanceSettings.getFont());

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
            System.out.println("FINECHESS SAYS GOODBYE AND HAVE A NICE DAY.");
            e.getWindow().dispose(); //close window
            System.exit(0); //close virtual machine.
        }
    }
}
