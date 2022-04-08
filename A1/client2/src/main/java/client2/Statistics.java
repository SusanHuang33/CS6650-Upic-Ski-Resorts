package client2;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    private AtomicInteger numSuccessReq;
    private AtomicInteger numFailReq;
    private long testStartTime;
    private long testEndTime;

    private Path recordOutputFilePath;

    private double meanResponseTime;
    private double medianResponseTime;
    private int throughput;
    private long p99ResponseTime;
    private long maxResponseTime;
    private long minResponseTime;

    public Statistics(AtomicInteger numSuccessReq, AtomicInteger numFailReq, long testStartTime, long testEndTime, Path filePath) {
        this.numSuccessReq = numSuccessReq;
        this.numFailReq = numFailReq;
        this.testStartTime = testStartTime;
        this.testEndTime = testEndTime;
        this.recordOutputFilePath = filePath;
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

    public Path getRecordOutputFilePath() {
        return recordOutputFilePath;
    }

    public double getMeanResponseTime() {
        return meanResponseTime;
    }

    public double getMedianResponseTime() {
        return medianResponseTime;
    }

    public int getThroughput() {
        return throughput;
    }

    public long getP99ResponseTime() {
        return p99ResponseTime;
    }

    public long getMaxResponseTime() {
        return maxResponseTime;
    }

    public long getMinResponseTime() {
        return minResponseTime;
    }

    public void setRecordOutputFilePath(Path recordOutputFilePath) {
        this.recordOutputFilePath = recordOutputFilePath;
    }

    public void setMeanResponseTime(double meanResponseTime) {
        this.meanResponseTime = meanResponseTime;
    }

    public void setMedianResponseTime(double medianResponseTime) {
        this.medianResponseTime = medianResponseTime;
    }

    public void setThroughput(int throughput) {
        this.throughput = throughput;
    }

    public void setP99ResponseTime(long p99ResponseTime) {
        this.p99ResponseTime = p99ResponseTime;
    }

    public void setMaxResponseTime(long maxResponseTime) {
        this.maxResponseTime = maxResponseTime;
    }

    public void setMinResponseTime(long minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
}
