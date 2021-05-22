package gui;

import chessNetwork.Password;
import core.Chess;
import core.Util;
import fineChessUpdater.Downloader;
import gui.chessBoard.AppearanceSettings;
import gui.chessBoard.Board;
import gui.dialogs.DialogMessage;
import gui.dialogs.DialogNameAndPassword;
import gui.dialogs.DialogText;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import static fileHandling.ReadWrite.getStringFromFile;
import static fileHandling.StaticSetting.getSetting;
import static fileHandling.StaticSetting.getStoredSettings;

public class Window {

    final static JFrame frame = new JFrame();
    final Board board;
    final AppearanceSettings appearanceSettings;
    final JPanel content;
    final JMenuBar menuBar;
    final JLabel labelCapturedWhitePieces;
    final JLabel labelCapturedBlackPieces;
    final JLabel labelPlaceHolderWest;
    final JLabel labelPlaceHolderEast;

    final JMenuItem itemStartServer;
    final JMenuItem itemStartClient;
    final JMenuItem itemConnectToServer;
    final JMenuItem itemConnectWithPlayer;
    final JMenuItem itemNetworkDisconnect;
    final JMenuItem itemNewNetworkGame;
    final JMenuItem itemRatingQuery;
    final JMenuItem itemSendFeedback;
    final JMenuItem itemShowChat;

    final JMenuItem itemNewGame;
    final JMenuItem itemStore;
    final JMenuItem itemRestore;
    final JMenuItem itemBegin;
    final JMenuItem itemLicense;
    final JMenuItem itemCheckVersion;
    final JMenuItem itemShowVersionLog;

    final JMenuItem itemSize1;
    final JMenuItem itemSize2;
    final JMenuItem itemSize3;
    final JMenuItem itemEnlarge;
    final JMenuItem itemDiminish;

    final JPopupMenu messageMenu;
    final JMenuItem messageItem;

    final JMenuItem itemChangePieceStyle;

    final JMenuItem itemCastlingQueenside;
    final JMenuItem itemCastlingKingside;
    final JMenuItem itemPromotionQueen;
    final JMenuItem itemPromotionKnight;
    final JMenuItem itemPromotionBishop;
    final JMenuItem itemPromotionRook;
    final JPopupMenu menuPromotion;
    final JMenuItem itemAccept;
    final JMenuItem itemDecline;
    final JPopupMenu menuDraw;
    final JMenuItem itemUndo;
    final JMenuItem itemRedo;
    final JMenuItem itemRotateBoard;
    final JMenuItem itemFromFEN;
    final JMenuItem itemRename;
    final JMenuItem itemAssignOpponentBlack;
    final JMenuItem itemAssignOpponentWhite;
    final JMenuItem itemResign;
    final JMenuItem itemOfferDraw;
    final JTextField chatInput;
    final JTextArea chatOutput;
    final int SIZE_L = 70;
    final int SIZE_M = 45;
    final int SIZE_S = 30;
    String VERSION = "VERSION UNKNOWN";
    String myName;
    String myPassword;
    String myFriendsName = "";

