package com.example.gamestoreclient.models;

public class LoginRequest {
    // Make fields public or provide public getters/setters
    private String email;
    private String password;

    // Public getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // No-args constructor required by FXML
    public LoginRequest() {
    }

    // Convenience constructor
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
