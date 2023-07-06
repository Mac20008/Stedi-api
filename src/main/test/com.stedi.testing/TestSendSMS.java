package com.stedi.testing;

import com.getsimplex.steptimer.utils.SendText;

public class TestSendSMS {

    public static void main (String[] args) throws Exception{

        SendText.send("8017190908", "Hello there!");
    }

}
