package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.ShoppingList;
import com.Home_Management_System.Aplication_Api.services.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;


    @GetMapping
    public ResponseEntity<List<ShoppingList>> listarTodas() {
        return ResponseEntity.ok(shoppingListService.listarTodas());
    }


    @GetMapping("/{id}")
    public ResponseEntity<ShoppingList> buscarPorId(@PathVariable String id) {
        ShoppingList lista = shoppingListService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lista de compras não encontrada!"));
        return ResponseEntity.ok(lista);
    }


    @PostMapping
    public ResponseEntity<ShoppingList> criar(@RequestBody ShoppingList shoppingList) {
        ShoppingList nova = shoppingListService.salvar(shoppingList);
        return ResponseEntity.status(HttpStatus.CREATED).body(nova);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ShoppingList> atualizar(@PathVariable String id,
                                                  @RequestBody ShoppingList shoppingList) {
        return ResponseEntity.ok(shoppingListService.atualizar(id, shoppingList));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        shoppingListService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}