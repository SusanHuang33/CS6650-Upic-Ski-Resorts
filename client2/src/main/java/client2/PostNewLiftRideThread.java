package client2;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.concurrent.CountDownLatch;

public class PostNewLiftRideThread implements Runnable {

    private final CountDownLatch latch;

    private final int startSkierId;
    private final int endSkierId;
    private final int numRequests;
    private final int numLifts;
    private final int startTime;
    private final int endTime;
    private final Statistics statistics;
    private SecureRandom random;
    private int resortId;
    private String seasonId;
    private String dayId;

    private static final int RETRIES = 5;   // max number of retries
    private static final SkiersApi apiInstance = new SkiersApi();

    public PostNewLiftRideThread(CountDownLatch latch, int startSkier, int endSkier,
                                 int numRequests, int numLifts, int startTime, int endTime,
                                 String serverURL, Statistics statistics, int resortId, String seasonId, String dayId) {
        this.latch = latch;
        this.startSkierId = startSkier;
        this.endSkierId = endSkier;
        this.numRequests = numRequests;
        this.numLifts = numLifts;
        this.startTime = startTime;
        this.endTime = endTime;
        apiInstance.getApiClient().setBasePath(serverURL);
        this.statistics = statistics;
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
        this.random = new SecureRandom();
    }

    @Override
    public void run() {
        for (int i = 0; i < numRequests; i++) {
            Integer waitTime = random.nextInt(10 + 1);
            Integer liftNum = 1 + random.nextInt(numLifts);
            Integer timeValue = startTime + random.nextInt(endTime - startTime + 1);
            Integer skierId = startSkierId + random.nextInt(endSkierId - startSkierId + 1);

            LiftRide body = new LiftRide();
            body.setTime(timeValue);
            body.setLiftID(liftNum);
            body.setWaitTime(waitTime);

            int triedTimes = 0;
            while (triedTimes < RETRIES + 1) {
                try {
                    if (triedTimes > 0) {
                        System.out.format("Start %dth retrying...\n", triedTimes);
                    }
                    long requestStartTime = System.currentTimeMillis();
                    ApiResponse<Void> response = apiInstance.writeNewLiftRideWithHttpInfo(body, resortId, seasonId, dayId, skierId);
                    statistics.getNumSuccessReq().getAndIncrement();
                    long requestEndTime = System.currentTimeMillis();
                    long latency = requestEndTime - requestStartTime;
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
                            statistics.getRecordOutputFilePath(),
                            StandardCharsets.UTF_8,
                            StandardOpenOption.APPEND)) {
                        bufferedWriter.write(
                                requestStartTime + ",POST," + latency + "," + response.getStatusCode() + "\n");
                    }
                    break;
                } catch (ApiException | IOException e) {
                    triedTimes += 1;
                    e.printStackTrace();
                }
            }

            if (triedTimes == RETRIES + 1) {
                statistics.getNumFailReq().getAndIncrement();
            }
        }

        try {
            this.latch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
