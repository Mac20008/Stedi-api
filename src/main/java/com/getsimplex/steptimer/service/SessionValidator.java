//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.utils.JedisData;
import com.getsimplex.steptimer.model.Token;
import com.getsimplex.steptimer.model.ValidationResponse;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;

/**
 * Created by sean on 8/12/2016.
 */
public class SessionValidator {

    public static HashMap<org.eclipse.jetty.websocket.api.Session, String> sessionTokens = new HashMap<org.eclipse.jetty.websocket.api.Session, String>();


    public static Boolean validateToken(String tokenString) throws Exception{
        return TokenService.isExpired(tokenString);
    }

    public static String emailFromToken(String tokenString) throws Exception{
        String customerEmail="";
        if  (!TokenService.isExpired(tokenString))
        {
            customerEmail=TokenService.getUserFromToken(tokenString).get().getEmail();
        }

        return customerEmail;

    }


}
