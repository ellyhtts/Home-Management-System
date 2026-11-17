package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Product;
import com.Home_Management_System.Aplication_Api.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> listarTodos() {
        return ResponseEntity.ok(productService.listarTodos());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> buscarPorCategoria(@PathVariable String categoryId) {
        return ResponseEntity.ok(productService.buscarPorCategoria(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> buscarPorId(@PathVariable String id) {
        Product p = productService.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado!"));
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<Product> criar(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.salvar(product));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Product>> criarLote(@RequestBody List<Product> products) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.salvarLote(products));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> atualizar(@PathVariable String id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.atualizar(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        productService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}