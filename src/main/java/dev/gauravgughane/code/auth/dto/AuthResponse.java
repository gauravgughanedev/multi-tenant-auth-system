package dev.gauravgughane.code.auth.dto;

public class AuthResponse {
    private String message;
    private String token; // For JWT later

    public AuthResponse(String message) {
        this.message = message;
    }

    // Getters & Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}