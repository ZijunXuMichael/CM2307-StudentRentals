package com.studentrentals.service;

import com.studentrentals.model.user.Homeowner;
import com.studentrentals.model.user.Student;
import com.studentrentals.model.user.User;
import com.studentrentals.repository.UserRepository;
import com.studentrentals.util.IdGenerator;
import com.studentrentals.util.PasswordHasher;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Student registerStudent(String name, String email, String plainPassword,
                                   String university, String studentId) {
        validateCommon(name, email, plainPassword);

        if (university == null || university.isBlank()) throw new IllegalArgumentException("University is required");
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("Student ID is required");

        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String userId = IdGenerator.newId("stu");
        String hash = PasswordHasher.sha256(plainPassword);

        Student student = new Student(
                userId,
                name.trim(),
                email.trim(),
                hash,
                university.trim(),
                studentId.trim()
        );
        userRepository.save(student);
        return student;
    }

    public Homeowner registerHomeowner(String name, String email, String plainPassword,
                                       String contactNumber) {
        validateCommon(name, email, plainPassword);

        if (contactNumber == null || contactNumber.isBlank()) {
            throw new IllegalArgumentException("Contact number is required");
        }

        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String userId = IdGenerator.newId("own");
        String hash = PasswordHasher.sha256(plainPassword);

        Homeowner owner = new Homeowner(
                userId,
                name.trim(),
                email.trim(),
                hash,
                contactNumber.trim()
        );
        userRepository.save(owner);
        return owner;
    }

    public User authenticate(String email, String plainPassword) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (plainPassword == null || plainPassword.isBlank()) throw new IllegalArgumentException("Password is required");

        return userRepository.findByEmail(email)
                .filter(u -> u.getPasswordHash().equals(PasswordHasher.sha256(plainPassword)))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }

    private void validateCommon(String name, String email, String plainPassword) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (plainPassword == null || plainPassword.isBlank()) throw new IllegalArgumentException("Password is required");
    }
}
