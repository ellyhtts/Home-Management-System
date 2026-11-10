package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.Product;
import com.Home_Management_System.Aplication_Api.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> listar() {
        return ResponseEntity.ok(productService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> buscarPorId(@PathVariable String id) {
        Product product = productService.buscarPorId(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> criar(@RequestBody Product product) {
        Product novoProduto = productService.salvar(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
    }

    @PostMapping("/lote")
    public ResponseEntity<List<Product>> criarEmLote(@RequestBody List<Product> products) {
        List<Product> novosProdutos = productService.salvarEmLote(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(novosProdutos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> atualizar(@PathVariable String id, @RequestBody Product product) {
        Product produtoAtualizado = productService.atualizar(id, product);
        return ResponseEntity.ok(produtoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        productService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}