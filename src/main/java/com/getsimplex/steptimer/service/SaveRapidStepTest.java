//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.LoginToken;
import com.google.gson.Gson;
import com.getsimplex.steptimer.model.RapidStepTest;
import spark.Request;
import com.getsimplex.steptimer.utils.GsonFactory;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.*;


/**
 * Created by sean on 9/7/2016.
 */
public class SaveRapidStepTest {
    private static Gson gson = GsonFactory.getGson();


    public static void save(String rapidStepTestString) throws Exception{
        System.out.println("Rapid Step Test:"+rapidStepTestString);
        RapidStepTest rapidStepTest = gson.fromJson(rapidStepTestString, RapidStepTest.class);
        JedisData.loadToJedisWithIndex(rapidStepTest,UUID.randomUUID().toString(), rapidStepTest.getStopTime(), "CustomerId",rapidStepTest.getCustomer());
        // The above adds the same data to 2 sorted sets:
        // - one containing all the customer's step tests
        // - one containing step tests for all customers
        // Both are sorted in ascending order (when using zrange) of the stop time of the test
    }
}
