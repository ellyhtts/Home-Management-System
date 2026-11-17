package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Inventory;
import com.Home_Management_System.Aplication_Api.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<Inventory>> listarTodos() {
        return ResponseEntity.ok(inventoryService.listarTodos());
    }

    @PostMapping("/low-stock")
    public ResponseEntity<List<Inventory>> estoqueBaixo(
            @RequestParam Integer max,
            @RequestParam(defaultValue = "false") boolean adicionarNaLista,
            @RequestParam(required = false) String userId) {
        return ResponseEntity.ok(inventoryService.estoqueBaixo(max, adicionarNaLista, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> buscarPorId(@PathVariable String id) {
        Inventory i = inventoryService.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de estoque não encontrado!"));
        return ResponseEntity.ok(i);
    }

    @PostMapping
    public ResponseEntity<Inventory> criar(@RequestBody Inventory inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.salvar(inventory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> atualizar(@PathVariable String id, @RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.atualizar(id, inventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        inventoryService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
