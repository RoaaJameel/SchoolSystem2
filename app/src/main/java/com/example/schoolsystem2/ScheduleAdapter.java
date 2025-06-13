package com.example.schoolsystem2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<Schedule> scheduleList;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }


    @Override
    public ScheduleViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder( ScheduleViewHolder holder, int position) {
        Schedule s = scheduleList.get(position);
        holder.textClass.setText("Class: " + s.className);
        holder.textCourse.setText("Course: " + s.courseName);
        holder.textTeacher.setText("Teacher: " + s.teacherName);
        holder.textDay.setText("Day: " + s.day);
        holder.textTime.setText("Time: " + s.startTime + " - " + s.endTime);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView textClass, textCourse, textTeacher, textDay, textTime;

        public ScheduleViewHolder( View itemView) {
            super(itemView);
            textClass = itemView.findViewById(R.id.textClass);
            textCourse = itemView.findViewById(R.id.textCourse);
            textTeacher = itemView.findViewById(R.id.textTeacher);
            textDay = itemView.findViewById(R.id.textDay);
            textTime = itemView.findViewById(R.id.textTime);
        }
    }
}
