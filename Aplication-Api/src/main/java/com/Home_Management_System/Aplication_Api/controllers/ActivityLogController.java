package com.Home_Management_System.Aplication_Api.controllers;

import com.Home_Management_System.Aplication_Api.models.ActivityLog;
import com.Home_Management_System.Aplication_Api.services.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> listarTodos() {
        return ResponseEntity.ok(activityLogService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> buscarPorId(@PathVariable String id) {
        ActivityLog log = activityLogService.buscarPorId(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Log não encontrado!"));
        return ResponseEntity.ok(log);
    }

    @PostMapping
    public ResponseEntity<ActivityLog> criar(@RequestBody ActivityLog log) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityLogService.salvar(log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        activityLogService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}