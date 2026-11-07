package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> listarTodos() {
        return ResponseEntity.ok(userService.listarTodos()); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(userService.buscarPorId(id)); // 200 OK
    }

    @PostMapping
    public ResponseEntity<User> criar(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.criar(user)); // 201 Created
    }
}