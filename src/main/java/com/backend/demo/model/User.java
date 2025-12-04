package com.backend.demo.model;

public class User {

    private final String name;
    private final String email;
    private final String password; // hashed password
    private final String role;     // ATTENDEE or ORGANIZER

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
