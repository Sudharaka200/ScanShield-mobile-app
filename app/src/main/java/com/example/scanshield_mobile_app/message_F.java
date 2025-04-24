package com.example.scanshield_mobile_app;

public class message_F {
    private String phoneNumber;
    private String Message;
    private String DateTime;

    public message_F(){
    }

    public message_F(String message, String phoneNumber, String dateTime) {
        Message = message;
        this.phoneNumber = phoneNumber;
        DateTime = dateTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
