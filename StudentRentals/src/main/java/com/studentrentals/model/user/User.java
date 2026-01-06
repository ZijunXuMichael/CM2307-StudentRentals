package com.studentrentals.model.user;

public abstract class User {
    private final String userId;
    private String name;
    private String email;
    private String passwordHash;

    protected User(String userId, String name, String email, String passwordHash) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public abstract String getRole(); // "Student" / "Homeowner"
}
