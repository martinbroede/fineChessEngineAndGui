package gui;

import core.*;
import gui.dialogs.DialogFeedback;
import gui.dialogs.DialogTextArea;
import misc.Password;
import network.Subscriber;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import static misc.Properties.resourceBundle;
import static misc.StaticSetting.rememberSetting;
import static misc.StaticSetting.storeSettings;

public class Gui extends MainWindow implements ClockSubscriber {

    private static final boolean BLACK = Constants.BLACK; //for readability reasons
    private static final boolean WHITE = Constants.WHITE; //for readability reasons
    private static final String INIT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static final String HTML_SPACE_1 = "<html> &#160 &#160 &#x25a1";
    private static final String HTML_SPACE_2 = "<html> &#160 &#x25a0";
    private static final String SERVER_ADDRESS = "chessnet.dynv6.net";
    private static final String SERVER_PORT = "55555";
    private static final String CASTLING_O_O_ILLEGAL = "CASTLING o-o ILLEGAL";
    private static final String CASTLING_O_O_O_ILLEGAL = "CASTLING o-o-o ILLEGAL";
    private static final String SERVER_SCORING_1_0 = "%SERVER SCORING 1.0";
    private static final String SERVER_SCORING_0_0 = "%SERVER SCORING 0.0";
    private static final String SERVER_ELO = "%SERVER ELO";

    private final SimpleDateFormat timeParser = new SimpleDateFormat("mm:ss");

    private final ChessModel chess;
    private String moveString;
    private String moveStringSpecialMoves; //for promotion, castling, enPassant
    private boolean userPlaysColor = WHITE;
    private boolean userPlaysBothColors = true;
    private boolean feature = false;

    private byte mousePosition = -1;

