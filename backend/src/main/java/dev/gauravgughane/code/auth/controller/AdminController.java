package dev.gauravgughane.code.auth.controller;

import dev.gauravgughane.code.auth.entity.BaseUser;
import dev.gauravgughane.code.auth.service.UserService;
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

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<BaseUser> users = userService.findAllUsers();

            List<Object> safeUsers = users.stream().map(user -> {
                return new Object() {
                    public final String id = user.getId().toString();
                    public final String name = user.getName();
                    public final String email = user.getEmail();
                };
            }).collect(Collectors.toList());

            return ResponseEntity.ok(safeUsers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching users: " + e.getMessage());
        }
    }
}