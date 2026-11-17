package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    List<Inventory> findByQuantityLessThanEqual(Integer quantity);
}