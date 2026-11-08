package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Purchase;
import com.Home_Management_System.Aplication_Api.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<Purchase>> listarTodas() {
        return ResponseEntity.ok(purchaseService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Purchase> buscarPorId(@PathVariable String id) {
        Purchase compra = purchaseService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compra não encontrada!"));
        return ResponseEntity.ok(compra);
    }

    @PostMapping
    public ResponseEntity<Purchase> criar(@RequestBody Purchase purchase) {
        Purchase nova = purchaseService.salvar(purchase);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Purchase> atualizar(@PathVariable String id,
                                              @RequestBody Purchase purchase) {
        return ResponseEntity.ok(purchaseService.atualizar(id, purchase));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        purchaseService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}