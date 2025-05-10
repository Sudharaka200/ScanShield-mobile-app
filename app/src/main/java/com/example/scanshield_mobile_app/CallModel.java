package com.example.scanshield_mobile_app;

public class CallModel {
    private String number;
    private String type;
    private String date;
    private String duration;
    private boolean isSpam;

    public CallModel(String number, String type, String date, String duration, boolean isSpam) {
        this.number = number;
        this.type = type;
        this.date = date;
        this.duration = duration;
        this.isSpam = isSpam;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isSpam() {
        return isSpam;
    }
}
