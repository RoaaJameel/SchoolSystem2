package com.example.schoolsystem2;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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
        if (field.equals("Date of Birth")) {
            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date birthDate = sdf.parse(student.getDob());
                if (birthDate != null) {
                    calendar.setTime(birthDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        Calendar minDate = Calendar.getInstance();
                        minDate.add(Calendar.YEAR, -5);

                        if (selectedDate.after(minDate)) {
                            Toast.makeText(context, "Student MUST be older than 5 years.", Toast.LENGTH_LONG).show();
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String newDob = sdf.format(selectedDate.getTime());
                            if (newDob.equals(student.getDob())) {
                                Toast.makeText(context, "Notice: You entered the same value", Toast.LENGTH_SHORT).show();
                            } else {
                                updateStudentOnServer(student, position, field, newDob);
                            }
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, -5);
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            datePickerDialog.show();

        } else if (field.equals("Gender")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Gender");

            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            RadioGroup radioGroup = new RadioGroup(context);
            radioGroup.setOrientation(RadioGroup.VERTICAL);

            RadioButton maleRadio = new RadioButton(context);
            maleRadio.setText("Male");
            RadioButton femaleRadio = new RadioButton(context);
            femaleRadio.setText("Female");

            if ("Male".equalsIgnoreCase(student.getGender())) {
                maleRadio.setChecked(true);
            } else if ("Female".equalsIgnoreCase(student.getGender())) {
                femaleRadio.setChecked(true);
            }

            radioGroup.addView(maleRadio);
            radioGroup.addView(femaleRadio);
            layout.addView(radioGroup);

            builder.setView(layout);

            builder.setPositiveButton("Save", (dialog, which) -> {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadio = radioGroup.findViewById(selectedId);
                    String genderValue = selectedRadio.getText().toString();
                    if (genderValue.equalsIgnoreCase(student.getGender())) {
                        Toast.makeText(context, "Notice: You entered the same value", Toast.LENGTH_SHORT).show();
                    } else {
                        updateStudentOnServer(student, position, field, genderValue);
                    }
                } else {
                    Toast.makeText(context, "Please select a gender", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();

        } else if (field.equals("Class")) {
            fetchClassesAndShowDialog(student, position, field);

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit " + field);
            final EditText input = new EditText(context);
            input.setHint("Enter new " + field);

            switch (field) {
                case "Name":
                    input.setText(student.getName());
                    break;
                case "Parent Contact":
                    input.setText(student.getParentContact());
                    break;
                case "Academic Year":
                    input.setText(student.getGradeLevel());
                    break;
            }

            builder.setView(input);
            builder.setPositiveButton("Save", (dialog, which) -> {
                String newValue = input.getText().toString().trim();
                String oldValue = "";
                switch (field) {
                    case "Name":
                        oldValue = student.getName();
                        break;
                    case "Parent Contact":
                        oldValue = student.getParentContact();
                        break;
                    case "Academic Year":
                        oldValue = student.getGradeLevel();
                        break;
                }
                if (!newValue.isEmpty()) {
                    if (newValue.equals(oldValue)) {
                        Toast.makeText(context, "Notice: You entered the same value", Toast.LENGTH_SHORT).show();
                    } else {
                        updateStudentOnServer(student, position, field, newValue);
                    }
                } else {
                    Toast.makeText(context, "Value cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
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
    private void fetchClassesAndShowDialog(Student student, int position, String field) {
        String url = "http://10.0.2.2/Android/get_classes.php";
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            List<String> classNames = new ArrayList<>();
            List<String> classIds = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    classIds.add(obj.getString("class_id"));
                    classNames.add(obj.getString("class_name"));
                }
                showClassDialog(student, position, field, classNames, classIds);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing classes", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(context, "Error fetching classes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }


    private void showClassDialog(Student student, int position, String field, List<String> classNames, List<String> classIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Class");

        final Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, classNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int selectedIndex = classNames.indexOf(student.getClassName());
        if (selectedIndex != -1) {
            spinner.setSelection(selectedIndex);
        }

        builder.setView(spinner);

        builder.setPositiveButton("Save", (dialog, which) -> {
            int selectedPosition = spinner.getSelectedItemPosition();
            String selectedClassId = classIds.get(selectedPosition);
            String selectedClassName = classNames.get(selectedPosition);

            if (selectedClassName.equals(student.getClassName())) {
                Toast.makeText(context, "Notice: You entered the same value", Toast.LENGTH_SHORT).show();
            } else {
                updateStudentOnServerWithCallback(student, position, field, selectedClassId, () -> {
                    student.setClassName(selectedClassName);
                    notifyItemChanged(position);
                    Toast.makeText(context, "The data successfully updated", Toast.LENGTH_SHORT).show();
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void updateStudentOnServerWithCallback(Student student, int position, String field, String newValue, Runnable onSuccess) {
        String url = "http://10.0.2.2/Android/modify_student.php";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            if (onSuccess != null) onSuccess.run();
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
