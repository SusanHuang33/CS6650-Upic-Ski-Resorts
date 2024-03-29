import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Consumer {

    private static final String QUEUE_NAME = "postLiftRideQ";
//    private static final String HOST_NAME = "localhost";
    private static final String HOST_NAME = "172.31.28.212";
    private static final int PORT = 5672;
    private static final int NUMTHREADS = 256;

    private static class LiftRide {
        int time;
        int liftId;
        int waitTime;
        LiftRide(int time, int liftId, int waitTime) {
            this.time = time;
            this.liftId = liftId;
            this.waitTime = waitTime;
        }

        @Override
        public String toString() {
            return "LiftEvent{" +
                    "time=" + time +
                    ", liftId=" + liftId +
                    ", waitTime=" + waitTime +
                    '}';
        }
    }
    private static final Map<Integer, List<LiftRide>> skierIdToLiftRideMap = new ConcurrentHashMap<>();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");
        factory.setPort(PORT);
        final Connection connection = factory.newConnection();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        System.out.println(NUMTHREADS + " consumers started.");
        for (int i = 0; i < NUMTHREADS; i++) {
            Runnable runnable = () -> {
                try {
                    final Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    // max one message per receiver
                    channel.basicQos(1);
//                    System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        String[] tokens = message.split(",");
                        int skierId = Integer.parseInt(tokens[0]);
                        int time = Integer.parseInt(tokens[1]);
                        int liftId = Integer.parseInt(tokens[2]);
                        int waitTime = Integer.parseInt(tokens[3]);
                        skierIdToLiftRideMap.putIfAbsent(skierId, new ArrayList<>());
                        skierIdToLiftRideMap.get(skierId).add(new LiftRide(time, liftId, waitTime));
//                        System.out.println(map);
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    };
                    // process messages
                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            Thread t = new Thread(runnable);
            t.start();
            completed.countDown();
        }
        completed.await();
    }
}
