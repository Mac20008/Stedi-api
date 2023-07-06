//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.JedisData;
import com.getsimplex.steptimer.utils.SendText;

import java.util.List;
import java.util.Optional;

/**
 * Created by sean on 6/13/2017.
 */
public class FindUser {

    public static User getUserByUserName(String userName) throws Exception{

        Optional<User> userOptional = JedisData.getEntityById(User.class, userName);
        if (!userOptional.isPresent()){
            throw new Exception ("User name not found");
        }
        User currentUser = userOptional.get();
        return currentUser;

    }

    public static User getUserByPhone(String phoneNumber) throws Exception{

        String formattedPhoneNumber = SendText.getFormattedPhone(phoneNumber);//ex: 801-719-0908 becomes +18017190908
        String formattedPhoneNumberDigitsOnly = formattedPhoneNumber.replaceAll("[^0-9]","");//ex: +18017190908 becomes 18017190908
        List<User> users = JedisData.getEntitiesByScore(User.class, Long.valueOf(formattedPhoneNumberDigitsOnly), Long.valueOf(formattedPhoneNumberDigitsOnly));
        if (users.isEmpty()){
            throw new Exception ("Phone number "+phoneNumber+" not found");
        }
        if (users.size()>1){
            throw new Exception ("Multiple users found with same phone number: "+phoneNumber);
        }
        return users.get(0);

    }


}
