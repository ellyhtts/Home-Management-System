package com.Home_Management_System.Aplication_Api.repositories;

import com.Home_Management_System.Aplication_Api.models.Transaction;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByTipoIgnoreCase(String tipo);
    List<Transaction> findByBudgetId(String budgetId);
    List<Transaction> findByPurchaseId(String purchaseId);
}
