package com.example.schoolsystem2;

import java.util.ArrayList;

public class Course {
    private int courseId;
    private String courseName;
    private ArrayList<String> gradeLevels;
    private ArrayList<String> classes;
    private ArrayList<String> teachers;

    public Course(int courseId, String courseName,
                  ArrayList<String> gradeLevels,
                  ArrayList<String> classes,
                  ArrayList<String> teachers) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.gradeLevels = gradeLevels;
        this.classes = classes;
        this.teachers = teachers;
    }

    public Course(String courseName, ArrayList<String> gradeLevels, ArrayList<String> classes, ArrayList<String> teachers) {
        this.courseName = courseName;
        this.gradeLevels = gradeLevels;
        this.classes = classes;
        this.teachers = teachers;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public ArrayList<String> getGradeLevels() {
        return gradeLevels;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public ArrayList<String> getTeachers() {
        return teachers;
    }

}
