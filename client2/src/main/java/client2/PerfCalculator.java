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
        try (BufferedReader reader = Files.newBufferedReader(stats.getRecordOutputFilePath())) {
            int totalLatency = 0;
            List<Long> latencies = stats.getLatencies();
//            List<Long> latencies = new ArrayList<>();
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] fields = line.split(",");
//                long startTime = Long.parseLong(fields[0]);
//                String method = fields[1];
//                long latency = Long.parseLong(fields[2]);
//                String statusCode = fields[3];
//
//                totalLatency += latency;
//                latencies.add(latency);
//            }
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
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }
}
