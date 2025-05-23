package com.example.gamestoreclient.utils;

import com.example.gamestoreclient.models.User;

public class SessionManager {
    private static volatile SessionManager instance;
    private User currentUser;

    private SessionManager() {
        this.currentUser = null;
    }

    // Double-checked locking for thread-safe singleton
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                    System.out.println("SessionManager instance created");
                }
            }
        }
        return instance;
    }

    public synchronized void setCurrentUser(User user) {
        String userName = (user != null && user.getName() != null) ? user.getName() :
                (user != null ? user.getEmail() : "null");
        System.out.println("SessionManager.setCurrentUser() called with: " +
                (user != null ? userName + " (" + user.getEmail() + ")" : "null"));
        this.currentUser = user;
        String storedUserName = (this.currentUser != null && this.currentUser.getName() != null) ?
                this.currentUser.getName() :
                (this.currentUser != null ? this.currentUser.getEmail() : "null");
        System.out.println("SessionManager.setCurrentUser() - User stored: " + storedUserName);
    }

    public synchronized User getCurrentUser() {
        String userName = (this.currentUser != null && this.currentUser.getName() != null) ?
                this.currentUser.getName() :
                (this.currentUser != null ? this.currentUser.getEmail() : "null");
        System.out.println("SessionManager.getCurrentUser() called - returning: " +
                (this.currentUser != null ? userName + " (" + this.currentUser.getEmail() + ")" : "null"));
        return this.currentUser;
    }

    public synchronized boolean isLoggedIn() {
        boolean loggedIn = this.currentUser != null;
        System.out.println("SessionManager.isLoggedIn() called - returning: " + loggedIn);
        return loggedIn;
    }

    public synchronized boolean isAdmin() {
        boolean admin = this.currentUser != null &&
                this.currentUser.getRole() != null &&
                this.currentUser.getRole().equalsIgnoreCase("ADMIN");
        System.out.println("SessionManager.isAdmin() called - returning: " + admin +
                " (user role: " + (this.currentUser != null ? this.currentUser.getRole() : "null") + ")");
        return admin;
    }

    public synchronized void logout() {
        System.out.println("SessionManager.logout() called - clearing user: " +
                (this.currentUser != null ? this.currentUser.getName() : "null"));
        this.currentUser = null;
        System.out.println("SessionManager.logout() - User cleared");
    }

    // Debug method to check SessionManager state
    public synchronized void debugState() {
        System.out.println("=== SessionManager Debug State ===");
        System.out.println("Instance: " + this);
        System.out.println("Current User: " + (this.currentUser != null ? this.currentUser.toString() : "null"));
        System.out.println("Is Logged In: " + isLoggedIn());
        System.out.println("Is Admin: " + isAdmin());
        System.out.println("=================================");
    }
}