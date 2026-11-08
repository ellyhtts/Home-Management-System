package com.Home_Management_System.Aplication_Api.models;

public class ShoppingListItem {
    private String nomeProduto;
    private Integer quantidade;
    private String unidadeMedida;
    private Boolean comprado;


    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public Boolean getComprado() { return comprado; }
    public void setComprado(Boolean comprado) { this.comprado = comprado; }
}