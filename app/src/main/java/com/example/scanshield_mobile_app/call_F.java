package com.example.scanshield_mobile_app;

public class call_F {
    private String email;
    private String phonenumber;
    private String status;
    private String datetime;

    public call_F(String email, String phonenumber, String status, String datetime) {
        this.email = email;
        this.phonenumber = phonenumber;
        this.status = status;
        this.datetime = datetime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
