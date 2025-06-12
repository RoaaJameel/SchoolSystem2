package com.example.schoolsystem2;

import java.util.List;

public class Course {
    private String courseName;
    private List<String> gradeLevels;
    private List<String> classes;
    private List<String> teachers;

    public Course(String courseName, List<String> gradeLevels, List<String> classes, List<String> teachers) {
        this.courseName = courseName;
        this.gradeLevels = gradeLevels;
        this.classes = classes;
        this.teachers = teachers;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<String> getGradeLevels() {
        return gradeLevels;
    }

    public List<String> getClasses() {
        return classes;
    }

    public List<String> getTeachers() {
        return teachers;
    }
}
