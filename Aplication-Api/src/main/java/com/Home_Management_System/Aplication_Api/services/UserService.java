package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.AddressUser;
import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogService activityLogService;

    public List<User> listarTodos() { return userRepository.findAll(); }

    public Optional<User> buscarPorId(String id) { return userRepository.findById(id); }

    public User salvar(User u) {
        validar(u);
        if (userRepository.existsByEmail(u.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
        }
        User salvo = userRepository.save(u);
        // no cadastro ainda não há cabeçalho: o próprio usuário criado é quem aparece no log
        activityLogService.registrar("Cadastrou o usuário '" + u.getName() + "' no sistema",
                salvo != null ? salvo.getId() : null);
        return salvo;
    }

    public User atualizar(String id, User dados) {
        User existente = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!"));
        validar(dados);
        userRepository.findByEmail(dados.getEmail()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
            }
        });
        existente.setName(dados.getName());
        existente.setEmail(dados.getEmail());
        existente.setPassword(dados.getPassword());
        existente.setAddressUser(dados.getAddressUser());
        User salvo = userRepository.save(existente);
        activityLogService.registrar("Atualizou os dados do usuário '" + existente.getName() + "'", id);
        return salvo;
    }

    public void deletar(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!");
        }
        String nome = userRepository.findById(id).map(User::getName).orElse("sem nome");
        userRepository.deleteById(id);
        activityLogService.registrar("Excluiu o usuário '" + nome + "'");
    }

    private void validar(User u) {
        if (vazio(u.getName()) || vazio(u.getEmail()) || vazio(u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome, e-mail e senha são obrigatórios!");
        }
        if (!u.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail em formato inválido!");
        }
        AddressUser a = u.getAddressUser();
        if (a == null || vazio(a.getCity()) || vazio(a.getDistrict()) || vazio(a.getStreet())
                || a.getHouseNumber() == null || a.getHouseNumber() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Endereço completo (city, district, street, houseNumber) é obrigatório!");
        }
    }

    private boolean vazio(String s) { return s == null || s.isBlank(); }
}
