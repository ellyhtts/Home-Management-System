package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Inventory;
import com.Home_Management_System.Aplication_Api.services.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<Inventory> criar(@RequestBody Inventory inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.salvar(inventory));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<Inventory>> criarEmLote(@RequestBody List<Inventory> inventories) {
        List<Inventory> novoInventario = inventoryService.salvarEmLote(inventories);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoInventario);
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> listarTodos() {
        return ResponseEntity.ok(inventoryService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(inventoryService.buscarPorId(id));
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Inventory>> listarEstoqueBaixo() {
        return ResponseEntity.ok(inventoryService.buscarEstoqueBaixo());
    }

    @GetMapping("/local/{local}")
    public ResponseEntity<List<Inventory>> listarPorLocal(@PathVariable String local) {
        return ResponseEntity.ok(inventoryService.buscarPorLocal(local));
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