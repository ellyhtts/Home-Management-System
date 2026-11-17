package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ActivityLogService activityLogService;

    public List<Category> listarTodas() { return categoryRepository.findAll(); }

    public Optional<Category> buscarPorId(String id) { return categoryRepository.findById(id); }

    public Category salvar(Category c) {
        validar(c);
        if (categoryRepository.existsByNameIgnoreCase(c.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma categoria com este nome!");
        }
        Category salva = categoryRepository.save(c);
        activityLogService.registrar("Criou a categoria '" + c.getName() + "'");
        return salva;
    }

    public Category atualizar(String id, Category dados) {
        Category existente = categoryRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!"));
        validar(dados);
        categoryRepository.findByNameIgnoreCase(dados.getName()).ifPresent(outra -> {
            if (!outra.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma categoria com este nome!");
            }
        });
        String nomeAnterior = existente.getName();
        existente.setName(dados.getName());
        Category salva = categoryRepository.save(existente);
        if (nomeAnterior != null && !nomeAnterior.equals(existente.getName())) {
            activityLogService.registrar("Renomeou a categoria '" + nomeAnterior + "' para '" + existente.getName() + "'");
        } else {
            activityLogService.registrar("Atualizou a categoria '" + existente.getName() + "'");
        }
        return salva;
    }

    public void deletar(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!");
        }
        boolean emUso = productRepository.findAll().stream()
                .anyMatch(p -> p.getCategory() != null && id.equals(p.getCategory().getId()));
        if (emUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível excluir: existem produtos nesta categoria!");
        }
        String nome = categoryRepository.findById(id).map(Category::getName).orElse("sem nome");
        categoryRepository.deleteById(id);
        activityLogService.registrar("Excluiu a categoria '" + nome + "'");
    }

    private void validar(Category c) {
        if (c.getName() == null || c.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da categoria é obrigatório!");
        }
    }
}
