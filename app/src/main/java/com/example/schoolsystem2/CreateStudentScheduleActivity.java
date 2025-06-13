package com.example.schoolsystem2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateStudentScheduleActivity extends AppCompatActivity {

    Spinner spinnerClass, spinnerCourse, spinnerTeacher, spinnerDay;
    EditText editStartTime, editEndTime;
    Button btnSave;

    List<Item> teacherList = new ArrayList<>();
    List<Item> courseList = new ArrayList<>();
    List<Item> classList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student_schedule);

        spinnerClass = findViewById(R.id.spinnerStudentClass);
        spinnerCourse = findViewById(R.id.spinnerStudentCourse);
        spinnerTeacher = findViewById(R.id.spinnerStudentTeacher);
        spinnerDay = findViewById(R.id.spinnerStudentDay);
        editStartTime = findViewById(R.id.editStudentStartTime);
        editEndTime = findViewById(R.id.editStudentEndTime);
        btnSave = findViewById(R.id.btnSaveStudentSchedule);

        setupDaySpinner();
        loadData();

        btnSave.setOnClickListener(v -> submitSchedule());
    }

    void setupDaySpinner() {
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        );
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);
    }

    void loadData() {
        loadSpinnerData("http://10.0.2.2/Android/get_classes2.php", spinnerClass, classList);
        loadSpinnerData("http://10.0.2.2/Android/get_courses2.php", spinnerCourse, courseList);
        loadSpinnerData("http://10.0.2.2/Android/get_teachers.php", spinnerTeacher, teacherList);
    }

    void loadSpinnerData(String url, Spinner spinner, List<Item> list) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        List<String> names = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Item item = new Item(obj.getInt("id"), obj.getString("name"));
                            list.add(item);
                            names.add(item.name);

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load: " + error.getMessage(), Toast.LENGTH_LONG).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    void submitSchedule() {

        int classId = classList.get(spinnerClass.getSelectedItemPosition()).id;
        int courseId = courseList.get(spinnerCourse.getSelectedItemPosition()).id;
        int teacherId = teacherList.get(spinnerTeacher.getSelectedItemPosition()).id;
        String day = spinnerDay.getSelectedItem().toString();
        String start = editStartTime.getText().toString();
        String end = editEndTime.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Start and end times required", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest req = new StringRequest(Request.Method.POST, "http://10.0.2.2/Android/create_schedule.php",
                response -> {
                    Toast.makeText(this, "Student schedule added", Toast.LENGTH_SHORT).show();
                    editStartTime.setText("");
                    editEndTime.setText("");
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("class_id", String.valueOf(classId));
                map.put("course_id", String.valueOf(courseId));
                map.put("teacher_id", String.valueOf(teacherId));
                map.put("day_of_week", day);
                map.put("start_time", start);
                map.put("end_time", end);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }

    static class Item {
        int id;
        String name;
        Item(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}