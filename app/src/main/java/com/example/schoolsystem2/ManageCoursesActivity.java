package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManageCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_courses);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onAddCourseClick(View view) {

         Intent intent = new Intent(this, AddCourseActivity.class);
         startActivity(intent);
    }

    public void onDeleteCourseClick(View view) {
        Toast.makeText(this, "Delete Course clicked", Toast.LENGTH_SHORT).show();

        // Intent intent = new Intent(this, DeleteCourseActivity.class);
        // startActivity(intent);
    }

    public void onViewCoursesClick(View view) {
        Toast.makeText(this, "View Courses clicked", Toast.LENGTH_SHORT).show();

        // Intent intent = new Intent(this, ViewCoursesActivity.class);
        // startActivity(intent);
    }
}
