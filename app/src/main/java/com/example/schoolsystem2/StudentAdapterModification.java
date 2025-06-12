package com.example.schoolsystem2;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAdapterModification extends RecyclerView.Adapter<StudentAdapterModification.StudentViewHolder> {

    private Context context;
    private List<Student> studentList;

    public StudentAdapterModification(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_modify, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.nameText.setText(student.getName());
        holder.genderText.setText("Gender: " + student.getGender());
        holder.dobText.setText("Date of Birth: " + student.getDob());
        holder.gradeLevelText.setText("Grade Level: " + student.getGradeLevel());
        holder.classNameText.setText("Class: " + student.getClassName());
        holder.contactText.setText("Parent Contact: " + student.getParentContact());

        holder.editNameBtn.setOnClickListener(v -> showEditDialog(student, position, "Name"));
        holder.editGenderBtn.setOnClickListener(v -> showEditDialog(student, position, "Gender"));
        holder.editDobBtn.setOnClickListener(v -> showEditDialog(student, position, "Date of Birth"));
        holder.editGradeLevelBtn.setOnClickListener(v -> showEditDialog(student, position, "Academic Year"));
        holder.editClassBtn.setOnClickListener(v -> showEditDialog(student, position, "Class"));
        holder.editContactBtn.setOnClickListener(v -> showEditDialog(student, position, "Parent Contact"));
    }

    private void showEditDialog(Student student, int position, String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit " + field);

        final EditText input = new EditText(context);
        input.setHint("Enter new " + field);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty()) {
                updateStudentOnServer(student, position, field, newValue);
            } else {
                Toast.makeText(context, "Value cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateStudentOnServer(Student student, int position, String field, String newValue) {
        String url = "http://10.0.2.2/Android/modify_student.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();

            switch (field) {
                case "Gender":
                    studentList.get(position).setGender(newValue);
                    break;
                case "Date of Birth":
                    studentList.get(position).setDob(newValue);
                    break;
                case "Parent Contact":
                    studentList.get(position).setParentContact(newValue);
                    break;
                case "Class":
                    studentList.get(position).setClassName(newValue);
                    break;
                case "Academic Year":
                    studentList.get(position).setGradeLevel(newValue);
                    break;
                case "Name":
                    studentList.get(position).setName(newValue);
                    break;
            }

            notifyItemChanged(position);

        }, error -> {
            Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("student_id", String.valueOf(student.getStudentId()));

                switch (field) {
                    case "Gender":
                        params.put("gender", newValue);
                        break;
                    case "Date of Birth":
                        params.put("date_of_birth", newValue);
                        break;
                    case "Parent Contact":
                        params.put("parent_contact", newValue);
                        break;
                    case "Class":
                        params.put("class_id", newValue);
                        break;
                    case "Academic Year":
                        params.put("academic_year", newValue);
                        break;
                    case "Name":
                        params.put("name", newValue);
                        break;
                }


                return params;
            }
        };

        queue.add(request);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, genderText, dobText, gradeLevelText, classNameText, contactText;
        Button editNameBtn, editGenderBtn, editDobBtn, editGradeLevelBtn, editClassBtn, editContactBtn;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            genderText = itemView.findViewById(R.id.genderText);
            dobText = itemView.findViewById(R.id.dobText);
            gradeLevelText = itemView.findViewById(R.id.gradeLevelText);
            classNameText = itemView.findViewById(R.id.classNameText);
            contactText = itemView.findViewById(R.id.contactText);

            editNameBtn = itemView.findViewById(R.id.editNameBtn);
            editGenderBtn = itemView.findViewById(R.id.editGenderBtn);
            editDobBtn = itemView.findViewById(R.id.editDobBtn);
            editGradeLevelBtn = itemView.findViewById(R.id.editGradeLevelBtn);
            editClassBtn = itemView.findViewById(R.id.editClassBtn);
            editContactBtn = itemView.findViewById(R.id.editContactBtn);
        }
    }
}
