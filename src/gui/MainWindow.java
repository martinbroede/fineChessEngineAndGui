package gui;

import core.ChessModel;
import core.Util;
import gui.chessBoard.Board;
import gui.dialogs.DialogName;
import gui.dialogs.DialogTextArea;
import misc.Downloader;
import misc.Password;
import network.Network;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import static misc.FileHandling.getStringFromFile;
import static misc.Properties.resourceBundle;
import static misc.StaticSetting.getSetting;
import static misc.StaticSetting.getStoredSettings;

public class MainWindow implements ChatSubscriber {

    public static final String CHESS_REGULAR = "Chess Regular";
    public static final String DEJA_VU_SANS = "DejaVu Sans";
    public static final String DIALOG_PLAIN = "Dialog.plain";
    public static final String MS_GOTHIC = "MS Gothic";
    public static final String TIMES = "Times";
    public static final String COURIER_NEW = "Courier New";
    public static final String VERSION_UNKNOWN = "VERSION UNKNOWN";
    public static final String START_GAME = "start.game";
    public static final String CHESS_OFFLINE = "chess.offline";
    public static final String VERSION_TXT = "version.txt";
    public static final String OFFLINE = "-OFFLINE-";
    public static final String ONLINE = "-ONLINE-";
    private static final String D_00 = "%d:00";
    public final int SIZE_L = 70;
    public final int SIZE_M = 45;
    public final int SIZE_S = 30;
    public final Network network;
    public final ChatWindow chatWindow;
    final AppearanceSettings appearanceSettings = new AppearanceSettings();
    final JFrame frame = new JFrame();
    final JMenuItem itemConnectWithPlayer = new JMenuItem(resourceBundle.getString("connect.random"));
    final JMenuItem itemChallengePlayer = new JMenuItem(resourceBundle.getString("challenge.player"));
    final JMenuItem itemShowChat = new JMenuItem(resourceBundle.getString("show.chat"));
    final JMenuItem itemRatingQuery = new JMenuItem(resourceBundle.getString("elo.rating"));
    final JMenuItem itemResign = new JMenuItem(resourceBundle.getString("resign"));
    final JMenuItem itemOfferDraw = new JMenuItem(resourceBundle.getString("offer.draw"));
    final JMenuItem itemNewNetworkGame = new JMenuItem(resourceBundle.getString("new.game"));
    final JMenuItem itemAssignOpponentBlack = new JMenuItem(resourceBundle.getString("play.white"));
    final JMenuItem itemAssignOpponentWhite = new JMenuItem(resourceBundle.getString("play.black"));
    final JMenuItem itemStartServer = new JMenuItem(resourceBundle.getString("1.to.1.server"));
    final JMenuItem itemStartClient = new JMenuItem(resourceBundle.getString("1.to.1.client"));
    final JMenuItem itemConnectToServer = new JMenuItem(resourceBundle.getString("connect.to.chess.server"));
    final JMenuItem itemNetworkDisconnect = new JMenuItem(resourceBundle.getString("disconnect"));
    final JMenuItem itemSendFeedback = new JMenuItem(resourceBundle.getString("send.feedback"));
    final JMenuItem itemNewGame = new JMenuItem(resourceBundle.getString("new"));
    final JMenuItem itemStore = new JMenuItem(resourceBundle.getString("store"));
    final JMenuItem itemRestore = new JMenuItem(resourceBundle.getString("restore"));
    final JMenuItem itemBegin = new JMenuItem(resourceBundle.getString(START_GAME));
    final JMenuItem itemLicense = new JMenuItem(resourceBundle.getString("info.license"));
    final JMenuItem itemCheckVersion = new JMenuItem(resourceBundle.getString("check.version"));
    final JMenuItem itemShowVersionLog = new JMenuItem(resourceBundle.getString("show.log"));
    final JMenuItem itemChangePieceStyle = new JMenuItem(resourceBundle.getString("chessmen"));
    final JMenuItem itemSize1 = new JMenuItem(resourceBundle.getString("large"));
    final JMenuItem itemSize2 = new JMenuItem(resourceBundle.getString("medium"));
    final JMenuItem itemSize3 = new JMenuItem(resourceBundle.getString("small"));
    final JMenuItem itemEnlarge = new JMenuItem("+ + +");
    final JMenuItem itemDiminish = new JMenuItem("- - -");
    final JMenuItem itemCastlingKingSide = new JMenuItem(resourceBundle.getString("castling.oo"));
    final JMenuItem itemCastlingQueenSide = new JMenuItem(resourceBundle.getString("castling.ooo"));
    final JMenuItem itemUndo = new JMenuItem("<<");
    final JMenuItem itemRedo = new JMenuItem(">>");
    final JMenuItem itemPromotionQueen = new JMenuItem();
    final JMenuItem itemPromotionKnight = new JMenuItem();
    final JMenuItem itemPromotionBishop = new JMenuItem();
    final JMenuItem itemPromotionRook = new JMenuItem();
    final JMenuItem itemAccept = new JMenuItem(resourceBundle.getString("yes"));
    final JMenuItem itemDecline = new JMenuItem(resourceBundle.getString("no"));
    final JMenuItem itemRotateBoard = new JMenuItem(resourceBundle.getString("rotate.board"));
    final JMenuItem itemFromFEN = new JMenuItem(resourceBundle.getString("from.fen"));
    final JMenuItem itemRename = new JMenuItem(resourceBundle.getString("rename"));
    final JPopupMenu menuPromotion = new JPopupMenu();
    final JPopupMenu menuDraw = new JPopupMenu();
    final JLabel labelCapturedWhitePieces = new JLabel("", JLabel.CENTER);
    final JLabel labelCapturedBlackPieces = new JLabel("", JLabel.CENTER);
    final JLabel labelPlaceHolderWest = new JLabel("", JLabel.LEFT);
    final JLabel labelPlaceHolderEast = new JLabel("", JLabel.RIGHT);
    final JPopupMenu messageMenu = new JPopupMenu();
    final JMenuItem messageItem = new JMenuItem();
    final JPanel content = new JPanel();
    final JMenuBar menuBar = new JMenuBar();
    final Board board;
    String VERSION = VERSION_UNKNOWN;
    String myFriendsName = "?";
    String myName;
    String myPassword;

