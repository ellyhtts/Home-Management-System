package com.Home_Management_System.Aplication_Api.repositories;

import com.Home_Management_System.Aplication_Api.models.ShoppingList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingListRepository extends MongoRepository<ShoppingList, String> {
}