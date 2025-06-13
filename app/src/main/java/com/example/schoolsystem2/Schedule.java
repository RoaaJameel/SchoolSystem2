package com.example.schoolsystem2;

public class Schedule {
    public int scheduleId;
    public String className;
    public String courseName;
    public String teacherName;
    public String day;
    public String startTime;
    public String endTime;

    public Schedule(int scheduleId, String className, String courseName,
                    String teacherName, String day, String startTime, String endTime) {
        this.scheduleId = scheduleId;
        this.className = className;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
