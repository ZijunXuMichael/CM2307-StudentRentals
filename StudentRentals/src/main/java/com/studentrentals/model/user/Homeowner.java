package com.studentrentals.model.user;

public class Homeowner extends User {
    private final String contactNumber;

    public Homeowner(String userId, String name, String email, String passwordHash,
                     String contactNumber) {
        super(userId, name, email, passwordHash);
        this.contactNumber = contactNumber;
    }

    public String getContactNumber() { return contactNumber; }

    @Override
    public String getRole() { return "Homeowner"; }
}
