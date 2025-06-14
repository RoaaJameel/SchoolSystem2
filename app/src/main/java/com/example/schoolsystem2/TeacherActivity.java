package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeacherActivity extends AppCompatActivity {

    Button btnSendAssignment, btnSendMarks, btnRecordAttendance, btnContactParents , btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        btnSendAssignment = findViewById(R.id.btnSendAssignment);
        btnSendMarks = findViewById(R.id.btnSendMarks);
        btnRecordAttendance = findViewById(R.id.btnRecordAttendance);
        btnContactParents = findViewById(R.id.btnContactParents);
        btnLogOut = findViewById(R.id.btnLogout);

        btnSendAssignment.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, SendAssignmentActivity.class);
            startActivity(intent);
        });


        btnSendMarks.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, SendMarksActivity.class);
            startActivity(intent);
        });

        btnRecordAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, RecordAttendanceActivity.class);
            startActivity(intent);
        });


        btnContactParents.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, ContactParentsActivity.class);
            startActivity(intent);
        });

        btnLogOut.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}