package com.Home_Management_System.Aplication_Api.repositories;

import com.Home_Management_System.Aplication_Api.models.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findByUserIdOrderByTimestampDesc(String userId);
}