package com.example.scanshield_mobile_app;

public class CallLogModel {
    private String number;
    private String name;
    private String date;
    private String duration;
    private String type;
    private String status;
    private String time;

    public CallLogModel() {}

    public CallLogModel(String number, String name, String type, String date, String time, String duration) {
        this.number = number;
        this.name = name != null ? name : "Unknown";
        this.type = type;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.status = "unknown"; // Default until Firebase updates
    }

    // Getters and setters
    public String getNumber() { return number; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getDuration() { return duration; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getTime() { return time; }

    public void setNumber(String number) { this.number = number; }
    public void setName(String name) { this.name = name; }
    public void setDate(String date) { this.date = date; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setTime(String time) { this.time = time; }
}