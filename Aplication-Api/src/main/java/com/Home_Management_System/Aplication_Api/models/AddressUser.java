package com.Home_Management_System.Aplication_Api.models;

public class AddressUser {

    private String city;
    private String district;
    private String street;
    private Integer houseNumber; // Wrapper Integer (proibido usar int!)[cite: 2, 13]

    // Construtor vazio obrigatório para o Spring serializar o JSON[cite: 13]
    public AddressUser() {
    }

    // Construtor com todos os campos
    public AddressUser(String city, String district, String street, Integer houseNumber) {
        this.city = city;
        this.district = district;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    // Getters e Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }
}