package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Budget;
import com.Home_Management_System.Aplication_Api.services.BudgetService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<Budget> criar(@RequestBody Budget budget) {
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.criar(budget));
    }

    @GetMapping
    public ResponseEntity<List<Budget>> listarTodos() {
        return ResponseEntity.ok(budgetService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(budgetService.buscarPorId(id));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Budget>> buscarPorPeriodo(@RequestParam Integer mes, @RequestParam Integer ano) {
        return ResponseEntity.ok(budgetService.buscarPorPeriodo(mes, ano));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> atualizar(@PathVariable String id, @RequestBody Budget budget) {
        return ResponseEntity.ok(budgetService.atualizar(id, budget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        budgetService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}