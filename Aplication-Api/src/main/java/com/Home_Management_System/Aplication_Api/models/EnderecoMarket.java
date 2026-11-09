package com.Home_Management_System.Aplication_Api.models;

public class EnderecoMarket {

    private String street;
    private String neighborhood;
    private String city;
    private String state;

    public EnderecoMarket() {}

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
