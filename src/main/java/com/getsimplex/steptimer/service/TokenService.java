//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.LoginToken;
import com.getsimplex.steptimer.model.User;
import com.getsimplex.steptimer.utils.JedisData;

import java.util.*;

import static com.getsimplex.steptimer.utils.JedisData.deleteFromRedis;

/**
 * Created by sean on 6/13/2017.
 */
public class TokenService {

    public static Optional<LoginToken> lookupToken(String userToken)throws Exception {
        Optional<LoginToken> tokenOptional = JedisData.getEntityById(LoginToken.class, userToken);

        return tokenOptional;
    }

    public static Optional<LoginToken> renewToken(String userToken)throws Exception {
        Optional<LoginToken> expiredTokenOptional = lookupToken(userToken);
        if (expiredTokenOptional.isPresent() && expiredTokenOptional.get().getExpires() && expiredTokenOptional.get().getExpiration().before(new Date())){
            LoginToken expiredToken = expiredTokenOptional.get();
            expiredToken.setExpiration(new Date(System.currentTimeMillis()+Long.valueOf(10*60*1000)));
            JedisData.loadToJedis(expiredToken,expiredToken.getUuid());
        }
        return expiredTokenOptional;
    }

    public static Optional<User> getUserFromToken(String userToken) throws Exception{
        Optional<User> foundUser = Optional.empty();

        Optional<LoginToken> tokenOptional = lookupToken(userToken);
        if (tokenOptional.isPresent()){
            LoginToken loginToken = tokenOptional.get();
            foundUser = Optional.of(FindUser.getUserByUserName(loginToken.getUser()));
        }
        return foundUser;
    }

    public static String createUserToken(String userName)throws Exception{

        String tokenString = UUID.randomUUID().toString();
        Long currentTimeMillis = System.currentTimeMillis();


        Long expiration = currentTimeMillis + 60 * 60 * 1000;  // expires after 60 minutes

        return createUserTokenSpecificTimeout(userName, expiration);

    }

    public static String createUserTokenSpecificTimeout(String userName, Long timeout) throws Exception{
        String tokenString = UUID.randomUUID().toString();
        Long currentTimeMillis = System.currentTimeMillis();
        LoginToken token = new LoginToken();
        token.setExpires(true);
        token.setUuid(tokenString);
        token.setUser(userName);

        Long expiration = currentTimeMillis + 60 * 60 * 1000;  // expires after 60 minutes
        Date expirationDate = new Date(timeout);
        token.setExpiration(expirationDate);

        JedisData.loadToJedis(token, token.getUuid());
        return tokenString;
    }

    public static Boolean isExpired(String tokenString) throws Exception{
        Optional<LoginToken> matchingToken=JedisData.getEntityById(LoginToken.class, tokenString);

        return matchingToken.isPresent() && matchingToken.get().getExpires() && matchingToken.get().getExpiration().before(new Date());
    }
}
