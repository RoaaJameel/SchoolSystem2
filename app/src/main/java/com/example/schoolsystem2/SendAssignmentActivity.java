package com.example.schoolsystem2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SendAssignmentActivity extends AppCompatActivity {
    EditText etTitle, etDescription;
    TextView tvDueDate;
    Spinner spinnerSubject;
    Button btnSubmit;

    String selectedDate = "";
    ArrayList<String> courseNames = new ArrayList<>(); //
    HashMap<String, String> courseMap = new HashMap<>(); // Maps course name to course_id

    Spinner spinnerClass;

    ArrayList<String> classNames = new ArrayList<>();
    HashMap<String, String> classMap = new HashMap<>(); // Maps class name to class_id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_assignment);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        tvDueDate = findViewById(R.id.tvDueDate);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnSubmit = findViewById(R.id.btnSubmit);
        spinnerClass = findViewById(R.id.spinnerClass);

        loadCourses(); // Populate course spinner

        loadClasses(); // Populate class spinner


        tvDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                tvDueDate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        btnSubmit.setOnClickListener(v -> submitAssignment());
    }

    private void loadCourses() {
        String url = "http://10.0.2.2/Android/get_courses.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String courseId = obj.getString("course_id");
                            String courseName = obj.getString("course_name");

                            courseNames.add(courseName);
                            courseMap.put(courseName, courseId);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courseNames);
                        spinnerSubject.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace(); // Add this to see the error in Logcat

                    Toast.makeText(this, "Error loading courses: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void loadClasses() {
        String url = "http://10.0.2.2/Android/get_classes.php"; // PHP endpoint for classes

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String classId = obj.getString("class_id");
                            String className = obj.getString("class_name");

                            classNames.add(className);
                            classMap.put(className, classId);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classNames);
                        spinnerClass.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading classes: " + error.getMessage(), Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(this).add(request);
    }


    private void submitAssignment() {
        if (spinnerSubject.getSelectedItem() == null) {
            Toast.makeText(this, "Courses not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String selectedCourseName = spinnerSubject.getSelectedItem().toString();
        String courseId = courseMap.get(selectedCourseName);
        String selectedClassName = spinnerClass.getSelectedItem().toString();
        String classId = classMap.get(selectedClassName);


        if (spinnerSubject.getSelectedItem() == null) {
            Toast.makeText(this, "Please wait for courses to load", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || desc.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send to server using Volley
        String url = "http://10.0.2.2/Android/send_assignment.php"; // Replace with your server URL

        StringRequest request = new StringRequest(Request.Method.POST, url,
                //response -> Toast.makeText(this, "Assignment submitted!", Toast.LENGTH_SHORT).show(),
                //error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                response -> {
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(this, "Assignment submitted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Server error: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("description", desc);
                params.put("due_date", selectedDate);
                params.put("course_id", courseId);
                params.put("teacher_id", "1"); // Replace with actual teacher_id after login
                params.put("class_id", classId);

                //params.put("class_id", "3"); // Use actual class_id here if available

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}