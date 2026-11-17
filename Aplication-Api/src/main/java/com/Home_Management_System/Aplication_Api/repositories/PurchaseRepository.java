package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.Purchase;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface PurchaseRepository extends MongoRepository<Purchase, String> {}