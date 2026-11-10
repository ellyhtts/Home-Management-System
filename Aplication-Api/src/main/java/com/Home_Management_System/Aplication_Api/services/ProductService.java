package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Product;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    public Product buscarPorId(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado!"));
    }

    public Product salvar(Product product) {
        if (product.getName() != null && productRepository.findByName(product.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um produto cadastrado com este nome!");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto deve estar associado a uma categoria válida!");
        }

        categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A categoria informada não existe no banco de dados!"));

        return productRepository.save(product);
    }

    public List<Product> salvarEmLote(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista de produtos não pode estar vazia!");
        }

        for (Product product : products) {
            if (product.getName() == null || product.getName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do produto é obrigatório!");
            }
            if (product.getCategory() == null || product.getCategory().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O produto deve estar associado a uma categoria válida!");
            }

            // Valida se a categoria informada existe no banco
            categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "A categoria informada não existe!"));
        }

        return productRepository.saveAll(products);
    }

    public Product atualizar(String id, Product produtoAtualizado) {
        Product produtoExistente = buscarPorId(id);

        produtoExistente.setName(produtoAtualizado.getName());
        produtoExistente.setCategory(produtoAtualizado.getCategory());

        return productRepository.save(produtoExistente);
    }

    public void deletar(String id) {
        Product produto = buscarPorId(id);
        productRepository.delete(produto);
    }
}