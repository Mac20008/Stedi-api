//© 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.ExchangeOTPForLoginToken;
import com.getsimplex.steptimer.model.OneTimePassword;
import com.getsimplex.steptimer.utils.ExpiredException;
import com.getsimplex.steptimer.utils.JedisData;
import com.getsimplex.steptimer.utils.NotFoundException;
import com.getsimplex.steptimer.utils.SendText;
import com.google.gson.Gson;
import spark.Request;

import java.util.Date;
import java.util.Optional;

public class OneTimePasswordService {
    private static Gson gson = new Gson();

    public static String handleRequest(Request request) throws Exception{
        String requestBody = request.body();
        ExchangeOTPForLoginToken exchangeOTPForLoginToken = gson.fromJson(requestBody, ExchangeOTPForLoginToken.class);
        return exchangeOneTimePasswordForToken(exchangeOTPForLoginToken);
    }

    public static void saveOneTimePassword(OneTimePassword oneTimePassword) throws Exception{
        JedisData.loadToJedis(oneTimePassword, String.valueOf(oneTimePassword.getOneTimePassword()), oneTimePassword.getExpirationDate().getTime());
    }

    public static Optional<OneTimePassword> getOneTimePassword(Integer oneTimePassword) throws Exception{
        return JedisData.getEntityById(OneTimePassword.class, String.valueOf(oneTimePassword));
    }

    public static String exchangeOneTimePasswordForToken(ExchangeOTPForLoginToken otpForToken) throws Exception{
        Optional<OneTimePassword> otp = getOneTimePassword(otpForToken.getOneTimePassword());
        otpForToken.setPhoneNumber(SendText.getFormattedPhone(otpForToken.getPhoneNumber()));
        String loginToken="";
        if (!otp.isPresent() || otp.isPresent() && !otp.get().getPhoneNumber().equals(otpForToken.getPhoneNumber())){
            throw new NotFoundException("OTP :"+otpForToken.getOneTimePassword()+" not found for phone: "+otpForToken.getPhoneNumber());
        }

        if (otp.isPresent() && otp.get().getPhoneNumber().equals(otpForToken.getPhoneNumber()) && otp.get().getExpirationDate().before(new Date(System.currentTimeMillis()))){
            throw new ExpiredException("OTP: "+otpForToken.getOneTimePassword()+" for phone: "+otpForToken.getPhoneNumber()+" expired at: "+otp.get().getExpirationDate().getTime());
        }

        if (otp.isPresent() && otp.get().getPhoneNumber().equals(otpForToken.getPhoneNumber()) && otp.get().getExpirationDate().after(new Date())){
            loginToken= otp.get().getLoginToken();
        }

        return loginToken;
    }

}
