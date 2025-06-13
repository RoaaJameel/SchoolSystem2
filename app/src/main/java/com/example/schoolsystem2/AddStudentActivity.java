package com.example.schoolsystem2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    // UI elements
    TextInputEditText usernameEditText, passwordEditText, confirmPasswordEditText,
            emailEditText, phoneEditText, fullNameEditText, dobEditText, parentContactEditText;
    Spinner classSpinner, gradeLevelSpinner;
    AutoCompleteTextView yearAutoComplete;
    RadioGroup genderRadioGroup;
    Button submitButton, clearButton;

    // API endpoints
    String addStudentURL = "http://10.0.2.2/Android/add_student.php";
    String getClassesURL = "http://10.0.2.2/Android/get_classes.php";
    String getYearsURL = "http://10.0.2.2/Android/get_academic_years.php";
    String getGradeLevelsURL = "http://10.0.2.2/Android/get_grade_levels.php";

    // Lists for spinner data
    ArrayList<String> classNames = new ArrayList<>();
    ArrayList<String> classIds = new ArrayList<>();
    ArrayList<String> academicYears = new ArrayList<>();
    ArrayList<String> gradeLevelNames = new ArrayList<>();
    ArrayList<String> gradeLevelIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // Bind views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        parentContactEditText = findViewById(R.id.parentContactEditText);
        classSpinner = findViewById(R.id.classSpinner);
        gradeLevelSpinner = findViewById(R.id.gradeLevelSpinner);
        yearAutoComplete = findViewById(R.id.yearAutoComplete);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        submitButton = findViewById(R.id.submitButton);
        clearButton = findViewById(R.id.clearButton);

        // Show date picker when DOB field is clicked
        dobEditText.setOnClickListener(v -> showDatePicker());

        // Loading data from server
        loadClasses();
        loadAcademicYears();
        loadGradeLevels();

        // Button actions
        submitButton.setOnClickListener(v -> submitForm());
        clearButton.setOnClickListener(v -> clearForm());
    }

    // Show date picker and validate age
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);

            if (!isAgeValid(year, month, dayOfMonth)) {
                Toast.makeText(this, "Student must be older than 5 years", Toast.LENGTH_SHORT).show();
                return;
            }
            String dob = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selected.getTime());
            dobEditText.setText(dob);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean isAgeValid(int year, int month, int day) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.set(year, month, day);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age >= 5;
    }

    private void loadClasses() {
        StringRequest request = new StringRequest(Request.Method.GET, getClassesURL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        classNames.clear();
                        classIds.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            classNames.add(obj.getString("class_name"));
                            classIds.add(obj.getString("class_id"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        classSpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to parse classes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading classes", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void loadAcademicYears() {
        StringRequest request = new StringRequest(Request.Method.GET, getYearsURL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        academicYears.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            academicYears.add(jsonArray.getJSONObject(i).getString("year"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, academicYears);
                        yearAutoComplete.setAdapter(adapter);
                        yearAutoComplete.setThreshold(1);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to parse academic years", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading academic years", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void loadGradeLevels() {
        StringRequest request = new StringRequest(Request.Method.GET, getGradeLevelsURL,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        gradeLevelNames.clear();
                        gradeLevelIds.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            gradeLevelIds.add(obj.getString("grade_level_id"));
                            gradeLevelNames.add(obj.getString("name"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gradeLevelNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        gradeLevelSpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Failed to parse grade levels", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading grade levels", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void submitForm() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String fullName = fullNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String dob = dobEditText.getText().toString();
        String parentContact = parentContactEditText.getText().toString();
        String academicYear = yearAutoComplete.getText().toString();

        if (classSpinner.getSelectedItemPosition() == -1 ||
                gradeLevelSpinner.getSelectedItemPosition() == -1 ||
                academicYear.isEmpty()) {
            Toast.makeText(this, "Please select class, grade level and academic year", Toast.LENGTH_SHORT).show();
            return;
        }
        String classId = classIds.get(classSpinner.getSelectedItemPosition());
        String gradeLevelId = gradeLevelIds.get(gradeLevelSpinner.getSelectedItemPosition());

        if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = ((RadioButton) findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString().toLowerCase();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addStudentURL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(this, "Student added successfully", Toast.LENGTH_LONG).show();
                            clearForm();
                        } else {
                            Toast.makeText(this, "Failed: " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                map.put("full_name", fullName);
                map.put("email", email);
                map.put("phone", phone);
                map.put("dob", dob);
                map.put("gender", gender);
                map.put("parent_contact", parentContact);
                map.put("class_id", classId);
                map.put("grade_level_id", gradeLevelId);
                map.put("academic_year", academicYear);
                return map;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void clearForm() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        fullNameEditText.setText("");
        dobEditText.setText("");
        parentContactEditText.setText("");
        yearAutoComplete.setText("");
        genderRadioGroup.clearCheck();
        classSpinner.setSelection(0);
        gradeLevelSpinner.setSelection(0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // حفظ البيانات في SharedPreferences عند الخروج
        getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).edit()
                .putString("username", usernameEditText.getText().toString())
                .putString("phone", phoneEditText.getText().toString())
                .putString("email", emailEditText.getText().toString())
                .putString("full_name", fullNameEditText.getText().toString())
                .putString("dob", dobEditText.getText().toString())
                .putString("parent_contact", parentContactEditText.getText().toString())
                .putString("academic_year", yearAutoComplete.getText().toString())
                .apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // استعادة البيانات عند العودة للتطبيق
        String username = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("username","");
        String phone = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("phone","");
        String email = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("email","");
        String full_name = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("full_name","");
        String dob = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("dob","");
        String parent_contact = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("parent_contact","");
        String academic_year = getSharedPreferences("AddStudentPrefs",MODE_PRIVATE).getString("academic_year","");

        usernameEditText.setText(username);
        phoneEditText.setText(phone);
        emailEditText.setText(email);
        fullNameEditText.setText(full_name);
        dobEditText.setText(dob);
        parentContactEditText.setText(parent_contact);
        yearAutoComplete.setText(academic_year);
    }
}
