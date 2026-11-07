package com.Home_Management_System.Aplication_Api.repositories;

import com.Home_Management_System.Aplication_Api.models.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {

    // Consulta de domínio: Itens onde quantity <= minimumQuantity
    @Query("{ '$expr': { '$lte': [ '$quantity', '$minimumQuantity' ] } }")
    List<Inventory> findLowStock();

    // Consulta de domínio: Filtrar por local de armazenamento ("Despensa", "Geladeira")
    List<Inventory> findByStorageLocationIgnoreCase(String storageLocation);
}