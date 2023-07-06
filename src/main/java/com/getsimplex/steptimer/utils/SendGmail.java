package com.getsimplex.steptimer.utils;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendGmail {
    public static void send(String toAddress, String messageText, String subject, String name) {
        final String username = "sean@stedi.me";
        final String password = "Itsnot@boutme";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {

            Message message = new MimeMessage(session);
            InternetAddress fromAddress = new InternetAddress ("sara-96325@hotmail.com", name);
            message.setFrom(fromAddress);
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toAddress)
            );
            Address ccAddress =  new InternetAddress("info@stedi.me");
            message.addRecipient(Message.RecipientType.CC, ccAddress);
            message.setSubject(subject);
            message.setText(messageText);
//            message.setFrom(fromAddress);
            Transport.send(message);

            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
