package com.kestra.blueprint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "Kestra SRE Spring Boot Demo");
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("availableEndpoints", Map.of(
            "healthCheck", "/actuator/health",
            "hello", "/api/hello",
            "introduction", "/api/info",
            "systemTelemetry", "/api/system",
            "clusterStatus", "/api/status"
        ));
        return ResponseEntity.ok(response);
    }
}
