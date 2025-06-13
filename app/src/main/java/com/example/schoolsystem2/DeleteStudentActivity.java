package com.example.schoolsystem2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteStudentActivity extends AppCompatActivity {

    // UI components
    private RecyclerView recyclerView;
    private Spinner gradeLevelSpinner, classSpinner, academicYearSpinner;

    // Adapter and data list
    private StudentAdapterDeletion studentAdapter;
    private ArrayList<Student> studentList;

    // Data for spinners
    private ArrayList<String> gradeLevelNames = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<String> academicYears = new ArrayList<>();

    // Maps for ID lookup by name
    private HashMap<String, String> gradeLevelMap = new HashMap<>();
    private HashMap<String, String> classMap = new HashMap<>();

    // URLs for backend endpoints
    private static final String URL_GET_STUDENTS = "http://10.0.2.2/Android/view_students.php";
    private static final String URL_DELETE_STUDENT = "http://10.0.2.2/Android/delete_student.php?student_id=";
    private static final String URL_GRADE_LEVEL = "http://10.0.2.2/Android/get_grade_levels.php";
    private static final String URL_CLASS = "http://10.0.2.2/Android/get_classes.php";
    private static final String URL_ACADEMIC_YEAR = "http://10.0.2.2/Android/get_academic_years.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_student);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewDelete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gradeLevelSpinner = findViewById(R.id.gradeLevelSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        academicYearSpinner = findViewById(R.id.academicYearSpinner);

        // Initialize data list and adapter
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapterDeletion(studentList, student -> deleteStudent(student.getId()));
        recyclerView.setAdapter(studentAdapter);

        // Load spinner data
        loadGradeLevels();
        loadClasses();
        loadAcademicYears();

        // Listener to trigger reloading students when filter changes
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadStudents();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        gradeLevelSpinner.setOnItemSelectedListener(filterListener);
        classSpinner.setOnItemSelectedListener(filterListener);
        academicYearSpinner.setOnItemSelectedListener(filterListener);

        // Load students initially
        loadStudents();
    }

    // Load grade levels and fill spinner
    private void loadGradeLevels() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_GRADE_LEVEL, null,
                response -> {
                    gradeLevelNames.clear();
                    gradeLevelMap.clear();
                    gradeLevelNames.add("All"); // Default option
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            String id = obj.optString("grade_level_id");
                            String name = obj.optString("name");
                            gradeLevelMap.put(name, id);
                            gradeLevelNames.add(name);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeLevelNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gradeLevelSpinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Failed to load grade levels", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    // Load classes and fill spinner
    private void loadClasses() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_CLASS, null,
                response -> {
                    classNames.clear();
                    classMap.clear();
                    classNames.add("All");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            String id = obj.optString("class_id");
                            String name = obj.optString("class_name");
                            classMap.put(name, id);
                            classNames.add(name);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    classSpinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Failed to load classes", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    // Load academic years and fill spinner
    private void loadAcademicYears() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_ACADEMIC_YEAR, null,
                response -> {
                    academicYears.clear();
                    academicYears.add("All");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.optJSONObject(i);
                        if (obj != null) {
                            String year = obj.optString("year");
                            academicYears.add(year);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, academicYears);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    academicYearSpinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Failed to load academic years", Toast.LENGTH_SHORT).show()
        );
        queue.add(request);
    }

    // Load students based on selected filters
    private void loadStudents() {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Get selected values from spinners
        String selectedGrade = gradeLevelSpinner.getSelectedItem() != null ? gradeLevelSpinner.getSelectedItem().toString() : "All";
        String selectedClass = classSpinner.getSelectedItem() != null ? classSpinner.getSelectedItem().toString() : "All";
        String selectedYear = academicYearSpinner.getSelectedItem() != null ? academicYearSpinner.getSelectedItem().toString() : "All";

        // Build URL with filters
        String url = URL_GET_STUDENTS + "?grade_level=";
        url += selectedGrade.equals("All") ? "" : gradeLevelMap.getOrDefault(selectedGrade, "");
        url += "&class_id=";
        url += selectedClass.equals("All") ? "" : classMap.getOrDefault(selectedClass, "");
        url += "&academic_year=";
        url += selectedYear.equals("All") ? "" : selectedYear;

        // Request student data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        studentList.clear();

                        if (!success) {
                            Toast.makeText(this, response.optString("message", "No students found"), Toast.LENGTH_LONG).show();
                            studentAdapter.notifyDataSetChanged();
                            return;
                        }

                        JSONArray studentsArray = response.optJSONArray("students");
                        if (studentsArray == null || studentsArray.length() == 0) {
                            Toast.makeText(this, "No students to display", Toast.LENGTH_LONG).show();
                            studentAdapter.notifyDataSetChanged();
                            return;
                        }

                        // Parse each student and add to list
                        for (int i = 0; i < studentsArray.length(); i++) {
                            JSONObject obj = studentsArray.getJSONObject(i);
                            int id = obj.getInt("student_id");
                            String name = obj.getString("full_name");
                            String gender = obj.getString("gender");
                            String dob = obj.getString("date_of_birth");
                            String contact = obj.optString("parent_contact", "N/A");
                            String gradeLevel = obj.optString("grade_level", "Unknown");
                            String className = obj.optString("class_name", "Unknown");

                            studentList.add(new Student(id, name, gender, dob, gradeLevel, className, contact));
                        }

                        // Update RecyclerView
                        studentAdapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        queue.add(jsonObjectRequest);
    }

    // Delete student with confirmation dialog
    private void deleteStudent(int studentId) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String url = URL_DELETE_STUDENT + studentId;
                    RequestQueue queue = Volley.newRequestQueue(this);

                    StringRequest request = new StringRequest(Request.Method.GET, url,
                            response -> {
                                Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_LONG).show();
                                loadStudents(); // Refresh list
                            },
                            error -> Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show()
                    );

                    queue.add(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
