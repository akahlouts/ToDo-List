package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akahlouts.todo_list.R;
import com.akahlouts.todo_list.ViewTaskActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import model.Task;

public class RecyclerViewTaskAdapter extends RecyclerView.Adapter<RecyclerViewTaskAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Task> taskList;
    private List<Task> forSearchTask;

    private FirebaseAuth firebaseAuth;

    public RecyclerViewTaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        forSearchTask = new ArrayList<>(taskList);
    }

    @NonNull
    @Override
    public RecyclerViewTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_row, viewGroup, false);

        return new RecyclerViewTaskAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTaskAdapter.ViewHolder viewHolder, int position) {
        viewHolder.setData(taskList.get(position));
        Task taskEntity = taskList.get(position);
        if (taskEntity.getIsChecked()) {
            viewHolder.checkBox.setChecked(true);
            viewHolder.checkBox.setPaintFlags(viewHolder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String currentId = user.getUid();

                Task newTask = new Task();
                newTask.setIsChecked(isChecked);
                newTask.setTaskName(taskEntity.getTaskName());
                newTask.setTaskId(taskEntity.getTaskId());
                newTask.setListId(taskEntity.getListId());
                newTask.setTimeAdded(taskEntity.getTimeAdded());


                FirebaseDatabase.getInstance().getReference("Users").child(currentId)
                        .child("List").child(taskEntity.getListId()).child("Task").child(taskEntity.getTaskId())
                        .child("isChecked").setValue(newTask.getIsChecked());

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

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            checkBox = itemView.findViewById(R.id.checkBox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Task task = taskList.get(position);
                    Intent intent = new Intent(context, ViewTaskActivity.class);
                    intent.putExtra("taskName", task.getTaskName());
                    intent.putExtra("timeAdded",task.getTimeAdded());
                    intent.putExtra("listId",task.getListId());
                    intent.putExtra("taskId",task.getTaskId());

                    context.startActivity(intent);
                }
            });
        }

        public void setData(Task task) {
            checkBox.setText(task.getTaskName());
            checkBox.setSelected(task.getIsChecked());
        }

    }

    @Override
    public Filter getFilter() {
        return taskSearch;
    }

    private Filter taskSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Task> filteredTask = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredTask.addAll(forSearchTask);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Task task : forSearchTask){
                    if (task.getTaskName().toLowerCase().contains(filterPattern)){
                        filteredTask.add(task);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredTask;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            taskList.clear();
            taskList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}