package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface ProductRepository extends MongoRepository<Product, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Product> findByNameIgnoreCase(String name);
}