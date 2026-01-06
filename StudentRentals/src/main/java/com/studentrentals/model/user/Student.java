package com.studentrentals.model.user;

public class Student extends User {
    private final String university;
    private final String studentId;

    public Student(String userId, String name, String email, String passwordHash,
                   String university, String studentId) {
        super(userId, name, email, passwordHash);
        this.university = university;
        this.studentId = studentId;
    }

    public String getUniversity() { return university; }
    public String getStudentId() { return studentId; }

    @Override
    public String getRole() { return "Student"; }
}