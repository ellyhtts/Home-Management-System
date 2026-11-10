package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Transaction;
import com.Home_Management_System.Aplication_Api.services.TransactionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> criar(@RequestBody Transaction transaction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.criar(transaction));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> listarTodas() {
        return ResponseEntity.ok(transactionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.buscarPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Transaction>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(transactionService.buscarPorTipo(tipo));
    }

    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<Transaction>> buscarPorOrcamento(@PathVariable String budgetId) {
        return ResponseEntity.ok(transactionService.buscarPorOrcamento(budgetId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> atualizar(@PathVariable String id, @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.atualizar(id, transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        transactionService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}