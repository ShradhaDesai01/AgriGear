package com.example.agrigear;

public class User {
    private String name;
    private String email;
    private String mobile;
    private String city;
    private String address;
    private String uniqueId;

    // Default constructor required for Firebase
    public User() {
    }

    public User(String name, String email, String mobile, String city, String address, String uniqueId) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.city = city;
        this.address = address;
        this.uniqueId = uniqueId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
} 