    public Gui(ChessModel chessModel) {

        super(chessModel);

        this.chess = chessModel;
        this.chess.getClock().callbackQuery(this);
        moveString = "";
        moveStringSpecialMoves = "";
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
        }, 0, 500); // update chess clock

        if ((myName.equals("") || myPassword.equals("")))
            getNameAndPassword();

        connectToServer();

        /* ######################################### add listeners ################################################## */

        frame.addWindowListener(new WindowListener());

        itemRename.addActionListener(e -> {
            getNameAndPassword();
            connectToServer();
        });

        itemNewGame.addActionListener(e -> {
            System.out.println("NEW GAME");
            this.chess.newGame();
            labelCapturedBlackPieces.setText("");
            labelCapturedWhitePieces.setText("");
            userPlaysBothColors = true;
            refreshFrameContent();
            moveString = "";
            showPopup(resourceBundle.getString(START_GAME));
        });

        itemStore.addActionListener(e -> {
            {
            }
        });

        itemBegin.addActionListener(e -> System.out.println("NOT YET IMPLEMENTED"));

        itemCastlingKingSide.addActionListener(e -> {
            Move nextMove;
            if (this.chess.getTurnColor() == WHITE) {
                nextMove = new Move((byte) 4, (byte) 6, Move.KING_SIDE_CASTLING);

            } else { //BLACK
                nextMove = new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING);
            }
            if (!movePiece(nextMove))
                System.out.println(CASTLING_O_O_ILLEGAL);
            refreshFrameContent(); // -1 : don't want to paint legal moves.
        });

        itemCastlingQueenSide.addActionListener(e -> {
            Move nextMove;
            if (this.chess.getTurnColor() == WHITE) {
                nextMove = new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING);

            } else { //BLACK
                nextMove = new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING);
            }
            if (!movePiece(nextMove))
                System.out.println(CASTLING_O_O_O_ILLEGAL);
            refreshFrameContent(); // -1 : don't want to paint legal moves.
        });

        itemPromotionQueen.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "Q"));
            refreshFrameContent();
        });

        itemPromotionRook.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "R"));
            refreshFrameContent();
        });

        itemPromotionKnight.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "N"));
            refreshFrameContent();
        });

        itemPromotionBishop.addActionListener(e -> {
            movePiece(new Move(moveStringSpecialMoves + "B"));
            refreshFrameContent();
        });

        itemUndo.addActionListener(e -> {
            this.chess.userUndo();
            refreshFrameContent();
        });

        itemRedo.addActionListener(e -> {
            this.chess.userRedo();
            refreshFrameContent();
        });

        itemChangePieceStyle.addActionListener(e -> {
            board.fontRoulette();
            setStyleSettings();
            refreshFrameContent();
        });

        itemShowChat.addActionListener(e -> chatWindow.toggleVisibility());

        itemStartClient.addActionListener(e -> network.showClientIpDialog(frame.getLocation()));
        itemStartServer.addActionListener(e -> network.showServerIpDialog(frame.getLocation()));

        itemNewNetworkGame.addActionListener(e -> {
            this.chess.newGame();
            network.send("%MOVE " + Move.START_GAME);
            showPopup("Spiel beginnen");
        });

        itemNetworkDisconnect.addActionListener(e -> network.disconnect());

        itemRotateBoard.addActionListener(e -> {
            board.toggleBoardOrientation();
            refreshFrameContent();
        });

        itemFromFEN.addActionListener(e -> {
            JTextField fen = new JTextField(INIT_FEN);
            JPanel panel = new JPanel();
            panel.add(fen);
            int result = JOptionPane.showConfirmDialog(frame, panel, resourceBundle.getString("start.from.fen"), JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                this.chess.startFromFEN(fen.getText());
            }
        });

        itemAssignOpponentBlack.addActionListener(e -> {
            network.send("%MOVE " + Move.OPPONENT_BLACK);
            userPlaysColor = WHITE;
            userPlaysBothColors = false;
            board.setWhitePlayerSouth();
            refreshFrameContent();
        });

        itemAssignOpponentWhite.addActionListener(e -> {
            network.send("%MOVE " + Move.OPPONENT_WHITE);
            userPlaysColor = BLACK;
            userPlaysBothColors = false;
            board.setWhitePlayerNorth();
            refreshFrameContent();
        });

        itemResign.addActionListener(e -> {
            if (this.chess.getTurnColor() == userPlaysColor && this.chess.getStatus() == Status.UNDECIDED) {
                this.chess.userMove(new Move(Move.RESIGN), userPlaysColor, true);
                network.send("%MOVE " + Move.RESIGN);
                processScoring();
                showPopup(this.chess.getStatusNotice());
            } else {
                showPopup("Gib auf, wenn du am Zug bist.");
            }
        });

        itemOfferDraw.addActionListener(e -> {
            if (this.chess.getTurnColor() == userPlaysColor) {
                network.send("%MOVE " + Move.OFFER_DRAW);
                this.chess.userMove(new Move(Move.OFFER_DRAW), userPlaysColor, true);
                showPopup("Remis angeboten...");
            } else {
                showPopup("Mache ein Angebot, wenn du am Zug bist.");
            }
        });

        itemAccept.addActionListener(e -> {
            this.chess.userMove(new Move(Move.ACCEPT_DRAW), userPlaysColor, true);
            network.send("%MOVE " + Move.ACCEPT_DRAW);
            processScoring();
            showPopup(this.chess.getStatusNotice());
        });

        itemDecline.addActionListener(e -> {
            network.send("%MOVE " + Move.DECLINE_DRAW);
            this.chess.userMove(new Move(Move.DECLINE_DRAW), userPlaysColor, true);
            showPopup("Angebot abgelehnt.");
        });

        itemConnectWithPlayer.addActionListener(e -> network.send("%SERVER LINK"));

        itemChallengePlayer.addActionListener(e -> {
            if (network.isConnected()) {
                String name = JOptionPane.showInputDialog(frame, resourceBundle.getString("name"));
                if (name != null)
                    network.send("%SERVER LINKTO NAME".replace("NAME", name));
            }
        });

        itemRatingQuery.addActionListener(e -> network.send(SERVER_ELO));

        itemConnectToServer.addActionListener(e -> {
            network.disconnect();
            network.createClient(String.format("%s/%s", SERVER_ADDRESS, SERVER_PORT));
        });

        itemSendFeedback.addActionListener(e -> new DialogFeedback("Lieber Entwickler von SoftPawn!\n\n" +
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
                    refreshFrameContent();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.isAltDown()) feature = true;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    chessModel.userRedo();
                    refreshFrameContent();
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    chessModel.userUndo();
                    refreshFrameContent();
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
                            if (chessModel.userRedoAll() > 0) {
                                refreshFrameContent();
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

    private void connectToServer() {
        network.createClient(String.format("%s/%s", SERVER_ADDRESS, SERVER_PORT));
    }

    private boolean movePiece(Move move) {

        if (chess.userMove(move, userPlaysColor, userPlaysBothColors)) {
            if (network.isConnected()) {
                network.send(//     send move information           and time stamps
                        "%MOVE " + move.getInformation() + ' ' + chess.getClock().whiteTime + ' ' + chess.getClock().blackTime);
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
            mousePosition = board.coordFromEvent(mouseEvent);

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
                            System.out.println(CASTLING_O_O_ILLEGAL);
                    } else if (nextMove.isFrom(Util.parse("E1")) && nextMove.getTo() == Util.parse("C1")) {
                        if (!movePiece(new Move((byte) 4, (byte) 2, Move.QUEEN_SIDE_CASTLING)))
                            System.out.println(CASTLING_O_O_O_ILLEGAL);
                    } else if (nextMove.isFrom(Util.parse("E8")) && nextMove.getTo() == Util.parse("G8")) {
                        if (!movePiece(new Move((byte) 60, (byte) 62, Move.KING_SIDE_CASTLING)))
                            System.out.println(CASTLING_O_O_ILLEGAL);
                    } else if (nextMove.isFrom(Util.parse("E8")) && nextMove.getTo() == Util.parse("C8")) {
                        if (!movePiece(new Move((byte) 60, (byte) 58, Move.QUEEN_SIDE_CASTLING)))
                            System.out.println(CASTLING_O_O_O_ILLEGAL);
                    }

                    /* move illegal? try en passant moves */
                    else if (nextMove.isFromRank('4') || nextMove.isFromRank('5')) {
                        Move enPassMove = new Move((short) (nextMove.getInformation() | Move.EN_PASSANT));
                        if (!movePiece(enPassMove)) System.out.println("EN PASSANT CAPTURE ILLEGAL");
                    }

                    /* move illegal? try promotion moves */
                    else {
                        byte from = nextMove.getFrom();
                        byte to = nextMove.getTo();
                        Move promotionMove = new Move(from, to, Move.PROMOTION_QUEEN);

                        Moves legalMoves;
                        if (chess.getTurnColor() == WHITE) {
                            legalMoves = chess.getWhitePieces().getPseudoLegalMoves();
                        } else {
                            legalMoves = chess.getBlackPieces().getPseudoLegalMoves(); // todo simplify to chess.getBlackMoves();
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
            refreshFrameContent();
        }
    }

    public void refreshFrameContent() {

        short lastMove = chess.getHistory().getLastMoveCoordinates();
        board.refreshChessBoard(true, true,
                chess.getUserLegalMoves(userPlaysColor, userPlaysBothColors).getMovesFrom(mousePosition), lastMove);
        refreshLabels();
    }

    public void refreshLabels() {

        String whiteCaptPieces = chess.getWhitePieces().getCapturedPiecesString();
        String blackCaptPieces = chess.getBlackPieces().getCapturedPiecesString();
        whiteCaptPieces = replaceChessCharacters(whiteCaptPieces);
        blackCaptPieces = replaceChessCharacters(blackCaptPieces);
        labelCapturedWhitePieces.setText(whiteCaptPieces);
        labelCapturedBlackPieces.setText(blackCaptPieces);

        String space = "<br><br><br><br><br><br><br><br>";
        String textWest = HTML_SPACE_2 + space; //square and linefeed...
        String textEast = HTML_SPACE_1 + space;

        int score = Math.round((float) (chess.getScore() / 100));

        if (score < 0)
            textWest += " &#160 &#160 +" + Math.abs(score);
        else if (score > 0)
            textEast += " &#160 +" + Math.abs(score);

        textWest += space + " &#160 " + timeParser.format(new Date(chess.getClock().blackTime));
        textEast += space + timeParser.format(new Date(chess.getClock().whiteTime)) + " &#160 ";

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
                        network.send(SERVER_SCORING_1_0);
                    else
                        network.send(SERVER_SCORING_0_0);
                } else {
                    if (chess.getStatus().getResult() == Status.Scoring.BLACK_ONE)
                        network.send(SERVER_SCORING_1_0);
                    else
                        network.send(SERVER_SCORING_0_0);
                }
                System.out.println("TRANSMITTED SCORING");
            }
        }
    }

    class NetworkListener implements Subscriber {

        public static final String VERSION_ = "%VERSION ";
        private boolean subscribed;

        public void networkQuery() {

            if (!subscribed && network.isConnected()) {
                changeTitle(OFFLINE, ONLINE);
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
                                refreshFrameContent();
                            }

                            Move nextMove = new Move(Short.parseShort(args[1]));
                            switch (nextMove.getInformation()) {
                                case Move.START_GAME:
                                    chess.newGame();
                                    labelCapturedBlackPieces.setText("");
                                    labelCapturedWhitePieces.setText("");
                                    network.send(String.format(resourceBundle.getString("game.started"), myName, VERSION));
                                    showPopup(resourceBundle.getString(START_GAME));
                                    break;
                                case Move.OPPONENT_BLACK:
                                    userPlaysColor = BLACK;
                                    board.setWhitePlayerNorth();
                                    userPlaysBothColors = false;
                                    refreshFrameContent();
                                    break;
                                case Move.OPPONENT_WHITE:
                                    userPlaysColor = WHITE;
                                    board.setWhitePlayerSouth();
                                    userPlaysBothColors = false;
                                    refreshFrameContent();
                                    break;
                                case Move.OFFER_DRAW:
                                    chess.userMove(nextMove, userPlaysColor, true);
                                    showDrawPopup();
                                    break;
                                case Move.DECLINE_DRAW:
                                    chess.userMove(nextMove, userPlaysColor, true);
                                    showPopup(resourceBundle.getString("decline.offer"));
                                    break;
                                case Move.ACCEPT_DRAW:
                                case Move.RESIGN:
                                    chess.userMove(nextMove, userPlaysColor, true);
                                    showPopup(chess.getStatusNotice());
                                    break;
                                default:
                                    chess.userMove(nextMove, userPlaysColor, true);
                                    refreshFrameContent();
                            }

                            try {
                                chess.getClock().whiteTime = Long.parseLong(args[2]);
                                chess.getClock().blackTime = Long.parseLong(args[3]);
                            } catch (Exception ex) {
                                System.out.println("NO TIMESTAMP SENT");
                            }

                            processScoring();

                            break;
                        case "%FEN":
                            chess.startFromFEN(message.replace("%FEN ", ""));
                            labelCapturedBlackPieces.setText("");
                            labelCapturedWhitePieces.setText("");
                            refreshFrameContent();
                            break;
                        case "%ELO":
                            String elo = message.replace("%ELO ", "");
                            elo += resourceBundle.getString("click.to.update");
                            new DialogTextArea(elo, frame.getLocation()) {
                                @Override
                                public void onMouseClick() {
                                    network.send(SERVER_ELO);
                                    dispose();
                                }
                            };
                            break;
                        case "%NOTE":
                            showPopup(message.replace("%NOTE ", ""));
                            break;
                        case "%INFO":
                            showMessageDialog(message.replace("%INFO ", ""));
                            break;
                        case "%VERSION?": // received version request
                            network.send(String.format("%%VERSION %s", VERSION));
                            break;
                        case "%ECHO?":
                            network.send("%ECHO");
                            break;
                        case "%VERSION": // received friend's version
                            chatWindow.addChatMessage(message.replace(VERSION_, "") + "\n");
                            break;
                        case "%NAME?": // received name request
                            network.send("%NAME " + myName);
                            break;
                        case "%NAME": // received friend's name
                            myFriendsName = message.replace("%NAME ", "");
                            System.out.println("YOU PLAY AGAINST " + myFriendsName);
                            break;
                        case "%CHAT": // display chat in chat window
                            chatWindow.addChatMessage(String.format(
                                    "%s: %s\n", myFriendsName, message.replace("%CHAT ", "")));
                            break;
                        case "%BOARD":
                            System.out.println(message.replace("%BOARD ", "")); // todo...
                            break;
                        case "%": // show information in chat window
                            chatWindow.addChatMessage(message.replace("% ", "") + "\n");
                            System.out.println(message);
                            break;
                        default:
                            System.out.println("RECEIVED: " + message);

                    }
                } catch (NoSuchElementException ex) {
                    System.out.println("MESSAGE QUEUE IS EMPTY");
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
            try {
                if (network.isConnected())
                    network.disconnect();
                rememberSetting("%NAME " + myName);
                rememberSetting("%PW " + myPassword);
                rememberSetting("%SIZE " + appearanceSettings.sizeFactor);
                rememberSetting("%STYLE " + appearanceSettings.colorScheme.getCurrentScheme());
                rememberSetting("%LOCATION " + frame.getLocation().getX() + "/" + frame.getLocation().getY());
                rememberSetting("%PIECES " + appearanceSettings.getFontNumer());
                storeSettings();
                e.getWindow().dispose();
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
            System.out.println("FINECHESS SAYS GOODBYE AND HAVE A NICE DAY");
            System.exit(0);
        }
    }
}