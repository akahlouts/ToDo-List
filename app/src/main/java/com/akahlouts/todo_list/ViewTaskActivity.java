package com.akahlouts.todo_list;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.ViewTask;

public class ViewTaskActivity extends AppCompatActivity {
    private EditText dets_description;
    private TextView textview_edit;
    private TextView back_viewTask;
    private TextView timeAdded;
    private TextView textview_deleteTask;

    private String listId;
    private String taskId;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference = db.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        dets_description = findViewById(R.id.dets_description);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        ViewTask viewTask = new ViewTask();

        TextView detsTaskName = findViewById(R.id.dets_taskName);
        timeAdded = findViewById(R.id.timeAdded);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String taskName = bundle.getString("taskName");
            String time = bundle.getString("timeAdded");
            listId = bundle.getString("listId");
            taskId = bundle.getString("taskId");

            detsTaskName.setText(taskName);
            timeAdded.setText(time);
        }

        String currentId = user.getUid();


        databaseReference.child(currentId).child("List").child(listId)
                .child("Task").child(taskId).child("ViewTask")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            dets_description.setText(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        textview_edit = findViewById(R.id.textview_edit);

        dets_description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!(actionId == EditorInfo.IME_ACTION_PREVIOUS)) {

                    dets_description.setEnabled(true);

                        String newDescription = dets_description.getText().toString().trim();

                    String viewTaskId = databaseReference.child(currentId).child("List").child(listId)
                            .child("Task").child(taskId).child("ViewTask").push().getKey();

                        viewTask.setDescription(newDescription);
                        viewTask.setViewTaskId(viewTaskId);

                    databaseReference.child(currentId).child("List").child(listId).child("Task")
                                .child(taskId).child("ViewTask")
                                .child(viewTaskId).setValue(viewTask);
                }else{
                    Toast.makeText(ViewTaskActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


//            if (!dets_description.getText().toString().isEmpty()) {
//
//                textview_edit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dets_description.setEnabled(true);
//                    }
//                });
//            } else {
//
//                dets_description.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        dets_description.setEnabled(true);
//
//                        String newDescription = dets_description.getText().toString().trim();
//
//                        ViewTask viewTask = new ViewTask();
//                        viewTask.setDescription(newDescription);
//                        viewTask.setViewTaskId(viewTaskId);
//
//                        databaseReference.child(currentId).child("List").child(listId).child("Task")
//                                .child(taskId).child("ViewTask")
//                                .child(viewTaskId).setValue(viewTask);
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//
//                    }
//                });
//
//            }
//
//        databaseReference.child(currentId).child("List").child(listId)
//                .child("Task").child(taskId).child("ViewTask")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ViewTask value  = snapshot.getValue(ViewTask.class);
//                        dets_description.setText(value.getDescription());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//


        back_viewTask = findViewById(R.id.back_viewTask);
        back_viewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textview_deleteTask = findViewById(R.id.textview_deleteTask);
        textview_deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });
    }

    private void deleteTask() {
        builder = new AlertDialog.Builder(this);

        inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.confirmation_pop, null);

        Button noButton = view.findViewById(R.id.conf_no_button);
        Button yesButton = view.findViewById(R.id.conf_yes_button);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentId = user.getUid();

                databaseReference.child(currentId).child("List")
                        .child(listId).child("Task").child(taskId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                alertDialog.dismiss();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ViewTaskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}