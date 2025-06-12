package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {

    Button btnSendAssignment, btnSendMarks, btnRecordAttendance, btnSendMemo, btnContactParents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher); // make sure to rename the layout file accordingly

        btnSendAssignment = findViewById(R.id.btnSendAssignment);
        btnSendMarks = findViewById(R.id.btnSendMarks);
        btnRecordAttendance = findViewById(R.id.btnRecordAttendance);
        btnSendMemo = findViewById(R.id.btnSendMemo);
        btnContactParents = findViewById(R.id.btnContactParents);

        btnSendAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, SendAssignmentActivity.class);
            startActivity(intent);
        });

        /*
        btnSendMarks.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, SendMarksActivity.class);
            startActivity(intent);
        });

        btnRecordAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, RecordAttendanceActivity.class);
            startActivity(intent);
        });

        btnSendMemo.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, SendMemoActivity.class);
            startActivity(intent);
        });

        btnContactParents.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, ContactParentsActivity.class);
            startActivity(intent);
        });
        */
    }
}