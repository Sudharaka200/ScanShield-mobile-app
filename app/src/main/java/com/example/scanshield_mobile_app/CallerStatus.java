package com.example.scanshield_mobile_app;

public class CallerStatus {
    private String phoneNumber;
    private String status; // "spam" or "not_spam"

    public CallerStatus() {
    }

    public CallerStatus(String phoneNumber, String status) {
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
