package com.example.scanshield_mobile_app;

public class message_F {
    private String email;
    private String phoneNumber;
    private String Message;
    private String DateTime;

    public message_F(){
    }

    public message_F(String email, String message, String phoneNumber, String dateTime) {
        this.email = email;
        Message = message;
        this.phoneNumber = phoneNumber;
        DateTime = dateTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
