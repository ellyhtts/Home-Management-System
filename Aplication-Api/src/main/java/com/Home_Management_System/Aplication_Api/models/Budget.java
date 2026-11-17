package com.Home_Management_System.Aplication_Api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;
    private String month;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal limitAmount;

    // Total já gasto no mês (calculado pelo sistema a partir das transações)
    @Field(targetType = FieldType.DECIMAL128)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    public Budget() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }

    public BigDecimal getSpentAmount() { return spentAmount != null ? spentAmount : BigDecimal.ZERO; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    // Saldo restante = limite - gasto (não é persistido, apenas calculado)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public BigDecimal getRemainingAmount() {
        return limitAmount == null ? null : limitAmount.subtract(getSpentAmount());
    }
}
