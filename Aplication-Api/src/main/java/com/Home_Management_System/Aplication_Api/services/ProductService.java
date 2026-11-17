package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Product;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ActivityLogService activityLogService;

    public List<Product> listarTodos() { return productRepository.findAll(); }

    public Optional<Product> buscarPorId(String id) { return productRepository.findById(id); }

    public List<Product> buscarPorCategoria(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!");
        }
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null && categoryId.equals(p.getCategory().getId()))
                .toList();
    }

    public Product salvar(Product p) {
        validarEPreencher(p);
        if (productRepository.existsByNameIgnoreCase(p.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um produto com este nome!");
        }
        Product salvo = productRepository.save(p);
        activityLogService.registrar("Cadastrou o produto '" + p.getName() + "' na categoria '"
                + nomeCategoria(p) + "'");
        return salvo;
    }

    public List<Product> salvarLote(List<Product> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A lista de produtos não pode ser vazia!");
        }
        Set<String> nomesNoLote = new HashSet<>();
        for (Product p : produtos) {
            if (p == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista contém um item vazio!");
            }
            validarEPreencher(p);
            if (!nomesNoLote.add(p.getName().trim().toLowerCase())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Nome repetido dentro do lote: " + p.getName());
            }
            if (productRepository.existsByNameIgnoreCase(p.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Já existe um produto com o nome: " + p.getName());
            }
        }
        List<Product> salvos = new ArrayList<>(productRepository.saveAll(produtos));
        activityLogService.registrar("Cadastrou " + produtos.size() + " produto(s) em lote: " + resumoNomes(produtos));
        return salvos;
    }

    public Product atualizar(String id, Product dados) {
        Product existente = productRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado!"));
        validarEPreencher(dados);
        productRepository.findByNameIgnoreCase(dados.getName()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um produto com este nome!");
            }
        });
        existente.setName(dados.getName());
        existente.setCategory(dados.getCategory());
        Product salvo = productRepository.save(existente);
        activityLogService.registrar("Atualizou o produto '" + existente.getName() + "' (categoria '"
                + nomeCategoria(existente) + "')");
        return salvo;
    }

    public void deletar(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado!");
        }
        String nome = productRepository.findById(id).map(Product::getName).orElse("sem nome");
        productRepository.deleteById(id);
        activityLogService.registrar("Excluiu o produto '" + nome + "'");
    }

    private String nomeCategoria(Product p) {
        return p.getCategory() != null && p.getCategory().getName() != null
                ? p.getCategory().getName() : "sem categoria";
    }

    private String resumoNomes(List<Product> produtos) {
        String nomes = String.join(", ", produtos.stream().map(Product::getName).limit(5).toList());
        return produtos.size() > 5 ? nomes + " e mais " + (produtos.size() - 5) : nomes;
    }

    private void validarEPreencher(Product p) {
        if (p.getName() == null || p.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do produto é obrigatório!");
        }
        if (p.getCategory() == null || p.getCategory().getId() == null || p.getCategory().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A categoria (com id) é obrigatória!");
        }
        p.setCategory(categoryRepository.findById(p.getCategory().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada!")));
    }
}
