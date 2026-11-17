package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "purchases")
public class Purchase {
    @Id
    private String id;

    @DocumentReference
    @Field("marketId")
    private Market market;

    @DocumentReference
    @Field("userId")
    private User user;

    private LocalDateTime date;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal totalValue;

    private List<PurchaseItem> purchasedItems;

    public Purchase() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Market getMarket() { return market; }
    public void setMarket(Market market) { this.market = market; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public List<PurchaseItem> getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(List<PurchaseItem> purchasedItems) { this.purchasedItems = purchasedItems; }
}