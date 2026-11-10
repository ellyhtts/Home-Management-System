package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> listarTodas() {
        return categoryRepository.findAll();
    }

    public Category buscarPorId(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!"));
    }

    public Category salvar(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome da categoria é obrigatório!");
        }
        return categoryRepository.save(category);
    }

    public List<Category> salvarEmLote(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista de categorias não pode estar vazia!");
        }
        return categoryRepository.saveAll(categories);
    }

    public void deletar(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!");
        }
        categoryRepository.deleteById(id);
    }
}