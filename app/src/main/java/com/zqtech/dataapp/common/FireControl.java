package com.zqtech.dataapp.common;

public class FireControl {
    private String longitude;
    private String latitude;
    private String location;

    public FireControl() {
    }

    public FireControl(String longitude, String latitude, String location) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
