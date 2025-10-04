package dev.gauravgughane.code.auth.controller;

import dev.gauravgughane.code.auth.config.TenantContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/tenant-info")
    public Map<String, Object> getTenantInfo() {
        Map<String, Object> response = new HashMap<>();
        String tenant = TenantContext.getTenantId();
        response.put("currentTenant", tenant);
        response.put("message", "Hello from tenant: " + tenant);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /// ////
    ///
    @GetMapping("/hello")
    public String hello() {
        return "Hello, GUI is coming!";
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("tenant", TenantContext.getTenantId());
        return response;
    }
}