    public Window(Chess chess) {

        frame.setResizable(false);
        frame.setTitle("Schach -OFFLINE- ");
        ColorScheme colorScheme = new ColorScheme();
        appearanceSettings = new AppearanceSettings(colorScheme);

        getStoredSettings();
        myName = getSetting("%NAME");
        myPassword = getSetting("%PW");
        {
            String xy = getSetting("%LOCATION");
            if (!xy.equals("")) {
                String[] args = xy.split("/");
                Point location = new Point((int) (Float.parseFloat(args[0])), (int) (Float.parseFloat(args[1])));
                frame.setLocation(location);
            }
        }

        try {
            String title = "Schach -OFFLINE- ";
            FileInputStream stream = new FileInputStream("version.txt");
            Scanner scanner = new Scanner(stream);
            VERSION = scanner.nextLine();
            title += VERSION;
            if (!myName.equals("")) title += " -" + myName + "-";
            frame.setTitle(title);
            scanner.close();
        } catch (FileNotFoundException ex) {
            System.err.println("VERSION FILE NOT FOUND");
        }

        int frameSize = SIZE_S;
        {
            String style = getSetting("%STYLE");
            if (!style.equals("")) colorScheme.setColors(Integer.parseInt(style));

            String pieces = getSetting("%PIECES");
            if (!pieces.equals("")) appearanceSettings.setFont(Integer.parseInt(pieces));

            String size = getSetting("%SIZE");
            if (!size.equals("")) frameSize = Integer.parseInt(size);
        }

        messageMenu = new JPopupMenu();
        messageItem = new JMenuItem();

        labelCapturedWhitePieces = new JLabel("", JLabel.CENTER);
        labelCapturedBlackPieces = new JLabel("", JLabel.CENTER);
        labelPlaceHolderWest = new JLabel("", JLabel.LEFT);
        labelPlaceHolderEast = new JLabel("", JLabel.RIGHT);
        board = new Board(SIZE_S, chess, appearanceSettings);

        content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        labelCapturedWhitePieces.setFont(appearanceSettings.font);
        labelCapturedBlackPieces.setFont(appearanceSettings.font);
        labelPlaceHolderWest.setFont(appearanceSettings.font);
        labelPlaceHolderEast.setFont(appearanceSettings.font);
        labelCapturedWhitePieces.setOpaque(true);
        labelCapturedBlackPieces.setOpaque(true);
        labelPlaceHolderWest.setOpaque(true);
        labelPlaceHolderEast.setOpaque(true);
        labelPlaceHolderWest.setVerticalAlignment(JLabel.CENTER);
        labelPlaceHolderEast.setVerticalAlignment(JLabel.CENTER);
        labelCapturedWhitePieces.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelCapturedBlackPieces.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelPlaceHolderWest.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelPlaceHolderEast.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);

        frame.setContentPane(content);

        itemConnectWithPlayer = new JMenuItem("Mit zufälligem Spieler verbinden");
        itemShowChat = new JMenuItem("Chat anzeigen");
        itemRatingQuery = new JMenuItem("ELO-Rating");
        itemResign = new JMenuItem("AUFGEBEN");
        itemOfferDraw = new JMenuItem("REMIS ANBIETEN");
        itemNewNetworkGame = new JMenuItem("Neues Spiel");
        itemAssignOpponentBlack = new JMenuItem("Ich spiele WEISS");
        itemAssignOpponentWhite = new JMenuItem("Ich spiele SCHWARZ");
        itemStartServer = new JMenuItem("1 to 1 Server");
        itemStartClient = new JMenuItem("1 to 1 Client");
        itemConnectToServer = new JMenuItem("Mit Schachserver verbinden");
        itemNetworkDisconnect = new JMenuItem("Verbindung trennen");
        itemSendFeedback = new JMenuItem("Feedback senden");

        itemNewGame = new JMenuItem("Neu");
        itemStore = new JMenuItem("Speichern");
        itemRestore = new JMenuItem("Wiederherstellen");
        itemBegin = new JMenuItem("Siel starten");
        itemLicense = new JMenuItem("Info und Lizenz");
        itemCheckVersion = new JMenuItem("Version überprüfen");
        itemShowVersionLog = new JMenuItem("LOG anzeigen");

        itemSize1 = new JMenuItem("groß");
        itemSize2 = new JMenuItem("mittel");
        itemSize3 = new JMenuItem("klein");
        itemEnlarge = new JMenuItem("+ + +");
        itemDiminish = new JMenuItem("- - -");
        itemChangePieceStyle = new JMenuItem("Schachfiguren");

        itemCastlingKingside = new JMenuItem("kurz");
        itemCastlingQueenside = new JMenuItem("lang");

