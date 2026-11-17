package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "shopping_lists")
public class ShoppingList {
    @Id
    private String id;


    @DocumentReference
    @Field("userId")
    private User user;

    private String title;
    private LocalDateTime date;
    private List<ShoppingListItem> items;

    public ShoppingList() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public List<ShoppingListItem> getItems() { return items; }
    public void setItems(List<ShoppingListItem> items) { this.items = items; }
}
