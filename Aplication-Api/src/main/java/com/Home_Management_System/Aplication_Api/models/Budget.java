package com.Home_Management_System.Aplication_Api.models;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "budgets")
public class Budget {

    @Id
    private String id;

    @Field("mes_referencia")
    private Integer mesReferencia;

    @Field("ano_referencia")
    private Integer anoReferencia;

    @Field("valor_limite")
    private BigDecimal valorLimite;

    @Field("valor_gasto_atual")
    private BigDecimal valorGastoAtual;

    @DocumentReference
    @Field("category_id")
    private Category category;

    public Budget() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(Integer mesReferencia) { this.mesReferencia = mesReferencia; }
    public Integer getAnoReferencia() { return anoReferencia; }
    public void setAnoReferencia(Integer anoReferencia) { this.anoReferencia = anoReferencia; }
    public BigDecimal getValorLimite() { return valorLimite; }
    public void setValorLimite(BigDecimal valorLimite) { this.valorLimite = valorLimite; }
    public BigDecimal getValorGastoAtual() { return valorGastoAtual; }
    public void setValorGastoAtual(BigDecimal valorGastoAtual) { this.valorGastoAtual = valorGastoAtual; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}