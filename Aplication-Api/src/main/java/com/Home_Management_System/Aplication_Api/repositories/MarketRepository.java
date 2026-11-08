package com.Home_Management_System.Aplication_Api.repositories;

import com.Home_Management_System.Aplication_Api.models.Market;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarketRepository extends MongoRepository<Market, String> {
    boolean existsByCnpj(String cnpj);
    Optional<Market> findByCnpj(String cnpj);
}
