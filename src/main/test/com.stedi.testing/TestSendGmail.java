package com.stedi.testing;

import com.getsimplex.steptimer.utils.SendGmail;

public class TestSendGmail {
 public static void main (String[] args) {
     SendGmail.send("scmurdock@gmail.com", "Hello, this is a test", "Testing","Sean");
 }
}
