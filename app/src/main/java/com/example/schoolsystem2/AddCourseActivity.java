package com.example.schoolsystem2;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddCourseActivity extends AppCompatActivity {

    private LinearLayout gradeLevelsContainer;
    private EditText courseNameEditText;
    private Spinner classSpinner, teacherSpinner;
    private Button submitCourseButton;

    private ArrayList<Item> classItems = new ArrayList<>();
    private ArrayList<Item> teacherItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_course);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gradeLevelsContainer = findViewById(R.id.gradeLevelsContainer);
        courseNameEditText = findViewById(R.id.courseNameEditText);
        classSpinner = findViewById(R.id.classSpinner);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        submitCourseButton = findViewById(R.id.submitCourseButton);

        fetchGradeLevelsAndCreateCheckboxes();
        fetchClasses();
        fetchTeachers();

        submitCourseButton.setOnClickListener(v -> submitCourse());
    }

    private void fetchGradeLevelsAndCreateCheckboxes() {
        String url = "http://10.0.2.2/Android/get_grade_levels.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    gradeLevelsContainer.removeAllViews();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject level = response.getJSONObject(i);
                            String levelName = level.getString("name");
                            int levelId = level.getInt("grade_level_id");

                            CheckBox checkBox = new CheckBox(AddCourseActivity.this);
                            checkBox.setText(levelName);
                            checkBox.setTag(levelId);

                            gradeLevelsContainer.addView(checkBox, new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCourseActivity.this, "Error parsing grade levels", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddCourseActivity.this, "Failed to load grade levels", Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);
    }

    private void fetchClasses() {
        String url = "http://10.0.2.2/Android/get_classes.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    classItems.clear();
                    ArrayList<String> classNames = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("class_id");
                            String name = obj.getString("class_name");
                            classItems.add(new Item(id, name));
                            classNames.add(name);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCourseActivity.this,
                                android.R.layout.simple_spinner_item, classNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        classSpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCourseActivity.this, "Error parsing classes data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddCourseActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void fetchTeachers() {
        String url = "http://10.0.2.2/Android/get_teachers.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    teacherItems.clear();
                    ArrayList<String> teacherNames = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("teacher_id");
                            String name = obj.getString("teacher_name");
                            teacherItems.add(new Item(id, name));
                            teacherNames.add(name);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddCourseActivity.this,
                                android.R.layout.simple_spinner_item, teacherNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        teacherSpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCourseActivity.this, "Error parsing teachers data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AddCourseActivity.this, "Failed to load teachers", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void submitCourse() {
        String courseName = courseNameEditText.getText().toString().trim();
        if (courseName.isEmpty()) {
            Toast.makeText(this, "Please enter course name", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> selectedGradeLevels = new ArrayList<>();
        for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
            View child = gradeLevelsContainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                if (cb.isChecked()) {
                    selectedGradeLevels.add(cb.getTag().toString());
                }
            }
        }

        int selectedClassPosition = classSpinner.getSelectedItemPosition();
        int selectedTeacherPosition = teacherSpinner.getSelectedItemPosition();

        if (selectedClassPosition < 0 || selectedTeacherPosition < 0) {
            Toast.makeText(this, "Please select class and teacher", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedClassId = classItems.get(selectedClassPosition).id;
        int selectedTeacherId = teacherItems.get(selectedTeacherPosition).id;

        String url = "http://10.0.2.2/Android/add_course.php";

        StringBuilder postData = new StringBuilder();
        postData.append("course_name=").append(courseName.replace(" ", "%20"));

        for (String gradeId : selectedGradeLevels) {
            postData.append("&grade_levels[]=").append(gradeId);
        }

        postData.append("&class_teacher_links[0][class_id]=").append(selectedClassId);
        postData.append("&class_teacher_links[0][teacher_id]=").append(selectedTeacherId);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(AddCourseActivity.this, response, Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(AddCourseActivity.this, "Failed to add course", Toast.LENGTH_SHORT).show()) {
            @Override
            public byte[] getBody() {
                return postData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        queue.add(stringRequest);
    }

    private static class Item {
        int id;
        String name;

        Item(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