    public MainWindow(ChessModel chessModel) {

        frame.setResizable(false);
        frame.setTitle(resourceBundle.getString(CHESS_OFFLINE));

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
            String title = resourceBundle.getString(CHESS_OFFLINE);
            FileInputStream stream = new FileInputStream(VERSION_TXT);
            Scanner scanner = new Scanner(stream);
            VERSION = scanner.nextLine();
            title += VERSION;
            if (!myName.equals("")) title = String.format("%s -%s-", title, myName);
            frame.setTitle(title);
            scanner.close();
        } catch (FileNotFoundException ex) {
            System.out.println("VERSION FILE NOT FOUND");
        }

        int frameSize = SIZE_S;
        {
            String style = getSetting("%STYLE");
            if (!style.equals("")) appearanceSettings.colorScheme.setColors(Integer.parseInt(style));

            String pieces = getSetting("%PIECES");
            if (!pieces.equals("")) appearanceSettings.setFont(Integer.parseInt(pieces));

            String size = getSetting("%SIZE");
            if (!size.equals("")) frameSize = Integer.parseInt(size);
        }

        board = new Board(SIZE_S, chessModel, appearanceSettings); // todo refactor to Gui so you don't need a chessModel instance here
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

        JMenuItem itemColorStandard = new JMenuItem(resourceBundle.getString("standard"));
        JMenuItem itemColorPlain = new JMenuItem(resourceBundle.getString("plain"));
        JMenuItem itemColorDark = new JMenuItem(resourceBundle.getString("dark"));

        JMenu menuMain = new JMenu(resourceBundle.getString("game"));
        JMenu menuSize = new JMenu(resourceBundle.getString("size"));
        JMenu menuStyle = new JMenu(resourceBundle.getString("style"));
        JMenu menuMove = new JMenu(resourceBundle.getString("move"));
        JMenu menuCastling = new JMenu(resourceBundle.getString("castling"));
        JMenu menuNetwork = new JMenu(resourceBundle.getString("online"));
        JMenu menuExtras = new JMenu(resourceBundle.getString("extras"));
        JMenu menuOnlineExtras = new JMenu(resourceBundle.getString("more"));
        JMenu menuVersion = new JMenu(resourceBundle.getString("version"));

