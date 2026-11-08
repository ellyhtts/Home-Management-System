package com.Home_Management_System.Aplication_Api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String familyRole;
    private Boolean active;
    private LocalDateTime createdAt;

    private AddressUser addressUser;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public User(String name, String email, String password, String familyRole, AddressUser addressUser) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.familyRole = familyRole;
        this.addressUser = addressUser;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFamilyRole() { return familyRole; }
    public void setFamilyRole(String familyRole) { this.familyRole = familyRole; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public AddressUser getAddressUser() { return addressUser; }
    public void setAddressUser(AddressUser addressUser) { this.addressUser = addressUser; }
}