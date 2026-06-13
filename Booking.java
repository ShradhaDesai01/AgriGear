package com.example.agrigear;

public class Booking {
    private String id;
    private String equipmentId;
    private String equipmentName;
    private String userId;
    private String vendorMobile;
    private String date;
    private String time;
    private String duration;
    private String purpose;
    private double totalCost;
    private String status; // pending, approved, rejected
    private long timestamp;

    // Default constructor required for Firestore
    public Booking() {}

    public Booking(String equipmentId, String equipmentName, String userId, String vendorMobile,
                  String date, String time, String duration, String purpose, double totalCost) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.userId = userId;
        this.vendorMobile = vendorMobile;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.purpose = purpose;
        this.totalCost = totalCost;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVendorMobile() { return vendorMobile; }
    public void setVendorMobile(String vendorMobile) { this.vendorMobile = vendorMobile; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 