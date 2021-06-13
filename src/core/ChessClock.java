package core;

import java.util.Timer;
import java.util.TimerTask;

public class ChessClock {

    private final Chess chess;
    public long blackTime;
    public long whiteTime;
    Timer clock;
    private boolean isTicking;
    private long lastStamp;
    private ClockSubscriber subscriber;

    public ChessClock(Chess chess) {
        this.chess = chess;
    }

    public void initialize(int timePreset) {
        if (clock != null) clock.cancel();
        isTicking = true;
        lastStamp = System.currentTimeMillis();
        whiteTime = (long) timePreset * 60 * 1000;
        blackTime = (long) timePreset * 60 * 1000;
        clock = new Timer();
        clock.scheduleAtFixedRate(new ClockTask(), 0, 1000);
    }

    public void callbackQuery(ClockSubscriber sub) {
        subscriber = sub;
    }

    public void whiteTimeExceeded() {
        isTicking = false;
        chess.gameStatus.setStatus(Status.WHITE_TIME_EXCEEDED);
        if (subscriber != null)
            subscriber.processScoring();
    }

    public void blackTimeExceeded() {
        isTicking = false;
        chess.gameStatus.setStatus(Status.BLACK_TIME_EXCEEDED);
        if (subscriber != null)
            subscriber.processScoring();
    }

    void update() {
        if (!isTicking) return;
        long now = System.currentTimeMillis();
        long difference = now - lastStamp;
        lastStamp = now;
        if (chess.whiteToMove == chess.isUndoneListEven()) {
            whiteTime -= difference;
        } else {
            blackTime -= difference;
        }
        if (whiteTime <= 1000)
            whiteTimeExceeded();
        if (blackTime <= 1000)
            blackTimeExceeded();
    }

    class ClockTask extends TimerTask {
        @Override

        public void run() {
            update();
        }
    }
}
