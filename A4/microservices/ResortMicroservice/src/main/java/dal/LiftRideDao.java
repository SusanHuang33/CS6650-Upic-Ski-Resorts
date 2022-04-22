package dal;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class LiftRideDao {
    //    private static final String HOST_NAME = "172.31.30.233"; //private t3large
//    private static final String HOST_NAME = "172.31.8.142"; //private t2micro
    //TODO: check host name
    private static final String HOST_NAME = "localhost";
    //    private static final String HOST_NAME = "34.215.32.101"; //public
    private static final int PORT = 6379;
    private static final JedisPool pool = new JedisPool(HOST_NAME, PORT);
    private static final String DATA_SEPARATOR = ",";

    public LiftRideDao() {
    }

    public void createLiftRide(LiftRide newLiftRide) {
        try (Jedis jedis = pool.getResource()) {
            String keyForQuery1 = "resortID:" + newLiftRide.getResortId() + DATA_SEPARATOR
                    + "season:" + newLiftRide.getSeasonsId() + DATA_SEPARATOR
                    + "day:" + newLiftRide.getDayId() + DATA_SEPARATOR
                    + "skierID:" + newLiftRide.getSkierId();
            jedis.rpush(keyForQuery1, newLiftRide.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    /**
//     * Query3: On day N, how many lift rides took place in each hour of the ski day?
//     */
//    public Long getNumOfRidesForDayHour(String resortID, String seasonID, String dayID, String hour) {
//        try (Jedis jedis = pool.getResource()) {
//            String key = "resortID:" + resortID + DATA_SEPARATOR
//                    + "season:" + seasonID + DATA_SEPARATOR
//                    + "day:" + dayID + DATA_SEPARATOR
//                    + "hour:" + hour ;
//            return jedis.llen(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    /**
//     * Query2: How many rides on lift N happened on day N?
//     */
//    public Long getNumOfRidesForLiFtDay(String resortID, String seasonID, String dayID, String liftID) {
//        try (Jedis jedis = pool.getResource()) {
//            String key = "resortID:" + resortID + DATA_SEPARATOR
//                    + "season:" + seasonID + DATA_SEPARATOR
//                    + "day:" + dayID + DATA_SEPARATOR
//                    + "liftID:" + liftID ;
//            return jedis.llen(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * How many unique skiers visited resort X on day N?
//     */
//    public Long getNumOfSkiersForDay(String resortID, String seasonID, String dayID) {
//        try (Jedis jedis = pool.getResource()) {
//            String key = "resortID:" + resortID + DATA_SEPARATOR
//                    + "season:" + seasonID + DATA_SEPARATOR
//                    + "day:" + dayID;
//            return jedis.scard(key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}

