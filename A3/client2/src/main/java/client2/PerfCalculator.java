package client2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PerfCalculator {
    public static void calculate(Statistics stats) {
        int totalLatency = 0;
        List<Long> latencies = stats.getLatencies();
        for (Long latency: latencies) {
            totalLatency += latency;
        }
        Collections.sort(latencies);
        stats.setMeanResponseTime(totalLatency / (double) latencies.size());
        stats.setMaxResponseTime(latencies.get(latencies.size() - 1));
        stats.setMinResponseTime(latencies.get(0));
        stats.setP99ResponseTime(latencies.get((int) (0.99 * (latencies.size() - 1))));
        stats.setMedianResponseTime(
                latencies.size() % 2 != 0 ?
                    (double)latencies.get(latencies.size() / 2)
                    :
                    (latencies.get((latencies.size() - 1) / 2) + latencies.get(latencies.size() / 2)) / 2.0);
    }
}