        menuMain.add(itemNewGame);
        menuMain.add(itemStore);
        menuMain.add(itemRestore);
        menuMain.add(itemBegin);
        menuMain.addSeparator();
        menuMain.add(itemLicense);
        menuVersion.add(itemCheckVersion);
        menuVersion.add(itemShowVersionLog);
        menuMain.add(menuVersion);

        menuSize.add(itemSize1);
        menuSize.add(itemSize2);
        menuSize.add(itemSize3);
        menuSize.addSeparator();
        menuSize.add(itemEnlarge);
        menuSize.add(itemDiminish);

        menuStyle.add(itemColorStandard);
        menuStyle.add(itemColorPlain);
        menuStyle.add(itemColorDark);
        menuStyle.addSeparator();
        menuStyle.add(itemChangePieceStyle);

        menuMove.add(itemUndo);
        menuMove.add(itemRedo);
        menuMove.addSeparator();
        menuMove.add(menuCastling);

        menuNetwork.add(itemConnectWithPlayer);
        menuNetwork.add(itemChallengePlayer);
        menuNetwork.addSeparator();

        menuNetwork.addSeparator();
        menuNetwork.add(itemResign);
        menuNetwork.add(itemOfferDraw);
        menuNetwork.addSeparator();
        menuNetwork.add(itemShowChat);
        menuNetwork.add(itemRatingQuery);
        menuNetwork.addSeparator();
        menuNetwork.add(menuOnlineExtras);
        menuOnlineExtras.add(itemStartClient);
        menuOnlineExtras.add(itemStartServer);
        menuOnlineExtras.addSeparator();
        menuOnlineExtras.add(itemNetworkDisconnect);
        menuOnlineExtras.addSeparator();
        menuOnlineExtras.add(itemConnectToServer);
        menuOnlineExtras.addSeparator();
        menuOnlineExtras.addSeparator();
        menuOnlineExtras.add(itemSendFeedback);
        menuOnlineExtras.addSeparator();
        menuOnlineExtras.add(itemNewNetworkGame);
        menuOnlineExtras.add(itemAssignOpponentBlack);
        menuOnlineExtras.add(itemAssignOpponentWhite);

        menuCastling.add(itemCastlingKingSide);
        menuCastling.add(itemCastlingQueenSide);

        menuExtras.add(itemRotateBoard);
        menuExtras.add(itemFromFEN);
        menuExtras.add(itemRename);

        menuPromotion.add(itemPromotionQueen);
        menuPromotion.add(itemPromotionRook);
        menuPromotion.add(itemPromotionBishop);
        menuPromotion.add(itemPromotionKnight);

        menuDraw.add(itemAccept);
        menuDraw.add(itemDecline);

        JMenu clockMenu = new JMenu(resourceBundle.getString("chess.clock"));
        int[] minutes = {1, 3, 5, 10, 15, 20, 30, 45};
        ButtonGroup clockGroup = new ButtonGroup();
        for (int min : minutes) {
            JRadioButtonMenuItem btn = new JRadioButtonMenuItem(String.format(D_00, min));
            btn.addActionListener(e -> {
                chessModel.setTime(min);
                if (!chessModel.isStarted()) {
                    chessModel.getClock().initialize(min);
                } else
                    System.out.println("GAME STARTED - CAN NOT RESET CLOCK");
                System.out.printf("%d MINUTES%n", min); // todo remove
            });
            clockGroup.add(btn);
            clockMenu.add(btn);
        }

        menuBar.add(menuMain);
        menuBar.add(menuMove);
        menuBar.add(menuExtras);
        menuBar.add(menuSize);
        menuBar.add(menuStyle);
        menuBar.add(clockMenu);
        menuBar.add(menuNetwork);
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

        network = new Network() {
            @Override
            public void showMessage(String msg) {
                showMessageDialog(msg);
            }

            public void notifyOnlineToOffline() {
                changeTitle(ONLINE, OFFLINE);
            }
        };

