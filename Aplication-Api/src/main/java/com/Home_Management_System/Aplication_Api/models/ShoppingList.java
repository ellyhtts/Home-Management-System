package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "shopping_lists")
public class ShoppingList {
    @Id
    private String id;
    private String titulo;
    private LocalDateTime dataCriacao;
    private String status;

    @DocumentReference
    private User criador;

    private List<ShoppingListItem> itens; // Embedded List


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getCriador() { return criador; }
    public void setCriador(User criador) { this.criador = criador; }
    public List<ShoppingListItem> getItens() { return itens; }
    public void setItens(List<ShoppingListItem> itens) { this.itens = itens; }
}