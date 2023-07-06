//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.Customer;
import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.AlreadyExistsException;
import com.getsimplex.steptimer.utils.JedisClient;
import com.getsimplex.steptimer.utils.JedisData;
import com.getsimplex.steptimer.utils.SendText;
import com.google.gson.Gson;
import spark.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mandy on 10/4/2016.
 */
public class CustomerService {

    private static Gson gson = new Gson();
    private static JedisClient jedisClient = new JedisClient();

    public static String handleRequest(Request request, boolean update) throws Exception{
        String newCustomerRequest = request.body();
        Customer newCustomer = gson.fromJson(newCustomerRequest, Customer.class);

        return createCustomer(newCustomer, update);

    }
    public static Customer getCustomerByPhone(String phoneNumber) throws Exception{

        String formattedPhoneNumber = SendText.getFormattedPhone(phoneNumber);//ex: 801-719-0908 becomes +18017190908
        String formattedPhoneNumberDigitsOnly = formattedPhoneNumber.replaceAll("[^0-9]","");//ex: +18017190908 becomes 18017190908
        List<Customer> customers = JedisData.getEntitiesByScore(Customer.class, Long.valueOf(formattedPhoneNumberDigitsOnly), Long.valueOf(formattedPhoneNumberDigitsOnly));
        if (customers.isEmpty()){
            throw new Exception ("Phone number "+phoneNumber+" not found");
        }
        if (customers.size()>1){
            throw new Exception ("Multiple customers found with same phone number: "+phoneNumber);
        }
        return customers.get(0);

    }
    public static String createCustomer(Customer newCustomer, boolean update) throws Exception{
        String phone = SendText.getFormattedPhone(newCustomer.getPhone());//ex: 801-719-0908 becomes: +18017190908
        newCustomer.setPhone(phone);
        String numericDigitsOnly = phone.replaceAll("[^0-9]","");
        ArrayList<Customer> matchingCustomers = JedisData.getEntitiesByScore(Customer.class, Long.valueOf(numericDigitsOnly), Long.valueOf(numericDigitsOnly));
        Optional<Customer> matchingCustomer = JedisData.getEntityById(Customer.class, newCustomer.getPhone());
        if ((matchingCustomers.size()>0 || matchingCustomer.isPresent()) && !update){
            throw new AlreadyExistsException("Customer already exists");
        }

        if (newCustomer != null && !newCustomer.getCustomerName().isEmpty() && !newCustomer.getPhone().isEmpty()) {
            //SAVE USER TO REDIS
            JedisData.loadToJedis(newCustomer, newCustomer.getPhone(), Long.valueOf(numericDigitsOnly));
        } else{
            throw new Exception("Customer must have a non-empty name and phone");
        }
        return "Welcome: " + newCustomer.getCustomerName();
    }

}
