package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

public class PurchaseItem {
    private String productId;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal unitPrice;

    private Integer quantity;

    public PurchaseItem() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}