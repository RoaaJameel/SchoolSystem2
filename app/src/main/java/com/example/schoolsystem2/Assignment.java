package com.example.schoolsystem2;

public class Assignment {

    private int assignmentId;
    private String title;
    private int classId;

    public Assignment(int assignmentId, String title, int classId) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.classId = classId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public String getTitle() {
        return title;
    }

    public int getClassId() {
        return classId;
    }

    @Override
    public String toString() {
        return title;
    }
}