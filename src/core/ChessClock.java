package core;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class ChessClock {

    Timer clock;
    private boolean running;
    public long blackTime;
    public long whiteTime;
    private long lastStamp;
    private Chess chess;

    public ChessClock(Chess chess) {
        this.chess = chess;
    }

    public void initialize() {
        if (clock != null) clock.cancel();
        running = true;
        lastStamp = System.currentTimeMillis();
        whiteTime = 0;
        blackTime = 0;
        clock = new Timer();
        clock.scheduleAtFixedRate(new ClockTask(), 1000, 1000);
    }

    public void whiteTimeExceeded() {
        print();
        System.out.println("black wins");
        initialize();
    }

    public void blackTimeExceeded() {
        print();
        System.out.println("white wins");
        clock.cancel();
    }

    void print() {
        long b = blackTime;
        long w = whiteTime;
        System.out.printf("black: %d, white: %d\n", b, w);
    }

    void update() {
        long now = System.currentTimeMillis();
        long difference = now - lastStamp;
        lastStamp = now;
        if (chess.whiteToMove) {
            whiteTime += difference;
        } else {
            blackTime += difference;
        }
    }

    class ClockTask extends TimerTask {
        @Override

        public void run() {
            if(!running) return;
            update();
        }
    }
}