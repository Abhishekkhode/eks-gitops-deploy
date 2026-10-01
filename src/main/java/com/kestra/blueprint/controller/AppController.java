package com.kestra.blueprint.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppController {

    private final Instant startTime = Instant.now();

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.blueprint:Kestra EKS Pipeline}")
    private String blueprintName;

    @Value("${app.author:Abhishek Khode}")
    private String author;

    // Endpoints
    @GetMapping({"", "/", "/hello"})
    public ResponseEntity<Map<String, Object>> helloWorld() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("message", "Service running successfully");
        response.put("timestamp", Instant.now().toString());
        response.put("endpoints", Map.of(
            "health", "/actuator/health",
            "info", "/api/info",
            "system", "/api/system",
            "status", "/api/status"
        ));
        return ResponseEntity.ok(response);
    }

    // Metadata
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("application", "eks-gitops-deploy");
        response.put("version", appVersion);
        response.put("blueprint", blueprintName);
        response.put("author", author);
        response.put("platform", "AWS EKS");
        return ResponseEntity.ok(response);
    }

    // System telemetry
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemTelemetry() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptimeSeconds = runtimeBean.getUptime() / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;

        String formattedUptime = String.format("%dh %dm %ds", hours, minutes, seconds);

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        // Pod metadata
        String podName = System.getenv().getOrDefault("HOSTNAME", "local");
        String podIp = System.getenv().getOrDefault("POD_IP", "127.0.0.1");
        String nodeName = System.getenv().getOrDefault("NODE_NAME", "local-node");
        String awsRegion = System.getenv().getOrDefault("AWS_REGION", System.getenv().getOrDefault("AWS_DEFAULT_REGION", "ap-south-2"));
        String k8sNamespace = System.getenv().getOrDefault("POD_NAMESPACE", "production");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
                .withZone(ZoneId.of("UTC"));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "HEALTHY");
        response.put("currentUtcTime", formatter.format(Instant.now()));
        response.put("serverStartTime", formatter.format(startTime));
        response.put("uptime", formattedUptime);

        Map<String, Object> hostInfo = new LinkedHashMap<>();
        hostInfo.put("region", awsRegion);
        hostInfo.put("namespace", k8sNamespace);
        hostInfo.put("pod", podName);
        hostInfo.put("podIp", podIp);
        hostInfo.put("node", nodeName);
        response.put("host", hostInfo);

        Map<String, Object> jvmStats = new LinkedHashMap<>();
        jvmStats.put("javaVersion", System.getProperty("java.version"));
        jvmStats.put("usedMemoryMb", usedMemory);
        jvmStats.put("totalMemoryMb", totalMemory);
        response.put("jvm", jvmStats);

        return ResponseEntity.ok(response);
    }

    // Status summary
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "eks-gitops-deploy");
        response.put("health", "HEALTHY");
        response.put("timestamp", Instant.now().toEpochMilli());
        return ResponseEntity.ok(response);
    }
}
