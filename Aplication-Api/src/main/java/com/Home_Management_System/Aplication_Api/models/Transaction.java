package com.Home_Management_System.Aplication_Api.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String descricao;
    private BigDecimal valor;
    private String tipo; // "DESPESA" ou "RECEITA"

    @Field("data_transacao")
    private LocalDateTime dataTransacao;

    @DocumentReference
    @Field("purchase_id")
    private Purchase purchase;

    @DocumentReference
    @Field("budget_id")
    private Budget budget;

    public Transaction() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDateTime getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDateTime dataTransacao) { this.dataTransacao = dataTransacao; }
    public Purchase getPurchase() { return purchase; }
    public void setPurchase(Purchase purchase) { this.purchase = purchase; }
    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
}