        Font promotionItemFont = new Font("Times", Font.PLAIN, 100);
        itemPromotionQueen = new JMenuItem();
        itemPromotionKnight = new JMenuItem();
        itemPromotionBishop = new JMenuItem();
        itemPromotionRook = new JMenuItem();

        itemAccept = new JMenuItem(" ja ");
        itemDecline = new JMenuItem("nein");

        itemUndo = new JMenuItem("<<");
        itemRedo = new JMenuItem(">>");

        itemRotateBoard = new JMenuItem("Brett drehen");
        itemFromFEN = new JMenuItem("Spiel beginnen aus FEN");
        itemRename = new JMenuItem("Namen ändern");

        JMenuItem itemColorStandard = new JMenuItem("standard");
        JMenuItem itemColorPlain = new JMenuItem("schlicht");
        JMenuItem itemColorDark = new JMenuItem("dunkel");

        JMenu mainMenu = new JMenu("Spiel...");
        JMenu sizeMenu = new JMenu("Größe...");
        JMenu styleMenu = new JMenu("Stil...");
        JMenu moveMenu = new JMenu("Zug...");
        JMenu castlingMenu = new JMenu("Rochade...");
        JMenu networkMenu = new JMenu("Online...");
        JMenu extrasMenu = new JMenu("Extras...");
        JMenu onlineExtraMenu = new JMenu("Mehr...");
        JMenu versionMenu = new JMenu("Version...");

        menuPromotion = new JPopupMenu();
        menuPromotion.setFont(promotionItemFont);
        menuDraw = new JPopupMenu();

        mainMenu.add(itemNewGame);
        mainMenu.add(itemStore);
        mainMenu.add(itemRestore);
        mainMenu.add(itemBegin);
        mainMenu.addSeparator();
        mainMenu.add(itemLicense);
        versionMenu.add(itemCheckVersion);
        versionMenu.add(itemShowVersionLog);
        mainMenu.add(versionMenu);

        sizeMenu.add(itemSize1);
        sizeMenu.add(itemSize2);
        sizeMenu.add(itemSize3);
        sizeMenu.addSeparator();
        sizeMenu.add(itemEnlarge);
        sizeMenu.add(itemDiminish);

        styleMenu.add(itemColorStandard);
        styleMenu.add(itemColorPlain);
        styleMenu.add(itemColorDark);
        styleMenu.addSeparator();
        styleMenu.add(itemChangePieceStyle);

        moveMenu.add(itemUndo);
        moveMenu.add(itemRedo);
        moveMenu.addSeparator();
        moveMenu.add(castlingMenu);

        networkMenu.add(itemConnectWithPlayer);
        networkMenu.addSeparator();

        networkMenu.addSeparator();
        networkMenu.add(itemResign);
        networkMenu.add(itemOfferDraw);
        networkMenu.addSeparator();
        networkMenu.add(itemShowChat);
        networkMenu.add(itemRatingQuery);
        networkMenu.addSeparator();
        networkMenu.add(onlineExtraMenu);
        onlineExtraMenu.add(itemStartClient);
        onlineExtraMenu.add(itemStartServer);
        onlineExtraMenu.addSeparator();
        onlineExtraMenu.add(itemNetworkDisconnect);
        onlineExtraMenu.addSeparator();
        onlineExtraMenu.add(itemConnectToServer);
        onlineExtraMenu.addSeparator();
        onlineExtraMenu.addSeparator();
        onlineExtraMenu.add(itemSendFeedback);
        onlineExtraMenu.addSeparator();
        onlineExtraMenu.add(itemNewNetworkGame);
        onlineExtraMenu.add(itemAssignOpponentBlack);
        onlineExtraMenu.add(itemAssignOpponentWhite);

        castlingMenu.add(itemCastlingKingside);
        castlingMenu.add(itemCastlingQueenside);

        extrasMenu.add(itemRotateBoard);
        extrasMenu.add(itemFromFEN);
        extrasMenu.add(itemRename);

        chatInput = new JTextField();
        chatOutput = new JTextArea();

