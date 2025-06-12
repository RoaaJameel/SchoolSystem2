package com.example.schoolsystem2;

public class Student {
    private int studentId;
    private String name;
    private String gender;
    private String dob;
    private String gradeLevel;
    private String className;
    private String parentContact;

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setParentContact(String parentContact) {
        this.parentContact = parentContact;
    }

    // Constructor الكامل
    public Student(int studentId, String name, String gender, String dob, String gradeLevel, String className, String parentContact) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.gradeLevel = gradeLevel;
        this.className = className;
        this.parentContact = parentContact;
    }

    // ✅ Constructor مختصر (اختياري)
    public Student(int studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    // ✅ Getter المطلوب في adapter
    public int getId() {
        return studentId;
    }

    // Getters الأخرى
    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public String getClassName() {
        return className;
    }

    public String getParentContact() {
        return parentContact;
    }
}
