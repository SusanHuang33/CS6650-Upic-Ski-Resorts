package client1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

 public class SingleThreadPostTest {
    public static void main(String[] args) throws InterruptedException {

        Statistics stats = new Statistics(
                new AtomicInteger(0),
                new AtomicInteger(0),
                0, 0);
        stats.setTestStartTime(System.currentTimeMillis());

        CountDownLatch latch = new CountDownLatch((int) (1));

        System.out.println("Starting single thread...");
        for (int i = 0; i < 1; i++) {
            final int startSkierID = i * 10 + 1;
            final int endSkierID = (i+1) * 10;
            Thread thread = new Thread(new PostNewLiftRideThread(latch, startSkierID, endSkierID,
                    10000, 5, 1, 90, "http://ec2-35-85-145-0.us-west-2.compute.amazonaws.com:8080/A1_war", stats,
                    56, "2019", "1"));
            thread.start();
        }

        latch.await();
        stats.setTestEndTime(System.currentTimeMillis());
        long wallTime = stats.getTestEndTime() - stats.getTestStartTime();
        System.out.println("wall time in millisecond: " + wallTime);
        System.out.println("latency(milliseconds per requests): " + wallTime / 10000.0);

        System.out.println("wall time in second: " + wallTime/Math.pow(10,3));
        System.out.println("latency (seconds per requests): " + (wallTime/Math.pow(10,3)) / 10000.0);

    }
}
