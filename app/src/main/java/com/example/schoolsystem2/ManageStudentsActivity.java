package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageStudentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_students);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout viewStudentsCard = findViewById(R.id.viewStudentsCard);
        viewStudentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentsActivity.this, ViewStudentsActivity.class);
            startActivity(intent);
        });

        LinearLayout addStudentCard = findViewById(R.id.addStudentCard);
        addStudentCard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentsActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });

        LinearLayout deleteStudentCard = findViewById(R.id.deleteStudentCard);
        deleteStudentCard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentsActivity.this, DeleteStudentActivity.class);
            startActivity(intent);
        });

        // ✅ زر تعديل الطالب
        LinearLayout modifyStudentCard = findViewById(R.id.modifyStudentCard);
        modifyStudentCard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageStudentsActivity.this, ModifyStudentActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageStudentsActivity.this, AdminActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
