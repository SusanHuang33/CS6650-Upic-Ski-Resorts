package client1;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    private AtomicInteger numSuccessReq;
    private AtomicInteger numFailReq;
    private long testStartTime;
    private long testEndTime;

    public Statistics(AtomicInteger numSuccessReq, AtomicInteger numFailReq, long testStartTime, long testEndTime) {
        this.numSuccessReq = numSuccessReq;
        this.numFailReq = numFailReq;
        this.testStartTime = testStartTime;
        this.testEndTime = testEndTime;
    }

    public AtomicInteger getNumSuccessReq() {
        return numSuccessReq;
    }

    public AtomicInteger getNumFailReq() {
        return numFailReq;
    }

    public long getTestStartTime() {
        return testStartTime;
    }

    public long getTestEndTime() {
        return testEndTime;
    }

    public void setTestStartTime(long testStartTime) {
        this.testStartTime = testStartTime;
    }

    public void setTestEndTime(long testEndTime) {
        this.testEndTime = testEndTime;
    }
}
