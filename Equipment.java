package com.example.agrigear;

public class Equipment {
    private String id;
    private String name;
    private String specification;
    private String costPerHour;
    private String costPerDay;
    private String model;
    private String landmark;
    private String address;
    private String pincode;
    private String mobile;
    private String imageBase64;
    private boolean approved;
    private String uniqueId;

    // Default constructor required for Firestore
    public Equipment() {}

    public Equipment(String name, String specification, String costPerHour, String costPerDay, 
                    String model, String landmark, String address, String pincode, String mobile, 
                    String imageBase64) {
        this.name = name;
        this.specification = specification;
        this.costPerHour = costPerHour;
        this.costPerDay = costPerDay;
        this.model = model;
        this.landmark = landmark;
        this.address = address;
        this.pincode = pincode;
        this.mobile = mobile;
        this.imageBase64 = imageBase64;
        this.approved = false; // Default to not approved
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getCostPerHour() { return costPerHour; }
    public void setCostPerHour(String costPerHour) { this.costPerHour = costPerHour; }

    public String getCostPerDay() { return costPerDay; }
    public void setCostPerDay(String costPerDay) { this.costPerDay = costPerDay; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
} 