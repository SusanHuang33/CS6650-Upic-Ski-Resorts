package client1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SkiersClient {
  private InputParams inputParams;

  public SkiersClient(InputParams inputParams) {
    this.inputParams = inputParams;
  }

  public void PostNewLiftRide() throws InterruptedException {
    Integer totalRequests = 0;
    Statistics stats = new Statistics(
            new AtomicInteger(0),
            new AtomicInteger(0),
            0, 0);

    // Dummy data for assignment 1
    int resortId = 56;
    String seasonId = "2022";
    String dayId = "1";


    // Phase 1: startup
    int numThreadsThisPhase = inputParams.getNumThreads() / 4;
    int numSkiersPerThread = inputParams.getNumSkiers() / numThreadsThisPhase;
    int startTime = 1;
    int endTime = 90;
    int numRequestsPerThread = (int) (inputParams.getNumRuns() * 0.2) * numSkiersPerThread;
    totalRequests += numRequestsPerThread * numThreadsThisPhase;

    CountDownLatch latch = new CountDownLatch((int) (numThreadsThisPhase * 0.2));

    stats.setTestStartTime(System.currentTimeMillis());
    for (int i = 0; i < numThreadsThisPhase; i++) {
      final int startSkierID = i * numSkiersPerThread + 1;
      final int endSkierID = (i+1) * numSkiersPerThread;
      Thread thread = new Thread(new PostNewLiftRideThread(latch, startSkierID, endSkierID,
              numRequestsPerThread, inputParams.getNumLifts(), startTime, endTime, inputParams.getServerUrl(), stats,
              resortId, seasonId, dayId));
      thread.start();
    }

    latch.await();

    // Phase 2: peak
    numThreadsThisPhase = inputParams.getNumThreads();
    numSkiersPerThread = inputParams.getNumSkiers() / numThreadsThisPhase;
    startTime = 91;
    endTime = 360;
    numRequestsPerThread = (int) (inputParams.getNumRuns() * 0.6) * numSkiersPerThread;
    totalRequests += numRequestsPerThread * numThreadsThisPhase;

    latch = new CountDownLatch((int) (numThreadsThisPhase * 0.2));

    for (int i = 0; i < numThreadsThisPhase; i++) {
      final int startSkierID = i * numSkiersPerThread + 1;
      final int endSkierID = (i+1) * numSkiersPerThread;
      Thread thread = new Thread(new PostNewLiftRideThread(latch, startSkierID, endSkierID,
              numRequestsPerThread, inputParams.getNumLifts(), startTime, endTime, inputParams.getServerUrl(), stats,
              resortId, seasonId, dayId));
      thread.start();
    }

    latch.await();

    // Phase 3: cool down
    numThreadsThisPhase = inputParams.getNumThreads() / 10;
    numSkiersPerThread = inputParams.getNumSkiers() / numThreadsThisPhase;
    startTime = 361;
    endTime = 420;
    numRequestsPerThread = (int) (inputParams.getNumRuns() * 0.1) * numSkiersPerThread;
    totalRequests += numRequestsPerThread * numThreadsThisPhase;

    latch = new CountDownLatch(numThreadsThisPhase);

    for (int i = 0; i < numThreadsThisPhase; i++) {
      final int startSkierID = i * numSkiersPerThread + 1;
      final int endSkierID = (i+1) * numSkiersPerThread;
      Thread thread = new Thread(new PostNewLiftRideThread(latch, startSkierID, endSkierID,
              numRequestsPerThread, inputParams.getNumLifts(), startTime, endTime, inputParams.getServerUrl(), stats,
              resortId, seasonId, dayId));
      thread.start();
    }

    latch.await();

    stats.setTestEndTime(System.currentTimeMillis());

    long wallTime = stats.getTestEndTime() - stats.getTestStartTime();
    long wallTimeInSec =  wallTime / (long) (Math.pow(10, 3));
    long throughPut = (long) (1000.0 * totalRequests / wallTime);

    System.out.println();
    System.out.println(inputParams);
    System.out.println();
    System.out.println("Client Part1:");
    System.out.println();
    System.out.println("total number of successful requests sent:" + stats.getNumSuccessReq());
    System.out.println("total number of unsuccessful requests:" + stats.getNumFailReq());
    System.out.println("total run time in milliseconds:" + wallTime);
    System.out.println("total run time in seconds:" + wallTimeInSec);
    System.out.println("total requests:" + totalRequests);
    System.out.println("total throughput in requests/second:" + throughPut);
  }
}

