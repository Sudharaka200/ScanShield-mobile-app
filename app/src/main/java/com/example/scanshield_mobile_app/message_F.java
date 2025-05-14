package com.example.scanshield_mobile_app;

public class message_F {
    private String key; // Add this to store the Firebase key
    private String email;
    private String phoneNumber;
    private String message;
    private String dateTime;
    private Boolean isSpam;
    private Boolean read;
    private String replies;

    public message_F() {
        // Default constructor for Firebase
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getIsSpam() {
        return isSpam != null ? isSpam : false; // Default to false if null
    }

    public void setIsSpam(Boolean isSpam) {
        this.isSpam = isSpam;
    }

    public Boolean isRead() {
        return read != null ? read : false; // Default to false if null
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public String getReplies() {
        return replies != null ? replies : "";
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }
}