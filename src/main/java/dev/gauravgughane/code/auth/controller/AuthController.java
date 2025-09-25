package dev.gauravgughane.code.auth.controller;

// Correct imports
import dev.gauravgughane.code.auth.config.TenantContext;
import dev.gauravgughane.code.auth.dto.AuthRequest;
import dev.gauravgughane.code.auth.entity.BaseUser;
import dev.gauravgughane.code.auth.service.JwtService;
import dev.gauravgughane.code.auth.service.UserService; // ✅ Correct import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // ✅ Corrected: Use 'UserService userService', not 'UserService.UserService userService'
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            BaseUser user = userService.registerUser(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword()
            );
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<BaseUser> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            BaseUser user = userOpt.get();
            if (userService.checkPassword(user, request.getPassword())) {
                // Get tenant ID from TenantContext (set by TenantFilter)
                String tenantId = TenantContext.getTenantId();

                // Generate JWT token
                String token = jwtService.generateToken(user.getId().toString(), tenantId);

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("expiresIn", 3600);
                response.put("user", Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail()
                ));
                return ResponseEntity.ok(response);
            }
        }
        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid email or password");
        return ResponseEntity.status(401).body(error);
    }
}