        menuPromotion.add(itemPromotionQueen);
        menuPromotion.add(itemPromotionRook);
        menuPromotion.add(itemPromotionBishop);
        menuPromotion.add(itemPromotionKnight);

        menuDraw.add(itemAccept);
        menuDraw.add(itemDecline);

        menuBar = new JMenuBar();
        menuBar.add(mainMenu);
        menuBar.add(moveMenu);
        menuBar.add(extrasMenu);
        menuBar.add(sizeMenu);
        menuBar.add(styleMenu);
        menuBar.add(networkMenu);
        frame.add(menuPromotion);
        frame.setJMenuBar(menuBar);
        setStyleSettings();

        content.add(labelCapturedWhitePieces, BorderLayout.NORTH);
        content.add(labelPlaceHolderWest, BorderLayout.WEST);
        content.add(board, BorderLayout.CENTER);
        content.add(labelPlaceHolderEast, BorderLayout.EAST);
        content.add(labelCapturedBlackPieces, BorderLayout.SOUTH);

        itemRestore.setEnabled(false);
        itemStore.setEnabled(false);
        itemBegin.setEnabled(false);

        adjustBoardAndFrameSize(frameSize);
        frame.setVisible(true);

        checkVersion();

        if (myName.equals("") || myPassword.equals("")) {
            new DialogRename();
        }

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
        itemEnlarge.addActionListener(e -> {
            adjustBoardAndFrameSize(appearanceSettings.sizeFactor * 6 / 5);
            board.repaint();
        });
        itemDiminish.addActionListener(e -> {
            adjustBoardAndFrameSize(appearanceSettings.sizeFactor * 5 / 6);
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

        itemLicense.addActionListener(e -> {
            String license;
            license = getStringFromFile("LICENSE");
            license = "Schach " + VERSION + "\n\n" + license;
            new DialogText(license, frame.getLocation());
        });

        itemCheckVersion.addActionListener(e -> checkVersion());

        itemShowVersionLog.addActionListener(e ->
                new DialogText(getStringFromFile("version.txt"), frame.getLocation()));

        itemRename.addActionListener(e -> new DialogRename());

        /*  #################################### \add action listeners\ ############################################# */
    }

    public static void changeTitle(String toReplace, String replacement) {
        frame.setTitle(frame.getTitle().replace(toReplace, replacement));
    }

    public String replaceChessCharacters(String in) {
        char[] temp = in.toCharArray();
        HashMap<Character, Character> charConverter;

        switch (appearanceSettings.font.getFontName()) {
            case "Chess Regular":
                charConverter = Util.SYMBOL_SARAH;
                break;
            case "DejaVu Sans":
            case "Dialog.plain": // "Times"
            case "MS Gothic":
                charConverter = Util.SYMBOLS;
                break;
            default:
                charConverter = Util.SYMBOL_SCF;
        }

        for (int i = 0; i < temp.length; i++) {
            Character replacement = charConverter.get(temp[i]);
            if (replacement != null)
                temp[i] = charConverter.get(temp[i]);
        }

        return new String(temp);
    }

    void checkVersion() {
        if (!VERSION.equals("VERSION UNKNOWN")) {
            String URL = "https://raw.githubusercontent.com/martinbro2021/fineChessEngineAndGui/main/version.txt";
            String latestVersion = Downloader.getHeadLineFromURL(URL);
            if (!latestVersion.equals("") && !latestVersion.equals(VERSION)) {
                new DialogMessage("Ein neueres Programm [Version " + latestVersion + "] steht auf github.com zur Verfügung!",
                        frame.getLocation());
            } else if (!latestVersion.equals("")) {
                showPopup("Deine Version ist aktuell - cool!");
            }
        }
    }

    private void adjustBoardAndFrameSize(int size_factor) {

        appearanceSettings.adjustSize(size_factor);
        adjustBoardAndFrameSize();
    }

    private void adjustBoardAndFrameSize() {

        Dimension newDim;

        newDim = new Dimension(appearanceSettings.sizeFactor * 2, appearanceSettings.margin);
        labelPlaceHolderWest.setPreferredSize(newDim);
        labelPlaceHolderEast.setPreferredSize(newDim);

        newDim = new Dimension(appearanceSettings.sizeFactor * 2 + appearanceSettings.margin,
                appearanceSettings.sizeFactor * 2);
        labelCapturedWhitePieces.setPreferredSize(newDim);
        labelCapturedBlackPieces.setPreferredSize(newDim);

        newDim = new Dimension(appearanceSettings.sizeFactor * 4, appearanceSettings.margin * 2);
        chatOutput.setPreferredSize(newDim);

        board.adjustSize();
        setStyleSettings();

        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        frame.pack();
    }

    public void showPopup(String message) {

        board.setActive(false); //makes board diffuse

        chatOutput.append(message + "\n");
        messageMenu.add(messageItem);
        messageMenu.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        messageMenu.setPopupSize(appearanceSettings.margin, appearanceSettings.sizeFactor * 2);
        messageItem.setText(message);
        messageItem.setFont(new Font("Times", Font.PLAIN, appearanceSettings.sizeFactor / 3));
        messageItem.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);

        messageMenu.show(board, 0, appearanceSettings.margin);
    }

