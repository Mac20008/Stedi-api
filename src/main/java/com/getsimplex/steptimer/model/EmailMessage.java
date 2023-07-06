package com.getsimplex.steptimer.model;

public class EmailMessage {
    private String messageText;
    private String toAddress;
    private String subject;

    private String name;


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;

    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
     public String getName(){
        return name;
     }
    public void setName(String name) {
        this.name = name;
    }
}
