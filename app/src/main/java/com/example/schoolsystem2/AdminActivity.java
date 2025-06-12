package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    CardView cardStudents, cardCourses, cardSchedules;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Setup Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Logout button in navigation header
        View headerView = navigationView.getHeaderView(0);
        Button buttonLogout = headerView.findViewById(R.id.button_logout);

        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize cards
        cardStudents = findViewById(R.id.card_students);
        cardCourses = findViewById(R.id.card_courses);
        cardSchedules = findViewById(R.id.card_schedules);

        // Set click listeners
        cardStudents.setOnClickListener(v ->
                startActivity(new Intent(AdminActivity.this, ManageStudentsActivity.class))
        );

//        cardCourses.setOnClickListener(v ->
//                startActivity(new Intent(AdminActivity.this, CoursesActivity.class))
//        );

//        cardSchedules.setOnClickListener(v ->
//                startActivity(new Intent(AdminActivity.this, ScheduleActivity.class))
//        );
    }

}
