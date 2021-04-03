package core;

public class CurrentStatus {

    private Status status;
    private boolean drawOffered;
    public CurrentStatus() {
        reset();
    }

    public boolean isDrawOffered() {
        return drawOffered;
    }

    public void setDrawOffered(boolean drawOffered) {
        this.drawOffered = drawOffered;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void reset() {
        status = Status.UNDECIDED;
    }

    public String getStatusNotice() {
        return status.getNotice();
    }
}
