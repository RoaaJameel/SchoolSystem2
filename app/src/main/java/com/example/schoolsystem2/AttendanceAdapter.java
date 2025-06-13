package com.example.schoolsystem2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private final List<Student> students;
    private final Map<Integer, Boolean> attendanceMap = new HashMap<>();

    public AttendanceAdapter(List<Student> students) {
        this.students = students;
        for (Student s : students) {
            attendanceMap.put(s.getStudentId(), true); // default: present
        }
    }

    public Map<Integer, Boolean> getAttendanceMap() {
        return attendanceMap;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_attendance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.nameTextView.setText(student.getName());

        holder.checkBox.setChecked(attendanceMap.get(student.getStudentId()));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                attendanceMap.put(student.getStudentId(), isChecked));
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        CheckBox checkBox;

        ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.studentNameTextView);
            checkBox = view.findViewById(R.id.attendanceCheckbox);
        }
    }
}
