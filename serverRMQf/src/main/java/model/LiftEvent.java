package model;

public class LiftEvent {

    private int liftId;
    private int time;
    private int waitTime;

    public LiftEvent(int liftId, int time, int waitTime) {
        this.liftId = liftId;
        this.time = time;
        this.waitTime = waitTime;
    }

    public int getLiftId() {
        return liftId;
    }

    public int getTime() {
        return time;
    }

    public int getWaitTime() {
        return waitTime;
    }

    @Override
    public String toString() {
        return "model.LiftEvent{" +
                "liftId=" + liftId +
                ", time=" + time +
                ", waitTime=" + waitTime +
                '}';
    }
}
