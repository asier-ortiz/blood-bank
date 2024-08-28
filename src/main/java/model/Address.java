package model;

import java.io.Serializable;

public class Address implements Serializable {
    private final String street;
    private final String number;
    private final String zipCode;
    private String floor;
    private String houseNumber;
    private final String city;
    private final String country;

    public Address(String street, String number, String zipCode, String floor, String houseNumber, String city, String country) {
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.floor = floor;
        this.houseNumber = houseNumber;
        this.city = city;
        this.country = country;
    }

    public Address(String street, String number, String zipCode, String city, String country) {
        this.street = street;
        this.number = number;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number=" + number +
                ", zipCode='" + zipCode + '\'' +
                ", floor='" + floor + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}