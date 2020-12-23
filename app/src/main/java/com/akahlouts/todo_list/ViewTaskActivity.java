package com.akahlouts.todo_list;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        TextView detsTaskName = findViewById(R.id.dets_taskName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String taskName = bundle.getString("taskName");

            detsTaskName.setText(taskName);
        }
    }
}