package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface CategoryRepository extends MongoRepository<Category, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Category> findByNameIgnoreCase(String name);
}