package dev.gauravgughane.code.auth.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class BaseUser {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String email;

    @Column(name = "password_hash") // ✅ Field name used in UserService.checkPassword
    private String passwordHash;

    // Constructors
    public BaseUser() {}

    public BaseUser(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters & Setters - ✅ These are CRUCIAL for UserService to work
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; } // ✅ Used in checkPassword
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}