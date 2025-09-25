package dev.gauravgughane.code.auth.controller; // Correct package for controllers

import dev.gauravgughane.code.auth.entity.BaseUser; // Import the entity
import dev.gauravgughane.code.auth.service.UserService; // Import the service from its correct package
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Autowire the UserService from the service package
    @Autowired
    private UserService userService;

    // Endpoint to get the profile of the current user (simplified)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {

        return ResponseEntity.ok("User profile endpoint - protected by JWT");
    }

    // Endpoint to get all users in the current tenant
    // This relies on the TenantFilter setting the correct schema context
    @GetMapping // Maps to GET /api/users
    public ResponseEntity<?> getAllUsers() {
        try {
            List<BaseUser> users = userService.findAll(); // Use UserService method

            // Create a safe list to return (excluding password hashes)
            List<Object> safeUsers = users.stream().map(user -> {
                // Return an anonymous object with only safe fields
                // Using a record-like anonymous class structure
                return new Object() {
                    public final String id = user.getId() != null ? user.getId().toString() : null;
                    public final String name = user.getName();
                    public final String email = user.getEmail();
                    // passwordHash is intentionally omitted
                };
            }).collect(Collectors.toList());

            return ResponseEntity.ok(safeUsers);
        } catch (Exception e) {
            // Basic error handling
            return ResponseEntity.internalServerError().body("Error fetching users: " + e.getMessage());
        }
    }
}