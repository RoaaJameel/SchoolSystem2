package com.example.schoolsystem2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeleteCourseActivity extends AppCompatActivity {

    private RecyclerView coursesRecyclerView;
    private CourseAdapterDeleting adapter;
    private ArrayList<Course> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_course);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        coursesRecyclerView = findViewById(R.id.coursesRecyclerView);
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        coursesList = new ArrayList<>();
        adapter = new CourseAdapterDeleting(coursesList, this::deleteCourse);
        coursesRecyclerView.setAdapter(adapter);

        fetchCoursesFromServer();
    }

    private void fetchCoursesFromServer() {
        String url = "http://10.0.2.2/Android/view_courses.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    coursesList.clear();
                    try {
                        if (!response.getBoolean("success")) {
                            Toast.makeText(this, "No courses found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray courseArray = response.getJSONArray("courses");

                        for (int i = 0; i < courseArray.length(); i++) {
                            JSONObject obj = courseArray.getJSONObject(i);

                            int courseId = obj.getInt("course_id"); // ✅ تأكد من أن ال PHP يرجع هذا الحقل
                            String courseName = obj.getString("course_name");

                            ArrayList<String> gradeLevels = splitByComma(obj.getString("grade_levels"));
                            ArrayList<String> classes = splitByComma(obj.getString("classes"));
                            ArrayList<String> teachers = splitByComma(obj.getString("teachers"));

                            Course course = new Course(courseId, courseName, gradeLevels, classes, teachers);
                            coursesList.add(course);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DeleteCourseActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(DeleteCourseActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show());

        queue.add(request);
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

    private void deleteCourse(int position, Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete the course \"" + course.getCourseName() + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String url = "http://10.0.2.2/Android/delete_course.php";

                    RequestQueue queue = Volley.newRequestQueue(this);

                    StringRequest deleteRequest = new StringRequest(Request.Method.POST, url,
                            response -> {
                                Toast.makeText(DeleteCourseActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                                coursesList.remove(position);
                                adapter.notifyItemRemoved(position);
                            },
                            error -> Toast.makeText(DeleteCourseActivity.this, "Failed to delete course", Toast.LENGTH_SHORT).show()) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("course_id", String.valueOf(course.getCourseId())); // ✅ نرسل course_id الآن
                            return params;
                        }
                    };

                    Log.d("DELETE_COURSE", "Sending course_id = " + course.getCourseId());
                    queue.add(deleteRequest);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
