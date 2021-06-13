package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

interface ChatSubscriber {

    void sendChat(String chat);

    AppearanceSettings getAppearanceSettings();

    String getName();
}

abstract class ChatWindow extends JFrame {

    final ChatSubscriber chatSub;
    final JTextField chatInput;
    final ListModel chatList;
    final JList<String> chatScreen;
    final JScrollPane scrollPane;


    public ChatWindow(ChatSubscriber chatSubscriber) {

        setTitle("Chat-Fenster");
        chatSub = chatSubscriber;
        chatInput = new JTextField();
        chatList = new ListModel();
        chatScreen = new JList<>(chatList);
        scrollPane = new JScrollPane(chatScreen);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.NORTH);
        add(chatInput, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setUpFrame();
        setResizable(false);
        setVisible(true);

        chatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
            }
        });

        chatScreen.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                chatScreen.updateUI();
            }
        });
    }

    abstract void sendMsg();

    public void addChatMessage(String message) {
        chatList.add(message);
        chatScreen.validate();
        chatScreen.updateUI();

        scrollPane.validate();
        scrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }

    public void toggleVisibility() {

        setUpFrame();
        setVisible(!isVisible());
    }

    private void setUpFrame() {

        AppearanceSettings style = chatSub.getAppearanceSettings();

        scrollPane.setPreferredSize(new Dimension(style.margin, style.margin));
        chatScreen.setBackground(style.colorScheme.WHITE_SQUARES_COLOR);

        chatInput.setPreferredSize(new Dimension(style.margin, style.sizeFactor));
        chatInput.setBackground(style.colorScheme.HIGHLIGHT_1_COLOR);

        pack();
    }
}

class ListModel extends AbstractListModel<String> {

    private final ArrayList<String> l = new ArrayList<>();

    public String getElementAt(int index) {
        return l.get(index);
    }

    public int getSize() {
        return l.size();
    }

    public void add(String e) {
        l.add(e);
    }
}