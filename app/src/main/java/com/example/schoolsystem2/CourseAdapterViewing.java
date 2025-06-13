package com.example.schoolsystem2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseAdapterViewing extends RecyclerView.Adapter<CourseAdapterViewing.CourseViewHolder> {

    private List<Course> courses;

    public CourseAdapterViewing(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);

        holder.courseNameTextView.setText(course.getCourseName());
        holder.gradeLevelsTextView.setText("Grade Levels: " + joinList(course.getGradeLevels()));
        holder.classesTextView.setText("Classes: " + joinList(course.getClasses()));
        holder.teachersTextView.setText("Teachers: " + joinList(course.getTeachers()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    private String joinList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "N/A";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView courseNameTextView;
        TextView gradeLevelsTextView;
        TextView classesTextView;
        TextView teachersTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            gradeLevelsTextView = itemView.findViewById(R.id.gradeLevelsTextView);
            classesTextView = itemView.findViewById(R.id.classesTextView);
            teachersTextView = itemView.findViewById(R.id.teachersTextView);
        }
    }
}
