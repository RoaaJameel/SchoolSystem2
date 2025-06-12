package com.example.schoolsystem2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewCoursesActivity extends AppCompatActivity {

    private RecyclerView coursesRecyclerView;
    private CourseAdapterViewing adapter;
    private ArrayList<Course> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_courses);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        coursesList = new ArrayList<>();
        adapter = new CourseAdapterViewing(coursesList);
        coursesRecyclerView.setAdapter(adapter);

        fetchCoursesFromServer();
    }

    private void fetchCoursesFromServer() {
        String url = "http://10.0.2.2/Android/view_courses.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    coursesList.clear();
                    try {
                        boolean success = response.getBoolean("success");
                        if (!success) {
                            Toast.makeText(ViewCoursesActivity.this, "No courses found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray courseArray = response.getJSONArray("courses");

                        for (int i = 0; i < courseArray.length(); i++) {
                            JSONObject obj = courseArray.getJSONObject(i);

                            String courseName = obj.getString("course_name");

                            ArrayList<String> gradeLevels = splitByComma(obj.getString("grade_levels"));
                            ArrayList<String> classes = splitByComma(obj.getString("classes"));
                            ArrayList<String> teachers = splitByComma(obj.getString("teachers"));

                            Course course = new Course(courseName, gradeLevels, classes, teachers);
                            coursesList.add(course);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ViewCoursesActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ViewCoursesActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show()
        );

        queue.add(jsonObjectRequest);
    }

    private ArrayList<String> splitByComma(String input) {
        ArrayList<String> list = new ArrayList<>();
        if (input != null && !input.trim().isEmpty()) {
            String[] parts = input.split(",");
            for (String part : parts) {
                list.add(part.trim());
            }
        }
        return list;
    }
}
