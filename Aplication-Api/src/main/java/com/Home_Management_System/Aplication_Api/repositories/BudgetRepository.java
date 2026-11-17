package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
public interface BudgetRepository extends MongoRepository<Budget, String> {
    boolean existsByMonthIgnoreCase(String month);
    Optional<Budget> findByMonthIgnoreCase(String month);
}