        chatWindow = new ChatWindow(this) {
            @Override
            void sendMsg() {
                if (chatInput.getText().startsWith("%")) chatSub.sendChat(chatInput.getText());
                else chatSub.sendChat(String.format("%%CHAT %s", chatInput.getText()));
                addChatMessage(String.format("%s: %s\n", chatSub.getName(), chatInput.getText()));
                chatInput.setText("");
            }
        };

        checkVersion();

        /*  ######################################    add    listeners  ############################################# */

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
            appearanceSettings.colorScheme.setColors(ColorScheme.STANDARD);
            setStyleSettings();
            board.repaint();
        });
        itemColorPlain.addActionListener(e -> {
            appearanceSettings.colorScheme.setColors(ColorScheme.PLAIN);
            setStyleSettings();
            board.repaint();
        });
        itemColorDark.addActionListener(e -> {
            appearanceSettings.colorScheme.setColors(ColorScheme.DARK);
            setStyleSettings();
            board.repaint();
        });

        itemLicense.addActionListener(e -> {
            String license;
            license = getStringFromFile("LICENSE");
            license = String.format(resourceBundle.getString("chess.placeholder"), VERSION, license);
            new DialogTextArea(license, frame.getLocation());
        });

        itemCheckVersion.addActionListener(e -> checkVersion());

        itemShowVersionLog.addActionListener(e ->
                new DialogTextArea(getStringFromFile(VERSION_TXT), frame.getLocation()));

        /*  ####################################   \add     listeners\  ############################################# */
    }

    public void changeTitle(String toReplace, String replacement) {
        frame.setTitle(frame.getTitle().replace(toReplace, replacement));
    }

    public void showMessageDialog(String message) {
        new Thread(() -> JOptionPane.showMessageDialog(frame, message)).start();
    }

    public AppearanceSettings getAppearanceSettings() {
        return appearanceSettings;
    }

    public void sendChat(String chat) {
        network.send(chat);
    }

    public String getName() {
        return myName;
    }

    public void getNameAndPassword() {

        String[] nameAndPw = DialogName.getNameAndPassword(frame);
        myName = nameAndPw[DialogName.NAME];
        myPassword = nameAndPw[DialogName.PASSWORD];
        myPassword = Password.toSHA256String(myName + myPassword);
    }

    public String replaceChessCharacters(String in) {

        char[] temp = in.toCharArray();
        HashMap<Character, Character> charConverter;

        switch (appearanceSettings.font.getFontName()) {
            case CHESS_REGULAR:
                charConverter = Util.SYMBOL_SARAH;
                break;
            case DEJA_VU_SANS:
            case DIALOG_PLAIN: // "Times"
            case MS_GOTHIC:
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

        if (!VERSION.equals(VERSION_UNKNOWN)) {
            String URL = "https://raw.githubusercontent.com/martinbro2021/fineChessEngineAndGui/main/version.txt";
            String latestVersion = Downloader.getHeadLineFromURL(URL);
            if (!latestVersion.equals("") && !latestVersion.equals(VERSION)) {
                String message = String.format(resourceBundle.getString("version.check"), latestVersion);
                showMessageDialog(message);
            } else if (!latestVersion.equals("")) {
                showPopup(resourceBundle.getString("up.to.date"));
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

        board.adjustSize();
        setStyleSettings();

        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        frame.pack();
    }

    public void showPopup(String message) {

        board.setActive(false); //makes board diffuse

        messageMenu.add(messageItem);
        messageMenu.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        messageMenu.setPopupSize(appearanceSettings.margin, appearanceSettings.sizeFactor * 2);
        messageItem.setText(message);
        messageItem.setFont(new Font(TIMES, Font.PLAIN, appearanceSettings.sizeFactor / 3));
        messageItem.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        messageMenu.show(board, 0, appearanceSettings.margin);

        chatWindow.addChatMessage(message);
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

        labelCapturedWhitePieces.setText(resourceBundle.getString("accept.draw"));
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
        Font westEastFont = new Font(COURIER_NEW, Font.PLAIN, appearanceSettings.sizeFactor * 2 / 5);
        labelPlaceHolderWest.setFont(westEastFont);
        labelPlaceHolderEast.setFont(westEastFont);
        labelCapturedWhitePieces.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelCapturedBlackPieces.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelPlaceHolderWest.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
        labelPlaceHolderEast.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);
    }
}
