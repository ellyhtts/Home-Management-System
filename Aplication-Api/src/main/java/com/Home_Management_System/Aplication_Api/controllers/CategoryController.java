package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/lote")
    public ResponseEntity<List<Category>> criarEmLote(@RequestBody List<Category> categories) {
        List<Category> novasCategorias = categoryService.salvarEmLote(categories);
        return ResponseEntity.status(HttpStatus.CREATED).body(novasCategorias);
    }

    @GetMapping
    public ResponseEntity<List<Category>> listar() {
        List<Category> categorias = categoryService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

}