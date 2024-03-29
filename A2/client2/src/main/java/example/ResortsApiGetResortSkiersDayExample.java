package example;

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.ResortsApi;

public class ResortsApiGetResortSkiersDayExample {

    public static void main(String[] args) {

        ResortsApi apiInstance = new ResortsApi();
        Integer resortID = 56; // Integer | ID of the resort of interest
        Integer seasonID = 56; // Integer | ID of the resort of interest
        Integer dayID = 56; // Integer | ID of the resort of interest
        try {
            ResortSkiers result = apiInstance.getResortSkiersDay(resortID, seasonID, dayID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ResortsApi#getResortSkiersDay");
            e.printStackTrace();
        }
    }
}