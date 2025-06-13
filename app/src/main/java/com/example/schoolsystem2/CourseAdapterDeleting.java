package com.example.schoolsystem2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapterDeleting extends RecyclerView.Adapter<CourseAdapterDeleting.CourseViewHolder> {

    private List<Course> courses;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position, Course course);
    }

    public CourseAdapterDeleting(List<Course> courses, OnDeleteClickListener listener) {
        this.courses = courses;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delete_course, parent, false);
        return new CourseViewHolder(v, onDeleteClickListener, courses);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseNameTextView.setText(course.getCourseName());
        holder.gradeLevelsTextView.setText("Grade Level: " + joinList(course.getGradeLevels()));
        holder.classesTextView.setText("Class: " + joinList(course.getClasses()));
        holder.teachersTextView.setText("Teacher: " + joinList(course.getTeachers()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "N/A";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView courseNameTextView, gradeLevelsTextView, classesTextView, teachersTextView;
        Button deleteButton;

        public CourseViewHolder(@NonNull View itemView, OnDeleteClickListener listener, List<Course> courseList) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            gradeLevelsTextView = itemView.findViewById(R.id.gradeLevelsTextView);
            classesTextView = itemView.findViewById(R.id.classesTextView);
            teachersTextView = itemView.findViewById(R.id.teachersTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position, courseList.get(position));
                }
            });
        }
    }
}
