package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.ShoppingList;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ShoppingListRepository extends MongoRepository<ShoppingList, String> {}