package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Document(collection = "activity_logs")
public class ActivityLog {

    @Id
    private String id;
    private String action;      // Ex: "CADASTRO_ESTOQUE", "ATUALIZACAO_ESTOQUE"[cite: 14, 25]
    private String details;     // Descrição detalhada da alteração[cite: 14]
    private LocalDateTime timestamp;

    @DocumentReference
    private User user;          // Referência para o usuário que executou a ação[cite: 14, 27, 28]

    // Construtor padrão obrigatório para o Spring Data
    public ActivityLog() {
        this.timestamp = LocalDateTime.now();
    }

    // Construtor utilitário para facilitar o disparo automático nos Services
    public ActivityLog(String action, String details, User user) {
        this.action = action;
        this.details = details;
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}