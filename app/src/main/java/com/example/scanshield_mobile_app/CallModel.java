package com.example.scanshield_mobile_app;

public class CallModel {
    private String number;
    private String duration;
    private String type;
    private long date;

    public CallModel() {}

    public CallModel(String number, String duration, String type, long date) {
        this.number = number;
        this.duration = duration;
        this.type = type;
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public String getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public long getDate() {
        return date;
    }
}
