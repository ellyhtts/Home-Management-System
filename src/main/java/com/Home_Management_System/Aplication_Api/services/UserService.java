package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    public User buscarPorId(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com o ID: " + id));
    }

    public User criar(User user) {
        // Regra de Negócio: Bloquear e-mail duplicado (409 Conflict)
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com o e-mail: " + user.getEmail());
        }
        return userRepository.save(user);
    }
}