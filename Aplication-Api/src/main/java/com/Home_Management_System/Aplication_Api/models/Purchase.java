package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "purchases")
public class Purchase {
    @Id
    private String id;
    private LocalDateTime dataCompra;
    private BigDecimal valorTotal;
    private String formaPagamento;

    @DocumentReference
    private User comprador;

    @DocumentReference
    private Market market;

    private List<PurchaseItem> itens;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public User getComprador() { return comprador; }
    public void setComprador(User comprador) { this.comprador = comprador; }
    public Market getMarket() { return market; }
    public void setMarket(Market market) { this.market = market; }
    public List<PurchaseItem> getItens() { return itens; }
    public void setItens(List<PurchaseItem> itens) { this.itens = itens; }
}