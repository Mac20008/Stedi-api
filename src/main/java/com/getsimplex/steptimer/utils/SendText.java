//© 2021 Sean Murdock

package com.getsimplex.steptimer.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

public class SendText {
    private static String ACCOUNT_SID ="";
    private static String AUTH_TOKEN = "";
    private static String TWILIO_PHONE= "";


    static {

        ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
        AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
        TWILIO_PHONE = System.getenv("TWILIO_PHONE");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void send(String destinationPhone, String text) throws Exception{

        String formattedPhone = getFormattedPhone(destinationPhone);
        PhoneNumber destination = new PhoneNumber(formattedPhone);
        PhoneNumber origin = new PhoneNumber(TWILIO_PHONE);

        Message message =Message.creator(destination, origin, text).create();

        System.out.println("**** Sent Text: ID "+message.getSid());

    }

    public static String getFormattedPhone(String inputPhone) throws Exception{
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(inputPhone, "US");
        String formattedPhone = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        formattedPhone = formattedPhone.replace(" ","");
        return formattedPhone;
    }

}
