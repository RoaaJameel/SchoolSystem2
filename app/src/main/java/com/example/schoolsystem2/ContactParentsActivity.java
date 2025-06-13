package com.example.schoolsystem2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactParentsActivity extends AppCompatActivity {

    private Spinner studentSpinner;
    private EditText messageEditText;
    private Button sendEmailButton;

    private ArrayList<StudentItem> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_parents);

        studentSpinner = findViewById(R.id.studentSpinner);
        messageEditText = findViewById(R.id.messageEditText);
        sendEmailButton = findViewById(R.id.sendEmailButton);

        loadStudents();

        sendEmailButton.setOnClickListener(v -> sendEmailToParent());
    }

    private void loadStudents() {
        String url = "http://10.0.2.2/Android/get_students_with_parents.php";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        studentList.clear();
                        ArrayList<String> names = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("student_id");
                            String name = obj.getString("name");
                            String email = obj.getString("parent_contact");

                            studentList.add(new StudentItem(id, name, email));
                            names.add(name);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        studentSpinner.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to parse students", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error loading students", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void sendEmailToParent() {
        String message = messageEditText.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = studentSpinner.getSelectedItemPosition();
        if (position == -1 || position >= studentList.size()) {
            Toast.makeText(this, "No student selected", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentItem selectedStudent = studentList.get(position);
        String parentEmail = selectedStudent.getParentContact();

        String url = "http://10.0.2.2/Android/send_parent_email.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(this, "Email sent successfully", Toast.LENGTH_SHORT).show(),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                String body = "email=" + parentEmail + "&message=" + message;
                return body.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // Inner class to hold student info
    static class StudentItem {
        private final int id;
        private final String name;
        private final String parentContact;

        public StudentItem(int id, String name, String parentContact) {
            this.id = id;
            this.name = name;
            this.parentContact = parentContact;
        }

        public int getId() { return id; }

        public String getName() { return name; }

        public String getParentContact() { return parentContact; }
    }
}