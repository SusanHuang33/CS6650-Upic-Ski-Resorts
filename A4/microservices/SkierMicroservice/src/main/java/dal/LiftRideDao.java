package dal;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;


public class LiftRideDao {
    private static final String DATA_SEPARATOR = ",";
    //TODO: check host name
    private static final String HOST_NAME = "localhost";
    //    private static final String HOST_NAME = "172.31.30.233"; //private t3large
//    private static final String HOST_NAME = "172.31.8.142"; //private t2micro
    private static final int PORT = 6379;
    private static final JedisPool pool = new JedisPool(HOST_NAME, PORT);

    public LiftRideDao() {
    }

//    public void createLiftRide(LiftRide newLiftRide) {
//        try (Jedis jedis = pool.getResource()) {
//            String key = "skierID:" + newLiftRide.getSkierId() + DATA_SEPARATOR
//                    + "season:" + newLiftRide.getSeasonsId() + DATA_SEPARATOR
//                    + "day:" + newLiftRide.getDayId();
//            jedis.rpush(key, newLiftRide.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public void createLiftRide(LiftRide newLiftRide) {
        try (Jedis jedis = pool.getResource()) {
            String dayField = "resortID:" + newLiftRide.getResortId() + DATA_SEPARATOR
                    + "skierID:" + newLiftRide.getSkierId() + DATA_SEPARATOR
                    + "season:" + newLiftRide.getSeasonsId() + DATA_SEPARATOR
                    + "day:" + newLiftRide.getDayId();
            int vertical = newLiftRide.getLiftId() * 10;
            jedis.hincrBy("DAY_VERTICAL", dayField, vertical);

            String totalKey = "resortID:" + newLiftRide.getResortId() + DATA_SEPARATOR
                    + "skierID:" + newLiftRide.getSkierId() + DATA_SEPARATOR;
            String totalField = String.valueOf(newLiftRide.getSeasonsId());
            jedis.hincrBy(totalKey, totalField, vertical);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Query3: For skier N, show me the lifts they rode on each ski day.
     */
    public List<String> getLiftRidesForDay(String skierID, String seasonID, String dayID) {
        try (Jedis jedis = pool.getResource()) {
            String key = "skierID:" + skierID + DATA_SEPARATOR
                    + "season:" + seasonID + DATA_SEPARATOR
                    + "day:" + dayID;
            long ind = 0;
            long len = jedis.llen(key);
            List<String> res = new ArrayList<>();
            while (ind < len) {
                String liftRide = jedis.lindex(key, ind);
                res.add(liftRide);
                ind += 1;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Query2: For skier N, what are the vertical totals for each ski day?
     */
    public Integer getVerticalTotalsForDay(String skierID, String seasonID, String dayID) {
        try (Jedis jedis = pool.getResource()) {
            String key = "skierID:" + skierID + DATA_SEPARATOR
                    + "season:" + seasonID + DATA_SEPARATOR
                    + "day:" + dayID;
            long ind = 0;
            long len = jedis.llen(key);
            int verticalTotal = 0;
            while (ind < len) {
                String[] liftRide = jedis.lindex(key, ind).substring(8, -1).split(DATA_SEPARATOR);
                String liftID = liftRide[4].split("=")[1];
                verticalTotal += Integer.parseInt(liftID) * 10;
                ind += 1;
            }
            return verticalTotal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Query1: For skier N, how many days have they skied this season?
     */
    public Integer getNumOfSkiDaysForSeason(String skierID, String seasonID) {
        try (Jedis jedis = pool.getResource()) {
//            jedis.lpush("queue#tasks", "firstTask");
            String keyPattern = "skierID:" + skierID + DATA_SEPARATOR + "season:" + seasonID + "*";
            ScanParams params = new ScanParams();
            params.match(keyPattern);

            // Use "0" to do a full iteration of the collection.
            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();
            for (String key : keys) {
                System.out.println("key:" + key + "\n");
            }
            System.out.println(keys.size());
            return keys.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

