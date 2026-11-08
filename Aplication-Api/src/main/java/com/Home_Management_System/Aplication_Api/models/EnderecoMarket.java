package com.Home_Management_System.Aplication_Api.models;

public class EnderecoMarket {

    private String rua;
    private String bairro;
    private String cidade;
    private String estado;

    public EnderecoMarket() {}

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}