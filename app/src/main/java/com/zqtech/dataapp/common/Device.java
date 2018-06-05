package com.zqtech.dataapp.common;

public class Device {
    private String longitude;
    private String latitude;
    private String deviceCode;
    private String location;

    public Device() {

    }

    public Device(String longitude, String latitude, String location, String deviceCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.deviceCode = deviceCode;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
