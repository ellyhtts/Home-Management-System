package com.Home_Management_System.Aplication_Api.models;

public class ShoppingListItem {
    private String productId;
    private Integer quantityNeeded;
    private Boolean isPurchased;

    public ShoppingListItem() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Integer getQuantityNeeded() { return quantityNeeded; }
    public void setQuantityNeeded(Integer quantityNeeded) { this.quantityNeeded = quantityNeeded; }
    public Boolean getIsPurchased() { return isPurchased; }
    public void setIsPurchased(Boolean isPurchased) { this.isPurchased = isPurchased; }
}