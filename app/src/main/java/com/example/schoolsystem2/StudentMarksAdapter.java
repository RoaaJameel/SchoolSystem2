package com.example.schoolsystem2;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentMarksAdapter extends RecyclerView.Adapter<StudentMarksAdapter.StudentMarkViewHolder> {

    private final List<Student> studentList;
    private final Map<Integer, Double> enteredMarks = new HashMap<>();

    public StudentMarksAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentMarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_mark_item, parent, false);
        return new StudentMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentMarkViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStudentName.setText(student.getName());

        holder.etMark.setText(""); // Clear previous text

        holder.etMark.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double mark = Double.parseDouble(s.toString());
                    enteredMarks.put(student.getStudentId(), mark);
                } catch (NumberFormatException e) {
                    enteredMarks.remove(student.getStudentId()); // Invalid input
                }
            }

            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public Map<Integer, Double> getEnteredMarks() {
        return enteredMarks;
    }

    public static class StudentMarkViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        EditText etMark;

        public StudentMarkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.studentNameTextView);
            etMark = itemView.findViewById(R.id.markEditText);
        }
    }
}