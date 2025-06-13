package com.example.schoolsystem2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecordAttendanceActivity extends AppCompatActivity {

    private Spinner classSpinner;
    private Button dateButton, submitAttendanceButton;
    private RecyclerView studentRecyclerView;
    private Calendar selectedDate;
    private List<Student> studentList = new ArrayList<>();
    private AttendanceAdapter adapter;
    private int selectedClassId = -1;
    private String formattedDate = "";

    ArrayList<String> classNames = new ArrayList<>();
    HashMap<String, String> classMap = new HashMap<>(); // Maps class name to class_id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_attendance);

        classSpinner = findViewById(R.id.classSpinner);
        dateButton = findViewById(R.id.dateButton);
        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        submitAttendanceButton = findViewById(R.id.submitAttendanceButton);

        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        selectedDate = Calendar.getInstance();
        updateDateButton();

        dateButton.setOnClickListener(v -> pickDate());

        loadClasses();

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedClassName = (String) parent.getItemAtPosition(position);
                String classIdString = classMap.get(selectedClassName);

                if (classIdString != null) {
                    selectedClassId = Integer.parseInt(classIdString);
                    loadStudentsForClass(selectedClassId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        submitAttendanceButton.setOnClickListener(v -> submitAttendance());
    }

    private void pickDate() {
        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    updateDateButton();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void updateDateButton() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formattedDate = sdf.format(selectedDate.getTime());
        dateButton.setText("Date: " + formattedDate);
    }

    private void loadClasses() {
        String url = "http://10.0.2.2/Android/get_classes.php";

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
                        classSpinner.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading classes: " + error.getMessage(), Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(this).add(request);
    }


    private void loadStudentsForClass(int classId) {
        String url = "http://10.0.2.2/Android/get_students_by_class.php?class_id=" + classId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        studentList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int studentId = obj.getInt("student_id");
                            String studentName = obj.getString("student_name");

                            studentList.add(new Student(studentId, studentName));
                        }
                        // Use Volley to fetch students in this class
                        // Populate studentList
                        // Then:

                        adapter = new AttendanceAdapter(studentList);
                        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        studentRecyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse students", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void submitAttendance() {
        if (selectedClassId == -1 || formattedDate.isEmpty() || adapter == null) {
            Toast.makeText(this, "Please select class and date", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/Android/submit_attendance.php";
        JSONObject requestBody = new JSONObject();
        JSONArray attendanceArray = new JSONArray();

        try {
            requestBody.put("class_id", selectedClassId);
            requestBody.put("date", formattedDate);

            Map<Integer, Boolean> attendanceMap = adapter.getAttendanceMap();
            for (Map.Entry<Integer, Boolean> entry : attendanceMap.entrySet()) {
                JSONObject obj = new JSONObject();
                obj.put("student_id", entry.getKey());
                obj.put("status", entry.getValue() ? "present" : "absent");
                attendanceArray.put(obj);
            }

            requestBody.put("attendance", attendanceArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to prepare data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this, "Attendance recorded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to record attendance", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}