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
    private static final String SKIER_QUEUE_NAME = "skierQ";
    //    private static final String QUEUE_NAME = "postLiftRideQ";
    //TODO: check host nameSystem
    private static final String HOST_NAME = "localhost";
//    private static final String HOST_NAME = "172.31.28.212"; //private
//    private static final String HOST_NAME = "52.12.84.113"; //public
    private static final int PORT = 5672;
    private static final int NUMTHREADS = 256;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        //TODO: check username and password
//        factory.setUsername("admin");
//        factory.setPassword("password");
//        factory.setVirtualHost("/");
//        factory.setPort(PORT);
        final Connection connection = factory.newConnection();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        System.out.println(NUMTHREADS + " consumers started.");
        for (int i = 0; i < NUMTHREADS; i++) {
            Runnable runnable = () -> {
                try {
                    final Channel channel = connection.createChannel();

//                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    channel.queueDeclare(SKIER_QUEUE_NAME, false, false, false, null);

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

                        LiftRideDao dao = new LiftRideDao();
//                        System.out.println("consumer:" + new LiftRide(skierId, resortId, seasonsId, dayId, time, liftId, waitTime));
                        dao.createLiftRide(new LiftRide(skierId, resortId, seasonsId, dayId, time, liftId, waitTime));
                        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                    };
                    // process messages
//                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
                    channel.basicConsume(SKIER_QUEUE_NAME, false, deliverCallback, consumerTag -> {
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
