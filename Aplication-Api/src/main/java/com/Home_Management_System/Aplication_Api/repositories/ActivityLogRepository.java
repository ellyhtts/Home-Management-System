package com.Home_Management_System.Aplication_Api.repositories;
import com.Home_Management_System.Aplication_Api.models.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {}