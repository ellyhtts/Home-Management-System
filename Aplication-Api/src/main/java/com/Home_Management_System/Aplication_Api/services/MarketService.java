package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Market;
import com.Home_Management_System.Aplication_Api.repositories.MarketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;

    public List<Market> listarTodos() {
        return marketRepository.findAll();
    }

    public Optional<Market> buscarPorId(String id) {
        return marketRepository.findById(id);
    }

    public Market salvar(Market market) {
        validarCamposObrigatorios(market);

        if (marketRepository.existsByCnpj(market.getCnpj())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um supermercado com o CNPJ: " + market.getCnpj());
        }
        return marketRepository.save(market);
    }

    public Market atualizar(String id, Market dados) {
        Market existente = marketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supermercado não encontrado!"));

        validarCamposObrigatorios(dados);


        marketRepository.findByCnpj(dados.getCnpj()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Já existe um supermercado com o CNPJ: " + dados.getCnpj());
            }
        });

        existente.setNome(dados.getNome());
        existente.setCnpj(dados.getCnpj());
        existente.setTelefone(dados.getTelefone());
        existente.setEndereco(dados.getEndereco());

        return marketRepository.save(existente);
    }

    public void deletar(String id) {
        if (!marketRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Supermercado não encontrado!");
        }
        marketRepository.deleteById(id);
    }

    private void validarCamposObrigatorios(Market market) {
        if (market.getNome() == null || market.getNome().isBlank()
                || market.getCnpj() == null || market.getCnpj().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome e CNPJ do supermercado são obrigatórios!");
        }
    }
}