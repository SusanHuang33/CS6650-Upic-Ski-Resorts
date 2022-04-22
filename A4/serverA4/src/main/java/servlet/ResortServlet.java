package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Resort;
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
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ResortServlet", value = "/ResortServlet")
public class ResortServlet extends HttpServlet {
    private static final int DAY_MIN = 1;
    private static final int DAY_MAX = 366;
    private static final String DATA_SEPARATOR = ",";

    private final Gson gson = new Gson();

    //TODO: check host name
    private static final String JEDIS_HOST_NAME = "localhost";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        String urlPath = req.getPathInfo();

        if (urlPath == null || urlPath.isEmpty()) {
//            res.setStatus(HttpServletResponse.SC_OK);
//            List<Resort> resorts = new ArrayList<Resort>();
//            resorts.add(new Resort("string", 0));
//            ResortResp resortResp = new ResortResp(resorts);
//            res.getWriter().write(gson.toJson(resortResp));
            Resort resort = new Resort("string", 0);
            List<Resort> resorts = Arrays.asList(resort);
            JsonArray jsonArray = this.gson.toJsonTree(resorts).getAsJsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("resorts", jsonArray);
            res.getWriter().write(jsonObject.toString());

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
            // GET: /resorts/{resortID}/seasons
            if (urlParts.length == 3) {
                List<String> dummy = Arrays.asList("string");
                JsonArray jsonArray = this.gson.toJsonTree(dummy).getAsJsonArray();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("seasons", jsonArray);
                res.getWriter().write(jsonObject.toString());
            } else {  // GET: /resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers

                String resortID = urlParts[1];
                String seasonID = urlParts[3];
                String dayID = urlParts[5];
                Integer numOfSkiers = getNumOfSkiersForDay(pool, resortID, seasonID, dayID);


                JsonObject numOfSkiersRes = new JsonObject();
                numOfSkiersRes.addProperty("time", "Mission Ridge"); //todo
                numOfSkiersRes.addProperty("numSkiers", numOfSkiers);
                res.getWriter().write(String.valueOf(numOfSkiersRes));


            }
        }
    }

    /**
     * Get number of unique skiers at resort/season/day.
     * GET: /resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skier
     */
    private Integer getNumOfSkiersForDay(JedisPool pool, String resortID, String seasonID, String dayID) {
        try (Jedis jedis = pool.getResource()) {
            String keyPattern = "resortID:" + resortID + DATA_SEPARATOR
                    + "season:" + seasonID + DATA_SEPARATOR
                    + "day:" + dayID + "*";
            ScanParams params = new ScanParams();
            params.match(keyPattern);
            // Use "0" to do a full iteration of the collection.
            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();
            for (String key : keys) {
                System.out.println("getNumOfSkiersForDay key:" + key + "\n");
            }
            System.out.println(keys.size());
            return keys.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath.length == 7) {
            return isNumeric(urlPath[1]) &&
                    urlPath[2].equals("seasons") &&
                    isNumeric(urlPath[3]) &&
                    urlPath[3].length() == 4 &&
                    urlPath[4].equals("day") &&
                    isNumeric(urlPath[5]) &&
                    Integer.parseInt(urlPath[5]) >= DAY_MIN &&
                    Integer.parseInt(urlPath[5]) <= DAY_MAX &&
                    urlPath[6].equals("skiers");
        } else if (urlPath.length == 3) {
            return isNumeric(urlPath[1]) &&
                    urlPath[2].equals("seasons");
        } else if (urlPath.length == 1) {
            return true;
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

    }
}
