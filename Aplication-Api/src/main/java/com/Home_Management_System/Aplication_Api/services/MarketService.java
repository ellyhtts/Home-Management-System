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

    @Autowired
    private ActivityLogService activityLogService;

    public List<Market> listarTodos() { return marketRepository.findAll(); }

    public Optional<Market> buscarPorId(String id) { return marketRepository.findById(id); }

    public Market salvar(Market market) {
        validar(market);
        Market salvo = marketRepository.save(market);
        activityLogService.registrar("Cadastrou o supermercado '" + market.getName()
                + "' (" + market.getLocation() + ")");
        return salvo;
    }

    public Market atualizar(String id, Market dados) {
        Market existente = marketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supermercado não encontrado!"));
        validar(dados);
        existente.setName(dados.getName());
        existente.setLocation(dados.getLocation());
        Market salvo = marketRepository.save(existente);
        activityLogService.registrar("Atualizou os dados do supermercado '" + existente.getName() + "'");
        return salvo;
    }

    public void deletar(String id) {
        if (!marketRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Supermercado não encontrado!");
        }
        String nome = marketRepository.findById(id).map(Market::getName).orElse("sem nome");
        marketRepository.deleteById(id);
        activityLogService.registrar("Excluiu o supermercado '" + nome + "'");
    }

    private void validar(Market m) {
        if (vazio(m.getName()) || vazio(m.getLocation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome e localização do supermercado são obrigatórios!");
        }
    }

    private boolean vazio(String s) { return s == null || s.isBlank(); }
}
