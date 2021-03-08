package gui;

import core.Chess;

public class Main {
    public static void main(String[] args) {
        Chess chess = new Chess();
        Gui gui = new Gui(chess);
    }
}
