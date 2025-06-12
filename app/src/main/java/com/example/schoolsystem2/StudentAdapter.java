package com.example.schoolsystem2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.nameText.setText(student.getName());
        holder.genderText.setText("Gender: " + student.getGender());
        holder.dobText.setText("Date Of Birth: " + student.getDob());
        holder.gradeLevelText.setText("Grade Level: " + student.getGradeLevel());
        holder.classNameText.setText("Class: " + student.getClassName());
        holder.contactText.setText("Parent Contact: " + student.getParentContact());
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, genderText, dobText, gradeLevelText, classNameText, contactText;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            genderText = itemView.findViewById(R.id.genderText);
            dobText = itemView.findViewById(R.id.dobText);
            gradeLevelText = itemView.findViewById(R.id.gradeLevelText);
            classNameText = itemView.findViewById(R.id.classNameText);
            contactText = itemView.findViewById(R.id.contactText);
        }
    }
}
