package gui;

import chessNetwork.Network;
import chessNetwork.TestReachability;
import core.*;
import fileHandling.ReadWrite;
import gui.dialogs.DialogInput;
import gui.dialogs.DialogMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import static fileHandling.StaticSetting.rememberSetting;
import static fileHandling.StaticSetting.storeSettingsInFile;

public class Gui extends MainWindow {

    final boolean WHITE = Constants.WHITE; //for readability reasons
    final boolean BLACK = Constants.BLACK;
    final Network network;
    private final int DELAY_MILLISEC = 100; //update interval
    private final String storagePath = "chessUserData/currentGame.txt";
    private final ChatDialog chatDialog;
    private final Chess chess;
    private String moveString;
    private String moveStringSpecialMoves; //for promotion, castling, enPassant
    private String preferredClientIp;
    private String preferredServerIp;
    private String preferredClientPort;
    private String preferredServerPort;
    private boolean allowUndoMoves = true;
    private boolean userPlaysColor = WHITE;
    private boolean userPlaysBothColors = true;
    private boolean feature = false;
    private SimpleDateFormat timeParser = new SimpleDateFormat("mm:ss");

    public Gui(Chess chessGame) {

        super(chessGame);
        frame.addWindowListener(new WindowListener());
        chatDialog = new ChatDialog();
        this.chess = chessGame;
        moveString = "";
        moveStringSpecialMoves = "";
        network = new Network();
        NetworkRefresher networkRefresher = new NetworkRefresher();
        networkRefresher.start();

        /* #################################### add action listeners ################################################ */

        itemNewGame.addActionListener(e -> {
            System.out.println("NEW GAME");
            chess.newGame();
            labelCapturedBlackPieces.setText("");
            labelCapturedWhitePieces.setText("");
            userPlaysBothColors = true;
            refreshFrameContent(-1);
            resetMoveString();
            showPopup("Spiel beginnen");
        });

        itemStore.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });

        itemRestore.addActionListener(e -> {
/*            Object obj = null;
            try {
                obj = ReadWrite.readFromFile(storagePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (obj != null) {
                chess = (Chess) obj;
                board.setBoardArray(chess.getBoard());
                System.out.println("SUCCESSFULLY LOADED");
                board.repaint();
            }*/
        });

        itemBegin.addActionListener(e -> System.err.println("NOT YET IMPLEMENTED"));

        itemCastlingKingside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                Move nextMove = new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.out.println("CASTLING o-o ILLEGAL");

            } else { //BLACK
                Move nextMove = new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.out.println("CASTLING o-o ILLEGAL");
            }
            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemCastlingQueenside.addActionListener(e -> {
            if (chess.getTurnColor() == WHITE) {
                Move nextMove = new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.out.println("CASTLING o-o-o ILLEGAL");

            } else { //BLACK
                Move nextMove = new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING);
                if (!movePiece(nextMove))
                    System.out.println("CASTLING o-o-o ILLEGAL");
            }
            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemPromotionQueen.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "Q"));
            refreshFrameContent(-1);
        });

        itemPromotionRook.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "R"));
            refreshFrameContent(-1);
        });

        itemPromotionKnight.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "N"));
            refreshFrameContent(-1);
        });

        itemPromotionBishop.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "B"));
            refreshFrameContent(-1);
        });

        itemUndo.addActionListener(e -> {
            if (network.isActive()) {
                System.out.println("UNDO NOT ALLOWED WHEN NETWORK IS ACTIVE");
            } else {
                chess.userUndo();
                refreshFrameContent(-1);
            }
        });

        itemRedo.addActionListener(e -> {
            if (network.isActive()) {
                System.out.println("REDO NOT ALLOWED WHEN NETWORK IS ACTIVE");
            } else {
                chess.userRedo();
                refreshFrameContent(-1);
            }
        });

        itemChangePieceStyle.addActionListener(e -> {
            board.fontRoulette();
            setStyleSettings();
            refreshFrameContent(-1);
        });

        itemShowChat.addActionListener(e -> chatDialog.toggleVisibility());

        itemStartClient.addActionListener(e -> network.showClientIpDialog(frame.getLocation()));
        itemStartServer.addActionListener(e -> network.showServerIpDialog(frame.getLocation()));

        itemNewNetworkGame.addActionListener(e -> {
            chess.newGame();
            network.sendToNet("%MOVE " + Move.START_GAME);
            showPopup("Spiel beginnen");
        });
        itemNetworkDestroy.addActionListener(e -> network.safeDeleteServerOrClient());

        itemRotateBoard.addActionListener(e -> {
            board.toggleBoardOrientation();
            refreshFrameContent(-1);
        });

        itemFromFEN.addActionListener(e -> {
            new DialogFEN();
        });

        itemAssignOpponentBlack.addActionListener(e -> {
            network.sendToNet("%MOVE " + Move.OPPONENT_BLACK);
            userPlaysColor = WHITE;
            userPlaysBothColors = false;
            board.setWhitePlayerSouth();
            refreshFrameContent(-1);
            showPopup("Du spielst WEISS");
        });

        itemAssignOpponentWhite.addActionListener(e -> {
            network.sendToNet("%MOVE " + Move.OPPONENT_WHITE);
            userPlaysColor = BLACK;
            userPlaysBothColors = false;
            board.setWhitePlayerNorth();
            refreshFrameContent(-1);
            showPopup("Du spielst SCHWARZ");
        });

        itemResign.addActionListener(e -> {
            if (chess.getTurnColor() == userPlaysColor) {
                network.sendToNet("%MOVE " + Move.RESIGN);
                chess.userMove(new Move(Move.RESIGN), userPlaysColor, true);
                showPopup(chess.gameStatus.getStatusNotice());
            } else {
                showPopup("Gib auf, wenn du am Zug bist.");
            }
        });

        itemOfferDraw.addActionListener(e -> {
            if (chess.getTurnColor() == userPlaysColor) {
                network.sendToNet("%MOVE " + Move.OFFER_DRAW);
                chess.userMove(new Move(Move.OFFER_DRAW), userPlaysColor, true);
                showPopup("Remis angeboten...");
            } else {
                showPopup("Mache ein Angebot, wenn du am Zug bist.");
            }
        });

        itemAccept.addActionListener(e -> {
            network.sendToNet("%MOVE " + Move.ACCEPT_DRAW);
            chess.userMove(new Move(Move.ACCEPT_DRAW), userPlaysColor, true);
            showPopup(chess.gameStatus.getStatusNotice());
        });

        itemDecline.addActionListener(e -> {
            network.sendToNet("%MOVE " + Move.DECLINE_DRAW);
            chess.userMove(new Move(Move.DECLINE_DRAW), userPlaysColor, true);
            showPopup("Angebot abgelehnt.");
        });

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'R') {
                    new TestReachability();
                }
                if (feature && e.getKeyChar() == '\\') {
                    board.toggleShowHints();
                    refreshFrameContent(-1);
                }
                if (e.getKeyChar() == 'P') {
                    showPopup("Test PopUpMenu");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.isAltDown()) feature = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                feature = false;
            }
        });


        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                int btn = mouseEvent.getButton();
                switch (btn) {
                    case 1: {
                        processMouseEvent(mouseEvent);
                        break;
                    }
                    case 2: {
                        chess.castling.print();
                        chess.blackPieces.printCaptured();
                        chess.whitePieces.printCaptured();
                        break;
                    }
                    case 3: {
                        System.out.println(chess.getScore());
                        break;
                    }
                }
            }
        });
    }

    private boolean movePiece(Move move) {

        if (chess.userMove(move, userPlaysColor, userPlaysBothColors)) {

            if (network.isActive()) network.sendToNet("%MOVE " + move.getInformation());

            if (chess.gameStatus.getStatusCode() != GameStatus.UNDECIDED)
                showPopup(chess.gameStatus.getStatusNotice());

            return true;

        } else {
            return false;
        }
    }

    private void processMouseEvent(MouseEvent mouseEvent) {

        if (chess.gameStatus.getStatusCode() != GameStatus.UNDECIDED) {
            showPopup(chess.gameStatus.getStatusNotice());
        } else {
            byte pos = board.coordFromEvent(mouseEvent);

            if (moveString.equals("")) { // no square chosen yet
                if (chess.pieceAtSquare(pos, chess.getTurnColor())) {

                    moveString += Util.parse(pos) + " ";
                }
            } //you already chose a square:
            else if (!moveString.equals(Util.parse(pos) + " ")) //avoids moves like A1->A1! :
                moveString += Util.parse(pos) + " ";

            if (moveString.length() > 5) { // "A1 A2 ": from A1 to A2
                Move nextMove = new Move(moveString);

                if (!movePiece(nextMove)) {

                    /* move illegal? try castling moves */
                    if (nextMove.isFrom(Util.parse("E1")) && nextMove.getTo() == Util.parse("G1")) {
                        if (!movePiece(new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING)))
                            System.out.println("CASTLING o-o ILLEGAL");
                    } else if (nextMove.isFrom(Util.parse("E1")) && nextMove.getTo() == Util.parse("C1")) {
                        if (!movePiece(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                            System.out.println("CASTLING o-o-o ILLEGAL");
                    } else if (nextMove.isFrom(Util.parse("E8")) && nextMove.getTo() == Util.parse("G8")) {
                        if (!movePiece(new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING)))
                            System.out.println("CASTLING o-o ILLEGAL");
                    } else if (nextMove.isFrom(Util.parse("E8")) && nextMove.getTo() == Util.parse("C8")) {
                        if (!movePiece(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
                            System.out.println("CASTLING o-o-o ILLEGAL");
                    }

                    /* move illegal? try en passant moves */
                    else if (nextMove.isFromRank('4') || nextMove.isFromRank('5')) {
                        Move enPmove = new Move((short) (nextMove.getInformation() | Move.EN_PASSANT));
                        if (!movePiece(enPmove)) System.out.println("EN PASSANT CAPTURE ILLEGAL");
                    }

                    /* move illegal? try promotion moves */
                    else {
                        byte from = nextMove.getFrom();
                        byte to = nextMove.getTo();
                        Move promotionMove = new Move(from, to, Move.PROMOTION_QUEEN);

                        if (chess.getTurnColor() == WHITE) {
                            Moves legalMoves = chess.whitePieces.getPseudoLegalMoves();
                            if (legalMoves.contains(promotionMove)) {
                                moveStringSpecialMoves = moveString;
                                moveString = "";
                                showPromotionPopup();
                                return;
                            }
                        } else {
                            Moves legalMoves = chess.blackPieces.getPseudoLegalMoves();
                            if (legalMoves.contains(promotionMove)) {
                                moveStringSpecialMoves = moveString;
                                moveString = "";
                                showPromotionPopup();
                                return;
                            }
                        }
                        System.out.println("MOVE ILLEGAL (GUI)");
                    }
                }
                moveString = "";
            }
            refreshFrameContent(pos);
        }
    }

    public void refreshFrameContent(int pos) {

        short lastMove = chess.history.getLastMoveCoordinates();
        board.refreshChessBoard(true, true,
                chess.getUserLegalMoves(userPlaysColor, userPlaysBothColors).getMovesFrom((byte) pos), lastMove);

        labelCapturedWhitePieces.setText(" " + chess.whitePieces.getCapturedPiecesAsSymbols() + " ");
        labelCapturedBlackPieces.setText(" " + chess.blackPieces.getCapturedPiecesAsSymbols() + " ");

        {
            String space = "<br><br><br><br><br><br><br><br>";
            String textWest = "<html> &#160 &#x25a0" + space; //square and linefeed...
            String textEast = "<html> &#160 &#160 &#x25a1" + space;

            int score = Math.round((float) (chess.getScore() / 100));

            if (score < 0)
                textWest += " &#160 &#160 +" + Math.abs(score);
            else if (score > 0)
                textEast += " &#160 +" + Math.abs(score);

            textWest += space + " &#160 " + timeParser.format(new Date(chess.blackTime));
            textEast += space + timeParser.format(new Date(chess.whiteTime)) + " &#160 ";

            textWest += "</html>";
            textEast += "</html>";

            labelPlaceHolderWest.setText(textWest);
            labelPlaceHolderEast.setText(textEast);
        }
    }


    public void resetMoveString() {
        moveString = "";
    }

    class ChatDialog extends JDialog {

        public ChatDialog() {

            setLayout(new BorderLayout());
            JScrollPane scrollPane = new JScrollPane(chatOutput);
            add(scrollPane, BorderLayout.NORTH);
            add(chatInput, BorderLayout.SOUTH);
            chatOutput.setEnabled(false);
            chatOutput.setDisabledTextColor(Color.DARK_GRAY);
            pack();
            setResizable(false);

            chatInput.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (chatInput.getText().startsWith("%")) network.sendToNet(chatInput.getText());
                        else network.sendToNet("%CHAT " + chatInput.getText());
                        addChatMessage(myName + ": " + chatInput.getText() + "\n");
                        chatInput.setText("");
                    }
                }
            });
        }

        public void addChatMessage(String message) {

            chatOutput.append(message);
            while (chatOutput.getLineCount() >= appearanceSettings.getSizeFactor() / 2) {
                String text = chatOutput.getText();
                text = text.substring(text.indexOf('\n') + 1);
                chatOutput.setText(text);
            }
        }

        public void toggleVisibility() {

            Dimension newDim = new Dimension(appearanceSettings.getMargin(), appearanceSettings.getMargin());
            chatOutput.setPreferredSize(newDim);
            chatOutput.setBackground(appearanceSettings.getColorScheme().WHITE_SQUARES_COLOR);

            newDim = new Dimension(appearanceSettings.getMargin(), appearanceSettings.getSizeFactor());
            chatInput.setPreferredSize(newDim);
            chatInput.setBackground(appearanceSettings.getColorScheme().HIGHLIGHT_1_COLOR);

            Point location = frame.getLocation();
            location.translate(frame.getWidth(), 0);
            setLocation(location);

            pack();
            setVisible(!isVisible());
        }
    }

    class DialogFEN extends DialogInput {

        public DialogFEN() {

            super(
                    "Spiel aus FEN beginnen",
                    "FEN eingeben:",
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                    "OK",
                    board.getLocation());
        }

        @Override
        public void buttonKlicked() {

            chess.startFromFEN(input.getText());
            network.sendToNet("%FEN " + input.getText());
            refreshFrameContent(-1);
            dispose();
        }
    }

    class NetworkRefresher extends Thread {

        @Override
        public void run() {

            setName("GUI REFRESHER");

            while (true) {

                while (!network.isConnected()) {
                    try {
                        sleep(DELAY_MILLISEC);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                network.sendToNet("%VERSION?");
                network.sendToNet("%NAME?");

                while (network.isConnected()) {

                    if (network.messageQueue.size() > 0) {
                        String message;
                        try {
                            message = network.messageQueue.getFirst();
                            network.messageQueue.removeFirst();

                            String[] args = message.split(" ");
                            switch (args[0]) {
                                case "%MOVE":
                                    Move nextMove = new Move(Short.parseShort(args[1]));
                                    switch (nextMove.getInformation()) {
                                        case Move.START_GAME:
                                            chess.newGame();
                                            labelCapturedBlackPieces.setText("");
                                            labelCapturedWhitePieces.setText("");
                                            network.sendToNet("% Spiel gegen " + myName + " begonnen - " + VERSION);
                                            showPopup("Spiel beginnen");
                                            break;
                                        case Move.OPPONENT_BLACK:
                                            userPlaysColor = BLACK;
                                            board.setWhitePlayerNorth();
                                            userPlaysBothColors = false;
                                            showPopup("Du spielst SCHWARZ");
                                            break;
                                        case Move.OPPONENT_WHITE:
                                            userPlaysColor = WHITE;
                                            board.setWhitePlayerSouth();
                                            userPlaysBothColors = false;
                                            showPopup("Du spielst WEISS");
                                            break;
                                        case Move.OFFER_DRAW:
                                            chess.userMove(nextMove, userPlaysColor, true);
                                            showDrawPopup();
                                            break;
                                        case Move.DECLINE_DRAW:
                                            chess.userMove(nextMove, userPlaysColor, true);
                                            showPopup("Angebot abgelehnt.");
                                            break;
                                        case Move.ACCEPT_DRAW:
                                            chess.userMove(nextMove, userPlaysColor, true);
                                            showPopup(chess.gameStatus.getStatusNotice());
                                            break;
                                        case Move.RESIGN:
                                            chess.userMove(nextMove, userPlaysColor, true);
                                            showPopup(chess.gameStatus.getStatusNotice());
                                            break;
                                        default:
                                            chess.userMove(nextMove, userPlaysColor, true);
                                            refreshFrameContent(-1);
                                            if (chess.gameStatus.getStatusCode() != GameStatus.UNDECIDED) {
                                                showPopup(chess.gameStatus.getStatusNotice());
                                            }
                                    }
                                    break;
                                case "%FEN":
                                    chess.startFromFEN(message.replaceAll("%FEN ", ""));
                                    labelCapturedBlackPieces.setText("");
                                    labelCapturedWhitePieces.setText("");
                                    refreshFrameContent(-1);
                                    break;
                                case "%NOTE":
                                    showPopup(message.replace("%NOTE ", ""));
                                    break;
                                case "%ERROR":
                                    new DialogMessage(message.replace("%ERROR ", ""));
                                    break;
                                case "%VERSION?": // received version request
                                    network.sendToNet("%VERSION " + myName + " / " + VERSION);
                                    break;
                                case "%VERSION": // received friend's version
                                    chatOutput.append(message.replaceAll("%VERSION ", "") + "\n");
                                    break;
                                case "%NAME?": // received name request
                                    network.sendToNet("%NAME " + myName);
                                    break;
                                case "%NAME": // received friend's name
                                    myFriendsName = message.replace("%NAME ", "");
                                    System.out.println("YOU PLAY AGAINST " + myFriendsName);
                                    break;
                                case "%CHAT": // display chat in chatwindow
                                    chatDialog.addChatMessage(myFriendsName + ": " + message.replaceAll("%CHAT ", "") + "\n");
                                    break;
                                case "%": // show information in chatwindow
                                    chatDialog.addChatMessage(message.replaceAll("% ", "") + "\n");
                                    break;
                                default:
                                    System.out.println("WHAT SHOULD I DO WITH THIS CRAP: " + message + "?");

                            }
                        } catch (NoSuchElementException ex) {
                            System.err.println("MESSAGE QUEUE IS EMPTY");
                        }
                    }

                    try {
                        sleep(DELAY_MILLISEC);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    class WindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {

            rememberSetting("%NAME " + myName);
            rememberSetting("%SIZE " + appearanceSettings.getSizeFactor());
            rememberSetting("%STYLE " + appearanceSettings.getColorScheme().getCurrentScheme());
            rememberSetting("%LOCATION " + frame.getLocation().getX() + "/" + frame.getLocation().getY());
            storeSettingsInFile();
            e.getWindow().dispose();
            System.out.println("FINECHESS SAYS GOODBYE AND HAVE A NICE DAY");
            System.exit(0);
        }
    }
}