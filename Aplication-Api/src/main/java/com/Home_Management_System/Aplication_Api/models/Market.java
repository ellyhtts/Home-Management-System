package com.Home_Management_System.Aplication_Api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "markets")
public class Market {

    @Id
    private String id;
    private String nome;
    private String cnpj;
    private String telefone;
    private EnderecoMarket endereco;

    public Market() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public EnderecoMarket getEndereco() { return endereco; }
    public void setEndereco(EnderecoMarket endereco) { this.endereco = endereco; }
}