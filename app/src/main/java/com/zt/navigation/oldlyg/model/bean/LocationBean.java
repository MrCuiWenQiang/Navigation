package com.zt.navigation.oldlyg.model.bean;

public class LocationBean {
    private String userId;
    private String longitude;
    private String latitude;
    private String address;

    public LocationBean() {
    }

    public LocationBean(String userId, String longitude, String latitude, String address) {
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
