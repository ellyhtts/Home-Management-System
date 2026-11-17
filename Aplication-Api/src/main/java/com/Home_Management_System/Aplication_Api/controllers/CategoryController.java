package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> listarTodas() {
        return ResponseEntity.ok(categoryService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> buscarPorId(@PathVariable String id) {
        Category c = categoryService.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!"));
        return ResponseEntity.ok(c);
    }

    @PostMapping
    public ResponseEntity<Category> criar(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.salvar(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> atualizar(@PathVariable String id, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.atualizar(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        categoryService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}