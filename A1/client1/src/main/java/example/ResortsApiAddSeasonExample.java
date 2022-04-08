package example;

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.ResortsApi;

public class ResortsApiAddSeasonExample {

    public static void main(String[] args) {

        ResortsApi apiInstance = new ResortsApi();
        ResortIDSeasonsBody body = new ResortIDSeasonsBody(); // ResortIDSeasonsBody | Specify new Season value
        body.setYear("2021");
        Integer resortID = 56; // Integer | ID of the resort of interest
        try {
            apiInstance.addSeason(body, resortID);
        } catch (ApiException e) {
            System.err.println("Exception when calling ResortsApi#addSeason");
            e.printStackTrace();
        }
    }
}