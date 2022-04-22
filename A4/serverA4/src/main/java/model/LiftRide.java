package model;

public class LiftRide {


    private int liftID;
    private int time;
    private int waitTime;



    public LiftRide(int liftID, int time, int waitTime) {
        this.liftID = liftID;
        this.time = time;
        this.waitTime = waitTime;
    }

    public int getLiftID() {
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