    public void showPromotionPopup() {

        board.setActive(false); //makes board diffuse

        JPopupMenu menu = menuPromotion;
        menu.setPopupSize(appearanceSettings.sizeFactor * 4 / 2, appearanceSettings.margin);

        itemPromotionBishop.setBackground(appearanceSettings.colorScheme.BLACK_SQUARES_COLOR);
        itemPromotionKnight.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        itemPromotionQueen.setBackground(appearanceSettings.colorScheme.BLACK_SQUARES_COLOR);
        itemPromotionRook.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);

        itemPromotionBishop.setFont(appearanceSettings.font);
        itemPromotionKnight.setFont(appearanceSettings.font);
        itemPromotionQueen.setFont(appearanceSettings.font);
        itemPromotionRook.setFont(appearanceSettings.font);

        itemPromotionBishop.setText(replaceChessCharacters("B b"));
        itemPromotionKnight.setText(replaceChessCharacters("N n"));
        itemPromotionQueen.setText(replaceChessCharacters("Q q"));
        itemPromotionRook.setText(replaceChessCharacters("R r"));

        menu.show(board, board.getWidth(), 0);
    }

    public void showDrawPopup() {

        labelCapturedWhitePieces.setText(" Remis akzeptieren? ");
        board.setActive(false); //makes board diffuse

        JPopupMenu menu = menuDraw;
        menu.setPopupSize(appearanceSettings.sizeFactor * 4 / 2, appearanceSettings.margin);

        itemAccept.setBackground(appearanceSettings.colorScheme.BLACK_SQUARES_COLOR);
        itemDecline.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);

        itemAccept.setFont(appearanceSettings.font);
        itemDecline.setFont(appearanceSettings.font);

        menu.show(board, board.getWidth(), 0);
    }

    public void setStyleSettings() {

        menuBar.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelCapturedWhitePieces.setFont(appearanceSettings.font);
        labelCapturedBlackPieces.setFont(appearanceSettings.font);
        Font westEastFont = new Font("Courier New", Font.PLAIN, appearanceSettings.sizeFactor * 2 / 5);
        labelPlaceHolderWest.setFont(westEastFont);
        labelPlaceHolderEast.setFont(westEastFont);
        labelCapturedWhitePieces.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelCapturedBlackPieces.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelPlaceHolderWest.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelPlaceHolderEast.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
    }

    class DialogRename extends DialogNameAndPassword {

        public DialogRename() {
            super(frame.getLocation());
        }

        public void buttonClicked() {

            myName = nameIn.getText();
            myPassword = Password.toSHA256String(nameIn.getText() + pwIn.getText());
            dispose();
        }
    }
}

