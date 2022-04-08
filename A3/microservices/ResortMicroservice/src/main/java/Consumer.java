import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import dal.LiftRide;
import dal.LiftRideDao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class Consumer {
    private static final String RESORT_QUEUE_NAME = "resortQ";
    //    private static final String EXCHANGE_NAME = "postLiftRideQ";
//    private static final String HOST_NAME = "localhost";
    private static final String HOST_NAME = "172.31.28.212"; //private
    //    private static final String HOST_NAME = "54.244.69.243"; //public
    private static final int PORT = 5672;
    private static final int NUMTHREADS = 256;

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
//                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    channel.queueDeclare(RESORT_QUEUE_NAME, false, false, false, null);

                    // max one message per receiver
                    channel.basicQos(1);
                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        String[] tokens = message.split(",");
                        int skierId = Integer.parseInt(tokens[0]);
                        int resortId = Integer.parseInt(tokens[1]);
                        int seasonsId = Integer.parseInt(tokens[2]);
                        int dayId = Integer.parseInt(tokens[3]);
                        int time = Integer.parseInt(tokens[4]);
                        int liftId = Integer.parseInt(tokens[5]);
                        int waitTime = Integer.parseInt(tokens[6]);
//                        System.out.println(new LiftRide(skierId,resortId,seasonsId,dayId,time,liftId,waitTime));
//                        skierIdToLiftRideMap.putIfAbsent(skierId, new ArrayList<>());
//                        skierIdToLiftRideMap.get(skierId).add(new LiftRide(resortId, seasonsId, dayId, time, liftId, waitTime));
//                        System.out.println(skierIdToLiftRideMap);
                        LiftRideDao dao = new LiftRideDao();
//                        System.out.println("consumer:" + new LiftRide(skierId, resortId, seasonsId, dayId, time, liftId, waitTime));
                        dao.createLiftRide(new LiftRide(skierId, resortId, seasonsId, dayId, time, liftId, waitTime));
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
//                        System.out.println("skierID:10, seasonID:2019");
//                        dao.getNumOfSkiDaysForSeason("10", "2019");
//                        System.out.println("skierID:10, seasonID:2022");
//                        dao.getNumOfSkiDaysForSeason("10", "2022");
//                        System.out.println("skierID:8, seasonID:2022");
//                        dao.getNumOfSkiDaysForSeason("8", "2022");

                    };
                    // process messages
//                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                    channel.basicConsume(RESORT_QUEUE_NAME, false, deliverCallback, consumerTag -> {
                    });
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
