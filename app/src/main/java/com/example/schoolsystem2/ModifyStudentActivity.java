package com.example.schoolsystem2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyStudentActivity extends AppCompatActivity {

    private Spinner gradeLevelSpinner, classSpinner, academicYearSpinner;
    private RecyclerView recyclerView;
    private StudentAdapterModification adapter;
    private ArrayList<Student> studentList;

    private ArrayList<String> gradeLevelNames = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<String> academicYears = new ArrayList<>();

    private HashMap<String, String> gradeLevelMap = new HashMap<>();
    private HashMap<String, String> classMap = new HashMap<>();

    private static final String URL_STUDENTS = "http://10.0.2.2/Android/view_students.php";
    private static final String URL_GRADE_LEVEL = "http://10.0.2.2/Android/get_grade_levels.php";
    private static final String URL_CLASS = "http://10.0.2.2/Android/get_classes.php";
    private static final String URL_ACADEMIC_YEAR = "http://10.0.2.2/Android/get_academic_years.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modify_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gradeLevelSpinner = findViewById(R.id.gradeLevelSpinner);
        classSpinner = findViewById(R.id.classSpinner);
        academicYearSpinner = findViewById(R.id.academicYearSpinner);
        recyclerView = findViewById(R.id.recyclerViewDelete);

        studentList = new ArrayList<>();
        adapter = new StudentAdapterModification(this, studentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadGradeLevels();
        loadClasses();
        loadAcademicYears();

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
    }

    private void loadGradeLevels() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_GRADE_LEVEL, null,
                response -> {
                    gradeLevelNames.clear();
                    gradeLevelMap.clear();
                    gradeLevelNames.add("All");
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
                error -> Toast.makeText(this, "Failed to load grade levels", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

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
                error -> Toast.makeText(this, "Failed to load classes", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

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
                error -> Toast.makeText(this, "Failed to load academic years", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void loadStudents() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String selectedGrade = gradeLevelSpinner.getSelectedItem() != null ? gradeLevelSpinner.getSelectedItem().toString() : "All";
        String selectedClass = classSpinner.getSelectedItem() != null ? classSpinner.getSelectedItem().toString() : "All";
        String selectedYear = academicYearSpinner.getSelectedItem() != null ? academicYearSpinner.getSelectedItem().toString() : "All";

        String url = URL_STUDENTS + "?grade_level=";
        url += selectedGrade.equals("All") ? "" : gradeLevelMap.getOrDefault(selectedGrade, "");
        url += "&class_id=";
        url += selectedClass.equals("All") ? "" : classMap.getOrDefault(selectedClass, "");
        url += "&academic_year=";
        url += selectedYear.equals("All") ? "" : selectedYear;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        if (!success) {
                            Toast.makeText(this, response.optString("message", "No students found"), Toast.LENGTH_LONG).show();
                            studentList.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        JSONArray studentsArray = response.optJSONArray("students");
                        if (studentsArray == null || studentsArray.length() == 0) {
                            Toast.makeText(this, "No students to display", Toast.LENGTH_LONG).show();
                            studentList.clear();
                            adapter.notifyDataSetChanged();
                            return;
                        }
                        studentList.clear();
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

                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show());

        queue.add(jsonObjectRequest);
    }
}
