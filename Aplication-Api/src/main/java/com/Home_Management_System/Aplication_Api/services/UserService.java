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

    public List<User> salvarEmLote(List<User> users) {
        if (users == null || users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista de utilizadores não pode estar vazia!");
        }

        for (User user : users) {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O e-mail do utilizador é obrigatório!");
            }

            // Valida se já existe um utilizador cadastrado com este e-mail
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um utilizador cadastrado com o e-mail: " + user.getEmail());
            }
        }

        return userRepository.saveAll(users);
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    public User buscarPorId(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado com o ID: " + id));
    }

    public User criar(User user) {
        validarCamposObrigatorios(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Já existe um usuário com o e-mail: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User atualizar(String id, User dados) {
        User existente = buscarPorId(id);

        validarCamposObrigatorios(dados);


        userRepository.findByEmail(dados.getEmail()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Já existe um usuário com o e-mail: " + dados.getEmail());
            }
        });

        existente.setName(dados.getName());
        existente.setEmail(dados.getEmail());
        existente.setPassword(dados.getPassword());
        existente.setFamilyRole(dados.getFamilyRole());
        existente.setAddressUser(dados.getAddressUser());
        if (dados.getActive() != null) {
            existente.setActive(dados.getActive());
        }

        return userRepository.save(existente);
    }

    public void deletar(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validarCamposObrigatorios(User user) {
        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nome, e-mail e senha são obrigatórios!");
        }
    }
}