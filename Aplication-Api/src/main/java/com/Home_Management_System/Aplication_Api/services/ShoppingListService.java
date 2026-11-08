package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.ShoppingList;
import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.ShoppingListRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListService {

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ShoppingList> listarTodas() {
        return shoppingListRepository.findAll();
    }

    public Optional<ShoppingList> buscarPorId(String id) {
        return shoppingListRepository.findById(id);
    }

    public ShoppingList salvar(ShoppingList shoppingList) {
        validarEPreencherCriador(shoppingList);
        if (shoppingList.getDataCriacao() == null) {
            shoppingList.setDataCriacao(LocalDateTime.now());
        }
        return shoppingListRepository.save(shoppingList);
    }

    public ShoppingList atualizar(String id, ShoppingList dados) {
        ShoppingList existente = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lista de compras não encontrada!"));

        validarEPreencherCriador(dados);

        existente.setTitulo(dados.getTitulo());
        existente.setStatus(dados.getStatus());
        existente.setCriador(dados.getCriador());
        existente.setItens(dados.getItens());

        return shoppingListRepository.save(existente);
    }

    public void deletar(String id) {
        if (!shoppingListRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Lista de compras não encontrada!");
        }
        shoppingListRepository.deleteById(id);
    }

    private void validarEPreencherCriador(ShoppingList lista) {
        if (lista.getCriador() == null || lista.getCriador().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O criador da lista e o ID do utilizador são obrigatórios.");
        }

        User criador = userRepository.findById(lista.getCriador().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Utilizador com o ID " + lista.getCriador().getId() + " não foi encontrado."));

        lista.setCriador(criador);
    }
}