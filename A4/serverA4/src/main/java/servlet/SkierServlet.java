package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.LiftRide;
import model.SeasonVertical;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "servlet.SkierServlet", value = "/servlet.SkierServlet")
public class SkierServlet extends HttpServlet {
    private static final int DAY_MIN = 1;
    private static final int DAY_MAX = 366;
    private static final String DATA_SEPARATOR = ",";

    private final Gson gson = new Gson();
    private static final String EXCHANGE_NAME = "postLiftRide";
    private static final String SKIER_QUEUE_NAME = "skierQ";
    private static final String RESORT_QUEUE_NAME = "resortQ";
    //    private static final String QUEUE_NAME = "postLiftRideQ";
    //TODO: check host name
    private static final String HOST_NAME = "localhost";
    //    private static final String HOST_NAME = "172.31.28.212"; //private
//    private static final String HOST_NAME = "35.86.246.38"; //public
    private static final int PORT = 5672;


    //TODO: check host name
    private static final String JEDIS_HOST_NAME = "localhost";


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
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        //TODO: check username and password
//        factory.setUsername("admin");
//        factory.setPassword("password");
//        factory.setVirtualHost("/");
//        factory.setPort(PORT);
        try {
            conn = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        channelPool = new GenericObjectPool<>(new ChannelFactory());
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing parameters");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject message = new JsonObject();
            message.addProperty("message", "Invalid URL.");
            res.getWriter().write(String.valueOf(message));
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            JedisPool pool = new JedisPool(JEDIS_HOST_NAME, 6379);

            // do any sophisticated processing with urlParts which contains all the url params
            //  GET: /skiers/{skierID}/vertical
            if (urlParts[2].equals("vertical")) {
                String specifiedResort = req.getParameter("resort");
                System.out.println("specifiedResorts:" + specifiedResort);
                String specifiedSeason = req.getParameter("season");
                if (specifiedResort == null) {
                    JsonObject message = new JsonObject();
                    message.addProperty("message", "Parameter resort required!");
                    res.getWriter().write(String.valueOf(message));
                } else {
                    String skierID = urlParts[1];
                    List<SeasonVertical> seasonVerticals = getVerticalTotals(pool, specifiedResort, skierID, specifiedSeason);
                    if (seasonVerticals != null) {
                        JsonArray jsonArray = this.gson.toJsonTree(seasonVerticals).getAsJsonArray();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("resorts", jsonArray);
                        res.getWriter().write(jsonObject.toString());
                    } else {
                        res.getWriter().write("null");
                    }
                }

                // GET: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
            } else {
                String resortID = urlParts[1];
                String seasonID = urlParts[3];
                String dayID = urlParts[5];
                String skierID = urlParts[7];
                Integer dayVertical = getVerticalTotalsForDay(pool, resortID, skierID, seasonID, dayID);
//                System.out.println("dayVertical:" + dayVertical);
                res.getWriter().write(String.valueOf(dayVertical));
            }
        }
    }


    /**
     * Get the total vertical for the skier the specified resort. If no season is specified, return all seasons
     * GET: /skiers/{skierID}/vertical
     */
    private List<SeasonVertical> getVerticalTotals(JedisPool pool, String resortID, String skierID, String seasonID) {
        try (Jedis jedis = pool.getResource()) {
            List<SeasonVertical> res = new ArrayList<>();
            Map<String, Integer> seasonToVetical = new HashMap<>();
            String totalKey = "resortID:" + resortID + DATA_SEPARATOR
                    + "skierID:" + skierID + DATA_SEPARATOR;
            if (seasonID != null) {
                String field = "season:" + seasonID;
                String stringVertical = jedis.hget(totalKey, field);
                Integer vertical;
                if (stringVertical != null) {
                    vertical = Integer.parseInt(jedis.hget(totalKey, field));
                } else {
                    vertical = 0;
                }
                SeasonVertical seasonVertical = new SeasonVertical(seasonID, vertical);
                res.add(seasonVertical);
            } else {
                Set<String> seasons = jedis.hkeys(totalKey);
                for (String season: seasons) {
//                    String field = "season:" + season;
                    Integer vertical = Integer.parseInt(jedis.hget(totalKey, season));
                    SeasonVertical seasonVertical = new SeasonVertical(season, vertical);
                    res.add(seasonVertical);
                }
             }
            System.out.println("getVerticalTotals:"+ res);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get ski day vertical for a skier.
     * GET: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}
     */
    private Integer getVerticalTotalsForDay(JedisPool pool, String resortID, String skierID, String seasonID, String dayID) {
        try (Jedis jedis = pool.getResource()) {
            String dayField ="resortID:" + resortID + DATA_SEPARATOR
                    + "skierID:" + skierID + DATA_SEPARATOR
                    + "season:" + seasonID + DATA_SEPARATOR
                    + "day:" + dayID;
            return Integer.parseInt(jedis.hget("DAY_VERTICAL", dayField));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath.length == 8) {
            return isNumeric(urlPath[1]) &&
                    urlPath[2].equals("seasons") &&
                    isNumeric(urlPath[3]) &&
                    urlPath[3].length() == 4 &&
                    urlPath[4].equals("days") &&
                    isNumeric(urlPath[5]) &&
                    Integer.parseInt(urlPath[5]) >= DAY_MIN &&
                    Integer.parseInt(urlPath[5]) <= DAY_MAX &&
                    urlPath[6].equals("skiers") &&
                    isNumeric(urlPath[7]);
        } else if (urlPath.length == 3) {
            return isNumeric(urlPath[1]) &&
                    urlPath[2].equals("vertical");
        }
        return false;
    }

    private boolean isNumeric(String s) {
        if (s == null || s.equals("")) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
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
//                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
                channel.queueDeclare(SKIER_QUEUE_NAME, false, false, false, null);
                channel.queueDeclare(RESORT_QUEUE_NAME, false, false, false, null);
                channel.queueBind(SKIER_QUEUE_NAME, EXCHANGE_NAME, "skier");
                channel.queueBind(RESORT_QUEUE_NAME, EXCHANGE_NAME, "resort");


                LiftRide lift = gson.fromJson(request.getReader(), LiftRide.class);
                // message = "skierId,resortID,seasonID,dayID,timestamp,liftId,waitTime"
                String message = urlParts[7] + "," + urlParts[1] + "," + urlParts[3] + "," + urlParts[5] + "," + lift.getTime() + "," + lift.getLiftID() + "," + lift.getWaitTime();
//                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
//                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
                channel.basicPublish(EXCHANGE_NAME, "skier", null, message.getBytes(StandardCharsets.UTF_8));
                channel.basicPublish(EXCHANGE_NAME, "resort", null, message.getBytes(StandardCharsets.UTF_8));
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

    @Override
    public void destroy() {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

