package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Market;
import com.Home_Management_System.Aplication_Api.services.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/markets")
public class MarketController {

    @Autowired
    private MarketService marketService;


    @GetMapping
    public ResponseEntity<List<Market>> listarTodos() {
        return ResponseEntity.ok(marketService.listarTodos());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Market> buscarPorId(@PathVariable String id) {
        Market market = marketService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supermercado não encontrado!"));
        return ResponseEntity.ok(market);
    }


    @PostMapping
    public ResponseEntity<Market> criar(@RequestBody Market market) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marketService.salvar(market));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Market> atualizar(@PathVariable String id, @RequestBody Market market) {
        return ResponseEntity.ok(marketService.atualizar(id, market));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        marketService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}