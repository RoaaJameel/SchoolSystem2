package com.example.schoolsystem2;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SendMarksActivity extends AppCompatActivity {

    private Spinner assignmentSpinner;
    private RecyclerView studentRecyclerView;
    private Button submitMarksButton;

    private List<Student> studentList = new ArrayList<>();
    private List<Assignment> assignmentList = new ArrayList<>();
    private StudentMarksAdapter adapter;

    private int selectedAssignmentId;
    private int selectedClassId; // extracted from assignment selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_marks);
        assignmentSpinner = findViewById(R.id.assignmentSpinner);
        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        submitMarksButton = findViewById(R.id.submitMarksButton);

        loadAssignments();

        assignmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Assignment selected = assignmentList.get(pos);
                selectedAssignmentId = selected.getAssignmentId();
                selectedClassId = selected.getClassId(); // assumed available
                loadStudents(selectedClassId);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submitMarksButton.setOnClickListener(v -> {
            Map<Integer, Double> marksMap = adapter.getEnteredMarks(); // student_id to mark
            if (marksMap.isEmpty()) {
                Toast.makeText(this, "Please enter marks before submitting", Toast.LENGTH_SHORT).show();
                return;
            }

            submitMarksToServer(
                    selectedAssignmentId,
                    selectedClassId,
                    "assignment", // inferred exam type
                    marksMap
            );
        });
    }

    private void loadAssignments() {
        String url = "http://10.0.2.2/Android/get_assignments.php?teacher_id=1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        assignmentList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int id = obj.getInt("assignment_id");
                            String title = obj.getString("title");
                            int classId = obj.getInt("class_id");

                            assignmentList.add(new Assignment(id, title, classId));
                        }

                        ArrayAdapter<Assignment> spinnerAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, assignmentList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        assignmentSpinner.setAdapter(spinnerAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse assignments", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to load assignments", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }
    // Use Volley to load assignments from DB and populate assignmentList
    // Include assignment_id, title, class_id
    // Then set spinner adapter


    private void loadStudents(int classId) {
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

                        adapter = new StudentMarksAdapter(studentList);
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

    private void submitMarksToServer(int assignmentId, int classId, String examType, Map<Integer, Double> studentMarks) {
        String url = "http://10.0.2.2/Android/submit_marks.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject requestBody = new JSONObject();
        JSONArray marksArray = new JSONArray();

        try {
            requestBody.put("assignment_id", assignmentId);
            requestBody.put("class_id", classId);
            requestBody.put("exam_type", examType);

            for (Map.Entry<Integer, Double> entry : studentMarks.entrySet()) {
                JSONObject markObject = new JSONObject();
                markObject.put("student_id", entry.getKey());
                markObject.put("mark", entry.getValue());
                marksArray.put(markObject);
            }

            requestBody.put("marks", marksArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to prepare data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(this, "Marks submitted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.optString("message", "Error submitting marks");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String responseData = new String(error.networkResponse.data);
                        Toast.makeText(this, "Server Response:\n" + responseData, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(jsonRequest);
    }

}