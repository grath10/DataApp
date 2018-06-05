package com.zqtech.dataapp.common;

public class Person {
    private String name;
    private String personId;
    private String homeAddress;

    public Person() {

    }

    public Person(String name, String personId, String homeAddress) {
        this.name = name;
        this.personId = personId;
        this.homeAddress = homeAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
