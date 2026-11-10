package com.Home_Management_System.Aplication_Api.repositories;

import com.Home_Management_System.Aplication_Api.models.Budget;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findByMesReferenciaAndAnoReferencia(Integer mesReferencia, Integer anoReferencia);
    List<Budget> findByCategoryId(String categoryId);
    Boolean existsByCategoryIdAndMesReferenciaAndAnoReferencia(String categoryId, Integer mesReferencia, Integer anoReferencia);
}