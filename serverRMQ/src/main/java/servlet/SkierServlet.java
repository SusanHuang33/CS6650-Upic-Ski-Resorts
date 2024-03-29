package servlet;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.LiftRide;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "servlet.SkierServlet", value = "/servlet.SkierServlet")
public class SkierServlet extends HttpServlet {
    private final Gson gson  = new Gson();
    private static final String QUEUE_NAME = "postLiftRideQ";
//    private static final String HOST_NAME = "localhost";
    private static final String HOST_NAME = "172.31.28.212";
//    private static final String HOST_NAME = "35.85.215.241";
    private static final int PORT = 5672;
    private static Connection conn;

    private static ObjectPool<Channel> channelPool;

    private static class ChannelFactory extends BasePooledObjectFactory<Channel> {
        @Override
        public Channel create() throws IOException {
            return conn.createChannel();
        }

        @Override
        public PooledObject<Channel> wrap(Channel channel) {
            return new DefaultPooledObject<>(channel);
        }

    }

    @Override
    public void init() {
//        gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");
        factory.setPort(PORT);
        try {
            conn = factory.newConnection();
            //TODO
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        channelPool = new GenericObjectPool<>(new ChannelFactory());
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if (!isGetUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            // do any sophisticated processing with urlParts which contains all the url params
            // TODO: process url params in `urlParts`
            res.getWriter().write("Getting skier data works!");
        }
    }

    private boolean isGetUrlValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/1/seasons/2019/day/1/skier/123"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        // get ski day vertical for a skier: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}:
        for (String e : urlPath) {
            System.out.println(e + ",");
        }
        if (urlPath.length == 8) {
            if (urlPath[2].equals("seasons") && urlPath[4].equals("days") && urlPath[6].equals("skiers")) {
                try {
                    for (int i = 1; i < 8; i += 2) {
                        int id = Integer.parseInt(urlPath[i]);
                    }
                    return true;
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
            return false;
            // get the total vertical for the skier for specified seasons at the specified resort: /skiers/{skierID}/vertical:
        } else if (urlPath.length == 3) {
            if (urlPath[2].equals("vertical")) {
                try {
                    int skierId = Integer.parseInt(urlPath[1]);
                    return true;
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        if (!isPostUrlPathValid(urlParts)) {
            response.getWriter().write("Invalid URL.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            try {
//                Channel channel = conn.createChannel();
                Channel channel = channelPool.borrowObject();
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                LiftRide lift = gson.fromJson(request.getReader(), LiftRide.class);

                // message = "skierId,timestamp,liftId,waitTime"
                String message = urlParts[7] + "," + lift.getTime() + "," + lift.getLiftID()+ "," + lift.getWaitTime();
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
//                channel.close();
                channelPool.returnObject(channel);
                response.getWriter().write(urlParts[7] + ": " + gson.toJson(lift));
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPostUrlPathValid(String[] urlPath) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/1/seasons/2019/day/1/skier/123"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        // write a new lift ride for the skier: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}:
        if (urlPath.length == 8) {
            try {
                for (int i = 1; i < urlPath.length; i += 2) {
                    Integer.parseInt(urlPath[i]);
                }

                return (urlPath[3].length() == 4
                        && Integer.parseInt(urlPath[5]) >= 1
                        && Integer.parseInt(urlPath[5]) <= 365
                        && urlPath[2].equals("seasons")
                        && urlPath[4].equals("days")
                        && urlPath[6].equals("skiers"));
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }


    private boolean isPostParametersPathValid(String[] parameters) {
        // TODO: validate the request url path according to the API spec
        // urlPath  = "/1/seasons/2019/day/1/skier/123"
        // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
        // write a new lift ride for the skier: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}:
        for (String e : parameters) {
            System.out.println(e);
        }
        if (parameters.length == 3) {
            HashMap<String, Integer> paras = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                String[] pairs = parameters[i].split(":");
                try {
                    String key = pairs[0].strip();
                    paras.put(key.substring(1, key.length() - 1), Integer.parseInt(pairs[1].strip()));
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
            for (String key : paras.keySet()) {
                System.out.println(key);
            }

            if (paras.containsKey("time") && paras.containsKey("liftID") && paras.containsKey("waitTime")) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void destroy() {
        if (conn == null) { return;}
        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

