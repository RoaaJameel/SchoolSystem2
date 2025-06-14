package com.example.schoolsystem2;

import android.content.SharedPreferences;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AddCourseActivity extends AppCompatActivity {

    // UI components
    private LinearLayout gradeLevelsContainer;
    private EditText courseNameEditText;
    private Spinner classSpinner, teacherSpinner;
    private Button submitCourseButton;

    // Lists for storing fetched class and teacher items
    private ArrayList<Item> classItems = new ArrayList<>();
    private ArrayList<Item> teacherItems = new ArrayList<>();
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "AddCoursePrefs";

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

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        fetchGradeLevelsAndCreateCheckboxes();
        fetchClasses();
        fetchTeachers();

        submitCourseButton.setOnClickListener(v -> submitCourse());

        // بعد التحميل الكامل في الأداة، رح يتم استعادة البيانات لاحقاً في loadFormData()
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
                        // بعد التحميل يتم استعادة الداتا المخزنة
                        loadFormData();

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
                        int pos = prefs.getInt("classPosition", 0);
                        classSpinner.setSelection(pos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCourseActivity.this, "Error parsing classes", Toast.LENGTH_SHORT).show();
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
                        int pos = prefs.getInt("teacherPosition", 0);
                        teacherSpinner.setSelection(pos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddCourseActivity.this, "Error parsing teachers", Toast.LENGTH_SHORT).show();
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
        final String selectedClassId = String.valueOf(classItems.get(selectedClassPosition).id);
        final String selectedTeacherId = String.valueOf(teacherItems.get(selectedTeacherPosition).id);

        if (selectedGradeLevels.isEmpty()) {
            Toast.makeText(this, "Please select at least one grade level", Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] requestsCount = {selectedGradeLevels.size()};
        final boolean[] addedAnyCourse = {false};

        for (String gradeId : selectedGradeLevels) {
            final String finalGradeId = gradeId;
            String courseNameEncoded;
            try {
                courseNameEncoded = URLEncoder.encode(courseName, "UTF-8");
            } catch (Exception e) {
                courseNameEncoded = courseName.replace(" ", "%20");
            }
            final String finalCourseNameEncoded = courseNameEncoded;

            String checkUrl = "http://10.0.2.2/Android/check_course_exists.php?course_name=" + finalCourseNameEncoded
                    + "&class_id=" + selectedClassId
                    + "&grade_level=" + finalGradeId;

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest checkRequest = new StringRequest(Request.Method.GET, checkUrl,
                    checkResponse -> {
                        if ("EXISTS".equals(checkResponse.trim())) {
                            Toast.makeText(AddCourseActivity.this, "This course is already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            String url = "http://10.0.2.2/Android/add_course.php";
                            StringBuilder postData = new StringBuilder();
                            postData.append("course_name=").append(finalCourseNameEncoded);
                            postData.append("&grade_levels[]=").append(finalGradeId);
                            postData.append("&class_teacher_links[0][class_id]=").append(selectedClassId);
                            postData.append("&class_teacher_links[0][teacher_id]=").append(selectedTeacherId);

                            StringRequest addRequest = new StringRequest(Request.Method.POST, url,
                                    response -> {
                                        Toast.makeText(AddCourseActivity.this, "Course added successfully", Toast.LENGTH_SHORT).show();
                                        addedAnyCourse[0] = true;
                                        requestsCount[0]--;
                                        if (requestsCount[0] == 0) {
                                            clearFormData();
                                            courseNameEditText.setText("");
                                            for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
                                                View child = gradeLevelsContainer.getChildAt(i);
                                                if (child instanceof CheckBox) {
                                                    ((CheckBox) child).setChecked(false);
                                                }
                                            }
                                            if (classSpinner.getAdapter() != null && classSpinner.getAdapter().getCount() > 0) {
                                                classSpinner.setSelection(0);
                                            }
                                            if (teacherSpinner.getAdapter() != null && teacherSpinner.getAdapter().getCount() > 0) {
                                                teacherSpinner.setSelection(0);
                                            }
                                        }
                                    },
                                    error -> {
                                        Toast.makeText(AddCourseActivity.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                                        requestsCount[0]--;
                                        if (requestsCount[0] == 0) {
                                            clearFormData();
                                            courseNameEditText.setText("");
                                            for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
                                                View child = gradeLevelsContainer.getChildAt(i);
                                                if (child instanceof CheckBox) {
                                                    ((CheckBox) child).setChecked(false);
                                                }
                                            }
                                            if (classSpinner.getAdapter() != null && classSpinner.getAdapter().getCount() > 0) {
                                                classSpinner.setSelection(0);
                                            }
                                            if (teacherSpinner.getAdapter() != null && teacherSpinner.getAdapter().getCount() > 0) {
                                                teacherSpinner.setSelection(0);
                                            }
                                        }
                                    }) {
                                @Override
                                public byte[] getBody() {
                                    return postData.toString().getBytes(StandardCharsets.UTF_8);
                                }
                                @Override
                                public String getBodyContentType() {
                                    return "application/x-www-form-urlencoded; charset=UTF-8";
                                }
                            };
                            queue.add(addRequest);
                            return;
                        }
                        requestsCount[0]--;
                        if (requestsCount[0] == 0 && !addedAnyCourse[0]) {
                            clearFormData();
                            courseNameEditText.setText("");
                            for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
                                View child = gradeLevelsContainer.getChildAt(i);
                                if (child instanceof CheckBox) {
                                    ((CheckBox) child).setChecked(false);
                                }
                            }
                            if (classSpinner.getAdapter() != null && classSpinner.getAdapter().getCount() > 0) {
                                classSpinner.setSelection(0);
                            }
                            if (teacherSpinner.getAdapter() != null && teacherSpinner.getAdapter().getCount() > 0) {
                                teacherSpinner.setSelection(0);
                            }
                        }
                    },
                    error -> {
                        Toast.makeText(AddCourseActivity.this, "فشل التحقق من وجود الكورس", Toast.LENGTH_SHORT).show();
                        requestsCount[0]--;
                        if (requestsCount[0] == 0 && !addedAnyCourse[0]) {
                            clearFormData();
                            courseNameEditText.setText("");
                            for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
                                View child = gradeLevelsContainer.getChildAt(i);
                                if (child instanceof CheckBox) {
                                    ((CheckBox) child).setChecked(false);
                                }
                            }
                            if (classSpinner.getAdapter() != null && classSpinner.getAdapter().getCount() > 0) {
                                classSpinner.setSelection(0);
                            }
                            if (teacherSpinner.getAdapter() != null && teacherSpinner.getAdapter().getCount() > 0) {
                                teacherSpinner.setSelection(0);
                            }
                        }
                    }
            );
            queue.add(checkRequest);
        }
    }



    private void loadFormData() {
        courseNameEditText.setText(prefs.getString("course_name", ""));
        for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
            View child = gradeLevelsContainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                boolean isChecked = prefs.getBoolean("grade_" + cb.getTag(), false);
                cb.setChecked(isChecked);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("course_name", courseNameEditText.getText().toString());

        for (int i = 0; i < gradeLevelsContainer.getChildCount(); i++) {
            View child = gradeLevelsContainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                editor.putBoolean("grade_" + cb.getTag(), cb.isChecked());
            }
        }
        editor.putInt("classPosition", classSpinner.getSelectedItemPosition());
        editor.putInt("teacherPosition", teacherSpinner.getSelectedItemPosition());

        editor.apply();
    }

    private void clearFormData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private static class Item {
        int id;
        String name;

        Item(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
