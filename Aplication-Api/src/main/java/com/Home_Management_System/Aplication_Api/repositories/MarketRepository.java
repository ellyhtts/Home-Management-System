package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.Market;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MarketRepository extends MongoRepository<Market, String> {}