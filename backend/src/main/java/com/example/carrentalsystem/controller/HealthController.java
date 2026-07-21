package com.example.carrentalsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lightweight, public health endpoint. Used by the keep-alive scheduler (and
 * any external uptime monitor) to keep the free-tier instance awake. It does
 * NOT touch the database, so the database is free to auto-suspend on its own.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
