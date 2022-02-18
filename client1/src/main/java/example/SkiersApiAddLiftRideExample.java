package example;

import io.swagger.client.ApiException;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import io.swagger.client.model.ResortIDSeasonsBody;

public class SkiersApiAddLiftRideExample {

    public static void main(String[] args) {

        SkiersApi apiInstance = new SkiersApi();

        LiftRide body = new LiftRide();
        body.setTime(217);
        body.setLiftID(21);
        body.setWaitTime(3);
        Integer resortID = 56;
        String seasonID = "2019";
        String dayID = "1";
        Integer skierID = 56;
        try {
            apiInstance.writeNewLiftRide(body,resortID,seasonID,dayID,skierID);
        } catch (ApiException e) {
            System.err.println("Exception when calling SkiersApi#writeNewLiftRide");
            e.printStackTrace();
        }
    }
}