package com.zqtech.dataapp.common;

import java.util.List;

public class Elevator {
    private String longitude;
    private String latitude;
    private String name;
    private String maintenanceCompany;
    private List<Maintenancer> maintenancerList;

    public Elevator() {
    }

    public Elevator(String longitude, String latitude, String name, String maintenanceCompany, List<Maintenancer> maintenancerList) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.maintenanceCompany = maintenanceCompany;
        this.maintenancerList = maintenancerList;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaintenanceCompany() {
        return maintenanceCompany;
    }

    public void setMaintenanceCompany(String maintenanceCompany) {
        this.maintenanceCompany = maintenanceCompany;
    }

    public List<Maintenancer> getMaintenancerList() {
        return maintenancerList;
    }

    public void setMaintenancerList(List<Maintenancer> maintenancerList) {
        this.maintenancerList = maintenancerList;
    }

    public String getMaintenancers() {
        StringBuffer buffer = new StringBuffer();
        for(Maintenancer maintenancer: maintenancerList) {
            buffer.append(maintenancer.toString());
            buffer.append(",");
        }
        if(buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
}
