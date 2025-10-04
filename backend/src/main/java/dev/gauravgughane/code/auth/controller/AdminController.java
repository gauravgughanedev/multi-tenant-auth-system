package dev.gauravgughane.code.auth.controller;

import dev.gauravgughane.code.auth.service.UserService;
import dev.gauravgughane.code.auth.entity.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // GET /api/admin/users → returns list of users in current tenant
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            System.out.println("Fetching all users...");
            List<BaseUser> users = userService.findAllUsers();
            System.out.println("Found " + users.size() + " users");

            // Log the first user if any
            if (!users.isEmpty()) {
                BaseUser firstUser = users.get(0);
                System.out.println("First user: " + firstUser.getName() + " - " + firstUser.getEmail());
            }

            // Remove password hashes from response for security
            List<Object> safeUsers = users.stream().map(user -> {
                return new Object() {
                    public final String id = user.getId().toString();
                    public final String name = user.getName();
                    public final String email = user.getEmail();
                };
            }).collect(Collectors.toList());

            return ResponseEntity.ok(safeUsers);
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching users: " + e.getMessage());
        }
    }
}