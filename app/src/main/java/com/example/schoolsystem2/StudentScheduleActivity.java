package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentScheduleActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    List<Schedule> scheduleList = new ArrayList<>();
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule);

        recyclerView = findViewById(R.id.recyclerViewStudentSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(scheduleList);
        recyclerView.setAdapter(adapter);

        // Get class ID of the logged-in student
        Intent intent = getIntent();
        classId = intent.getStringExtra("class_id");

        if (classId == null) {
            Toast.makeText(this, "Missing class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudentSchedules();
    }

    void loadStudentSchedules() {
        String url = "http://10.0.2.2/Android/get_schedule_by_class.php?class_id=" + classId;

        RequestQueue requestQueue = Volley.newRequestQueue(this); // No singleton used

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);
                        scheduleList.clear(); // Clear previous data
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            Schedule s = new Schedule(
                                    obj.getInt("schedule_id"),
                                    obj.getString("class_name"),
                                    obj.getString("course_name"),
                                    obj.getString("teacher_name"),
                                    obj.getString("day_of_week"),
                                    obj.getString("start_time"),
                                    obj.getString("end_time")
                            );
                            scheduleList.add(s);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading schedule", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}