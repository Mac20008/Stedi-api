//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.Customer;
import com.getsimplex.steptimer.utils.JedisClient;
import com.getsimplex.steptimer.utils.JedisData;
import com.google.gson.Gson;
import spark.Request;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Created by Mandy on 10/4/2016.
 */
public class FindCustomer {

    private static Gson gson = new Gson();
    private static JedisClient jedisClient = new JedisClient();

    public static String handleRequest(Request request) throws Exception{
        String customerEmail = request.params(":customer");

        Optional<Customer> matchingCustomer = findCustomer(customerEmail);

        if (matchingCustomer.isPresent()){
            return gson.toJson(matchingCustomer.get());
        } else {
            return null;
        }
    }

    public static Optional<Customer> findCustomer(String customerEmail) throws Exception{
        Optional<Customer> matchingCustomer = JedisData.getEntityById(Customer.class, customerEmail);
        return matchingCustomer;
    }

}
