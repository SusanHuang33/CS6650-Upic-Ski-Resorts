package dal;

public class LiftRide {
    private int resortId;
    private int seasonsId;
    private int dayId;
    private int time;
    private int skierId;
    private int liftId;
    private int waitTime;

    public LiftRide(int skierId, int resortId, int seasonsId, int dayId, int time, int liftId, int waitTime) {
        this.resortId = resortId;
        this.seasonsId = seasonsId;
        this.dayId = dayId;
        this.time = time;
        this.skierId = skierId;
        this.liftId = liftId;
        this.waitTime = waitTime;
    }

    public int getResortId() {
        return resortId;
    }

    public int getSeasonsId() {
        return seasonsId;
    }

    public int getDayId() {
        return dayId;
    }

    public int getTime() {
        return time;
    }

    public int getSkierId() {
        return skierId;
    }

    public int getLiftId() {
        return liftId;
    }

    public int getWaitTime() {
        return waitTime;
    }

    @Override
    public String toString() {
        return "LiftRide{" +
                "skierId=" + skierId +
                ", resortId=" + resortId +
                ", seasonsId=" + seasonsId +
                ", dayId=" + dayId +
                ", liftId=" + liftId +
                ", time=" + time +
                ", waitTime=" + waitTime +
                '}';
    }
}

