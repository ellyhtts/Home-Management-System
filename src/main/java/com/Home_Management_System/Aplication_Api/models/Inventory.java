package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "inventory")
public class Inventory {

    @Id
    private String id;
    private Integer quantity;         
    private Integer minimumQuantity;   
    private String storageLocation;    
    private LocalDate expirationDate;
    private LocalDateTime lastUpdated;

    @DocumentReference
    private Product product;           

    @DocumentReference
    private User responsibleUser;      

    // Construtor padrão
    public Inventory() {
        this.lastUpdated = LocalDateTime.now();
    }

    // Construtor com campos (opcional, facilita instanciar para testes)
    public Inventory(Integer quantity, Integer minimumQuantity, String storageLocation,
                     LocalDate expirationDate, Product product, User responsibleUser) {
        this.quantity = quantity;
        this.minimumQuantity = minimumQuantity;
        this.storageLocation = storageLocation;
        this.expirationDate = expirationDate;
        this.product = product;
        this.responsibleUser = responsibleUser;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }
}
