package com.akahlouts.todo_list.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akahlouts.todo_list.R;
import com.akahlouts.todo_list.model.Task;

import java.util.List;

public class RecyclerViewTaskAdapter extends RecyclerView.Adapter<RecyclerViewTaskAdapter.ViewHolder> {
    private Context context;
    private List<Task> taskList;

    public RecyclerViewTaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public RecyclerViewTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTaskAdapter.ViewHolder viewHolder, int position) {
//        Task task = taskList.get(position);
//        viewHolder.checkBox.setText(task.getTaskName());

        viewHolder.setData(taskList.get(position));
        Task taskEntity = taskList.get(position);
        if (taskEntity.getIsChecked()) {
            viewHolder.checkBox.setChecked(true);
            viewHolder.checkBox.setPaintFlags(viewHolder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                taskEntity.setIsChecked(isChecked);
                viewHolder.checkBox.setSelected(isChecked);
                if (isChecked) {
                    viewHolder.checkBox.setText(taskEntity.getTaskName());
                    viewHolder.checkBox.setPaintFlags(viewHolder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    viewHolder.checkBox.setText(taskEntity.getTaskName());
                    viewHolder.checkBox.setPaintFlags(viewHolder.checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public String taskName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void setData(Task task) {
            checkBox.setText(task.getTaskName());
            checkBox.setSelected(task.getIsChecked());
        }
    }
}