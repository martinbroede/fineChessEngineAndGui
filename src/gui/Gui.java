package gui;

import chessNetwork.Network;
import chessNetwork.Password;
import chessNetwork.Subscriber;
import core.*;
import fileHandling.ReadWrite;
import gui.dialogs.DialogFeedback;
import gui.dialogs.DialogInput;
import gui.dialogs.DialogMessage;
import gui.dialogs.DialogText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import static fileHandling.StaticSetting.rememberSetting;
import static fileHandling.StaticSetting.storeSettingsInFile;

public class Gui extends Window implements ClockSubscriber {

    public final Network network;
    final String SERVER_ADRESS = "chessnet.dynv6.net";
    final String SERVER_PORT = "/55555";
    final boolean WHITE = Constants.WHITE; //for readability reasons
    final boolean BLACK = Constants.BLACK;
    final String storagePath = "chessUserData/currentGame.txt";
    private final ChatDialog chatDialog;
    private final Chess chess;
    private final SimpleDateFormat timeParser = new SimpleDateFormat("mm:ss");
    private String moveString;
    private String moveStringSpecialMoves; //for promotion, castling, enPassant
    private boolean userPlaysColor = WHITE;
    private boolean userPlaysBothColors = true;
    private boolean feature = false;

    public Gui(Chess chessGame) {

        super(chessGame);
        chatDialog = new ChatDialog();
        this.chess = chessGame;
        chess.clock.callbackQuery(this);
        moveString = "";
        moveStringSpecialMoves = "";
        network = new Network();
        NetworkListener networkListener = new NetworkListener();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                networkListener.networkQuery();
            }
        }, 0, 100);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshLabels();
            }
        }, 0, 1000);

        /* todo remove
        Scanner in = new Scanner(System.in);
        while(chess.getTurnColor()){
            String init = in.nextLine().replace("_", " ");
            chess.newGame(init,Castling.NO_RIGHTS);
            refreshFrameContent(-1);
        }*/

        if (!(myName.equals("") || myPassword.equals(""))) {
            network.createClient(SERVER_ADRESS + SERVER_PORT);
        }

        /* ######################################### add listeners ################################################## */

        frame.addWindowListener(new WindowListener());

        itemNewGame.addActionListener(e -> {
            System.out.println("NEW GAME");
            chess.newGame();
            labelCapturedBlackPieces.setText("");
            labelCapturedWhitePieces.setText("");
            userPlaysBothColors = true;
            refreshFrameContent(-1);
            moveString = "";
            showPopup("Spiel beginnen");
        });

        itemStore.addActionListener(e -> {
            System.out.println("STORE GAME");
            ReadWrite.writeToFile(storagePath, chess);
        });

        itemBegin.addActionListener(e -> System.err.println("NOT YET IMPLEMENTED"));

        itemCastlingKingside.addActionListener(e -> {
            Move nextMove;
            if (chess.getTurnColor() == WHITE) {
                nextMove = new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING);

            } else { //BLACK
                nextMove = new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING);
            }
            if (!movePiece(nextMove))
                System.out.println("CASTLING o-o ILLEGAL");
            refreshFrameContent(-1); // -1 : don't want to paint legal moves.
        });

        itemCastlingQueenside.addActionListener(e -> {
            Move nextMove;
            if (chess.getTurnColor() == WHITE) {
                nextMove = new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING);

            } else { //BLACK
                nextMove = new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING);
            }
            if (!movePiece(nextMove))
                System.out.println("CASTLING o-o-o ILLEGAL");
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
            chess.userUndo();
            refreshFrameContent(-1);
        });

        itemRedo.addActionListener(e -> {
            chess.userRedo();
            refreshFrameContent(-1);
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
            network.send("%MOVE " + Move.START_GAME);
            showPopup("Spiel beginnen");
        });

        itemNetworkDisconnect.addActionListener(e -> network.disconnect());

        itemRotateBoard.addActionListener(e -> {
            board.toggleBoardOrientation();
            refreshFrameContent(-1);
        });

        itemFromFEN.addActionListener(e -> new DialogFEN());

        itemAssignOpponentBlack.addActionListener(e -> {
            network.send("%MOVE " + Move.OPPONENT_BLACK);
            userPlaysColor = WHITE;
            userPlaysBothColors = false;
            board.setWhitePlayerSouth();
            refreshFrameContent(-1);
        });

        itemAssignOpponentWhite.addActionListener(e -> {
            network.send("%MOVE " + Move.OPPONENT_WHITE);
            userPlaysColor = BLACK;
            userPlaysBothColors = false;
            board.setWhitePlayerNorth();
            refreshFrameContent(-1);
        });

        itemResign.addActionListener(e -> {
            if (chess.getTurnColor() == userPlaysColor && chess.getStatus() == Status.UNDECIDED) {
                chess.userMove(new Move(Move.RESIGN), userPlaysColor, true);
                network.send("%MOVE " + Move.RESIGN);
                processScoring();
                showPopup(chess.getStatusNotice());
            } else {
                showPopup("Gib auf, wenn du am Zug bist.");
            }
        });

        itemOfferDraw.addActionListener(e -> {
            if (chess.getTurnColor() == userPlaysColor) {
                network.send("%MOVE " + Move.OFFER_DRAW);
                chess.userMove(new Move(Move.OFFER_DRAW), userPlaysColor, true);
                showPopup("Remis angeboten...");
            } else {
                showPopup("Mache ein Angebot, wenn du am Zug bist.");
            }
        });

        itemAccept.addActionListener(e -> {
            chess.userMove(new Move(Move.ACCEPT_DRAW), userPlaysColor, true);
            network.send("%MOVE " + Move.ACCEPT_DRAW);
            processScoring();
            showPopup(chess.getStatusNotice());
        });

        itemDecline.addActionListener(e -> {
            network.send("%MOVE " + Move.DECLINE_DRAW);
            chess.userMove(new Move(Move.DECLINE_DRAW), userPlaysColor, true);
            showPopup("Angebot abgelehnt.");
        });

        itemConnectWithPlayer.addActionListener(e -> network.send("%SERVER LINK"));

        itemRatingQuery.addActionListener(e -> network.send("%SERVER ELO"));

        itemConnectToServer.addActionListener(e -> {
            network.disconnect();
            network.createClient(SERVER_ADRESS + SERVER_PORT);
        });

        itemSendFeedback.addActionListener(e -> new DialogFeedback("Hallo, Team von SoftPawn!\n\n" +
                "An diesem Programm\ngibt es absolut\nnichts auszusetzen!!!" +
                "\n\nGaligrü,\n" + myName, frame.getLocation()) {
            public void buttonClicked() {
                network.send("%SERVER FEEDBACK " + textArea.getText());
                dispose();
            }
        });

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (feature && e.getKeyChar() == '\\') {
                    board.toggleShowHints();
                    refreshFrameContent(-1);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.isAltDown()) feature = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    chess.userRedo();
                    refreshFrameContent(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    chess.userUndo();
                    refreshFrameContent(-1);
                }
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
                        if (network.isConnected()) {
                            if (chess.userRedoAll() > 0) {
                                refreshFrameContent(-1);
                            }
                        }
                        processMouseEvent(mouseEvent);
                        break;
                    }
                    case 2: {
                        System.out.println("BTN 2");
                        break;
                    }
                    case 3: {
                        System.out.println("BTN 3");
                        break;
                    }
                }
            }
        });
    }

    private boolean movePiece(Move move) {

        if (chess.userMove(move, userPlaysColor, userPlaysBothColors)) {
            if (network.isConnected()) {
                network.send(//     send move information           and time stamps
                        "%MOVE " + move.getInformation() + ' ' + chess.clock.whiteTime + ' ' + chess.clock.blackTime);
            }
            processScoring();
            return true;
        } else {
            return false;
        }
    }

    private void processMouseEvent(MouseEvent mouseEvent) {

        if (chess.getStatus() != Status.UNDECIDED) {
            showPopup(chess.getStatusNotice());
        } else {
            byte mousePosition = board.coordFromEvent(mouseEvent);

            if (moveString.equals("")) { // no square chosen yet
                if (chess.pieceAtSquare(mousePosition, chess.getTurnColor())) {

                    moveString += Util.parse(mousePosition) + " ";
                }
            } //you already chose a square:
            else if (!moveString.equals(Util.parse(mousePosition) + " ")) //avoids moves like A1->A1! :
                moveString += Util.parse(mousePosition) + " ";

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

                        Moves legalMoves;
                        if (chess.getTurnColor() == WHITE) {
                            legalMoves = chess.whitePieces.getPseudoLegalMoves();
                        } else {
                            legalMoves = chess.blackPieces.getPseudoLegalMoves();
                        }
                        if (legalMoves.contains(promotionMove)) {
                            moveStringSpecialMoves = moveString;
                            moveString = "";
                            showPromotionPopup();
                            return;
                        }
                        System.out.println("MOVE ILLEGAL (GUI)");
                    }
                }
                moveString = "";
            }
            refreshFrameContent(mousePosition);
        }
    }

    public void refreshFrameContent(int pos) {

        short lastMove = chess.history.getLastMoveCoordinates();
        board.refreshChessBoard(true, true,
                chess.getUserLegalMoves(userPlaysColor, userPlaysBothColors).getMovesFrom((byte) pos), lastMove);
        refreshLabels();
    }

    private void refreshLabels() {

        String whiteCaptPieces = chess.whitePieces.getCapturedPiecesString();
        String blackCaptPieces = chess.blackPieces.getCapturedPiecesString();
        whiteCaptPieces = replaceChessCharacters(whiteCaptPieces);
        blackCaptPieces = replaceChessCharacters(blackCaptPieces);
        labelCapturedWhitePieces.setText(whiteCaptPieces);
        labelCapturedBlackPieces.setText(blackCaptPieces);

        String space = "<br><br><br><br><br><br><br><br>";
        String textWest = "<html> &#160 &#x25a0" + space; //square and linefeed...
        String textEast = "<html> &#160 &#160 &#x25a1" + space;

        int score = Math.round((float) (chess.getScore() / 100));

        if (score < 0)
            textWest += " &#160 &#160 +" + Math.abs(score);
        else if (score > 0)
            textEast += " &#160 +" + Math.abs(score);

        textWest += space + " &#160 " + timeParser.format(new Date(chess.clock.blackTime));
        textEast += space + timeParser.format(new Date(chess.clock.whiteTime)) + " &#160 ";

        textWest += "</html>";
        textEast += "</html>";

        labelPlaceHolderWest.setText(textWest);
        labelPlaceHolderEast.setText(textEast);
    }

    public void processScoring() {

        if (chess.getStatus() != Status.UNDECIDED) {
            showPopup(chess.getStatusNotice());
            if (network.isConnected() && (userPlaysColor == WHITE)) { // only one player transmits scoring to server
                if (chess.getStatus().getResult() == Status.Scoring.HALF) {
                    network.send("%SERVER SCORING 0.5");
                } else if (userPlaysColor == WHITE) {
                    if (chess.getStatus().getResult() == Status.Scoring.WHITE_ONE)
                        network.send("%SERVER SCORING 1.0");
                    else
                        network.send("%SERVER SCORING 0.0");
                } else {
                    if (chess.getStatus().getResult() == Status.Scoring.BLACK_ONE)
                        network.send("%SERVER SCORING 1.0");
                    else
                        network.send("%SERVER SCORING 0.0");
                }
                System.out.println("TRANSMITTED SCORING");
            }
        }
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
                        if (chatInput.getText().startsWith("%")) network.send(chatInput.getText());
                        else network.send("%CHAT " + chatInput.getText());
                        addChatMessage(myName + ": " + chatInput.getText() + "\n");
                        chatInput.setText("");
                    }
                }
            });
        }

        public void addChatMessage(String message) {

            chatOutput.append(message);
            while (chatOutput.getLineCount() >= appearanceSettings.sizeFactor / 2) {
                String text = chatOutput.getText();
                text = text.substring(text.indexOf('\n') + 1);
                chatOutput.setText(text);
            }
        }

        public void toggleVisibility() {

            Dimension newDim = new Dimension(appearanceSettings.margin, appearanceSettings.margin);
            chatOutput.setPreferredSize(newDim);
            chatOutput.setBackground(appearanceSettings.colorScheme.WHITE_SQUARES_COLOR);

            newDim = new Dimension(appearanceSettings.margin, appearanceSettings.sizeFactor);
            chatInput.setPreferredSize(newDim);
            chatInput.setBackground(appearanceSettings.colorScheme.HIGHLIGHT_1_COLOR);

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
            super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        @Override
        public void buttonClicked() {

            chess.startFromFEN(input.getText());
            network.send("%FEN " + input.getText());
            refreshFrameContent(-1);
            dispose();
        }
    }

    class NetworkListener implements Subscriber {

        private boolean subscribed;

        public void networkQuery() {

            if (!subscribed && network.isConnected()) {
                changeTitle("-OFFLINE-", "-ONLINE-");
                network.getInstance().getReceiver().register(this);
                subscribed = true;
                String authentication = Password.toSHA256String("chessIsFun");
                network.send(authentication);
                network.send("%NAME " + myName);
                network.send(myPassword); //is a SHA256 Hex-String, so you can not draw conclusions from the real pw
            } else if (network.isConnected()) {
                react();
            }
        }

        @Override
        public void react() {

            while (network != null && network.getMessageQueue().size() > 0) {
                String message;
                try {

                    message = network.getMessageQueue().getFirst();
                    network.getMessageQueue().removeFirst();

                    String[] args = message.split(" ");
                    switch (args[0]) {
                        case "%MOVE":

                            if (chess.userRedoAll() > 0) {
                                refreshFrameContent(-1);
                            }

                            Move nextMove = new Move(Short.parseShort(args[1]));
                            switch (nextMove.getInformation()) {
                                case Move.START_GAME:
                                    chess.newGame();
                                    labelCapturedBlackPieces.setText("");
                                    labelCapturedWhitePieces.setText("");
                                    network.send("% Spiel gegen " + myName + " begonnen - " + VERSION);
                                    showPopup("Spiel beginnen");
                                    break;
                                case Move.OPPONENT_BLACK:
                                    userPlaysColor = BLACK;
                                    board.setWhitePlayerNorth();
                                    userPlaysBothColors = false;
                                    refreshFrameContent(-1);
                                    break;
                                case Move.OPPONENT_WHITE:
                                    userPlaysColor = WHITE;
                                    board.setWhitePlayerSouth();
                                    userPlaysBothColors = false;
                                    refreshFrameContent(-1);
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
                                case Move.RESIGN:
                                    chess.userMove(nextMove, userPlaysColor, true);
                                    showPopup(chess.getStatusNotice());
                                    break;
                                default:
                                    chess.userMove(nextMove, userPlaysColor, true);
                                    refreshFrameContent(-1);
                            }

                            try {
                                chess.clock.whiteTime = Long.parseLong(args[2]);
                                chess.clock.blackTime = Long.parseLong(args[3]);
                            } catch (Exception ex) {
                                System.out.println("PARSING ERROR\n" + ex.getMessage());
                            }

                            processScoring();

                            break;
                        case "%FEN":
                            chess.startFromFEN(message.replace("%FEN ", ""));
                            labelCapturedBlackPieces.setText("");
                            labelCapturedWhitePieces.setText("");
                            refreshFrameContent(-1);
                            break;
                        case "%ELO":
                            String elo = message.replace("%ELO ", "");
                            elo += "\nAktualisieren >Hier klicken<";
                            new DialogText(elo, frame.getLocation()) {
                                @Override
                                public void onMouseClick() {
                                    network.send("%SERVER ELO");
                                    dispose();
                                }
                            };
                            break;
                        case "%NOTE":
                            showPopup(message.replace("%NOTE ", ""));
                            break;
                        case "%INFO":
                            new DialogMessage(message.replace("%INFO ", ""));
                            break;
                        case "%VERSION?": // received version request
                            network.send("%VERSION " + VERSION);
                            break;
                        case "%ECHO?":
                            network.send("%ECHO");
                            break;
                        case "%VERSION": // received friend's version
                            chatOutput.append(message.replace("%VERSION ", "") + "\n");
                            break;
                        case "%NAME?": // received name request
                            network.send("%NAME " + myName);
                            break;
                        case "%NAME": // received friend's name
                            myFriendsName = message.replace("%NAME ", "");
                            System.out.println("YOU PLAY AGAINST " + myFriendsName);
                            break;
                        case "%CHAT": // display chat in chat window
                            chatDialog.addChatMessage(myFriendsName + ": " + message.replace("%CHAT ", "") + "\n");
                            break;
                        case "%BOARD":
                            System.out.println(message.replace("%BOARD ", "")); // todo...
                            break;
                        case "%": // show information in chat window
                            chatDialog.addChatMessage(message.replace("% ", "") + "\n");
                            System.out.println(message);
                            break;
                        default:
                            System.out.println("RECEIVED: " + message);

                    }
                } catch (NoSuchElementException ex) {
                    System.err.println("MESSAGE QUEUE IS EMPTY");
                }
            }
        }

        @Override
        public void unsubscribe() {
            subscribed = false;
        }
    }

    class WindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            if(network.isConnected())
                network.disconnect();
            rememberSetting("%NAME " + myName);
            rememberSetting("%PW " + myPassword);
            rememberSetting("%SIZE " + appearanceSettings.sizeFactor);
            rememberSetting("%STYLE " + appearanceSettings.colorScheme.getCurrentScheme());
            rememberSetting("%LOCATION " + frame.getLocation().getX() + "/" + frame.getLocation().getY());
            rememberSetting("%PIECES " + appearanceSettings.getFontNumer());
            storeSettingsInFile();
            e.getWindow().dispose();
            System.out.println("FINECHESS SAYS GOODBYE AND HAVE A NICE DAY");
            System.exit(0);
        }
    }
}