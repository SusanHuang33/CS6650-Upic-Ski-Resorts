package example;

import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.ResortsApi;

import java.io.File;
import java.util.*;

public class ResortsApiGetResortSeasonsExample {

    public static void main(String[] args) {

        ResortsApi apiInstance = new ResortsApi();
        Integer resortID = 56; // Integer | ID of the resort of interest
        try {
            SeasonsList result = apiInstance.getResortSeasons(resortID);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling ResortsApi#getResortSeasons");
            e.printStackTrace();
        }
    }
}