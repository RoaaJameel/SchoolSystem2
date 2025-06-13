package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ScheduleDashboardActivity extends AppCompatActivity {

    CardView cardCreateStudent, cardCreateTeacher, cardViewStudent, cardViewTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_dashboard);

        cardCreateStudent = findViewById(R.id.cardCreateStudent);
        cardCreateTeacher = findViewById(R.id.cardCreateTeacher);
        cardViewStudent = findViewById(R.id.cardViewStudent);
        cardViewTeacher = findViewById(R.id.cardViewTeacher);

        cardCreateStudent.setOnClickListener(v ->
                startActivity(new Intent(this, CreateStudentScheduleActivity.class)));

        cardCreateTeacher.setOnClickListener(v ->
                startActivity(new Intent(this, CreateTeacherScheduleActivity.class)));

        cardViewStudent.setOnClickListener(v ->
                startActivity(new Intent(this, StudentScheduleActivity.class)));

        cardViewTeacher.setOnClickListener(v ->
                startActivity(new Intent(this, TeacherScheduleActivity.class)));
    }
}