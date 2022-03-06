package model;

public class LiftRide {

    private int liftID;
    private int time;
    private int waitTime;

    public LiftRide(int liftId, int time, int waitTime) {
        this.liftID = liftId;
        this.time = time;
        this.waitTime = waitTime;
    }

    public int getLiftId() {
        return liftID;
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
                "liftId=" + liftID +
                ", time=" + time +
                ", waitTime=" + waitTime +
                '}';
    }
}
