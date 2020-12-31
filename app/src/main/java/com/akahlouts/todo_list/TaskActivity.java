package com.akahlouts.todo_list;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import adapter.RecyclerViewTaskAdapter;
import model.Task;

public class TaskActivity extends AppCompatActivity {

    private RecyclerViewTaskAdapter recyclerViewTaskAdapter;
    private RecyclerView recyclerViewTask;

    private EditText itemTaskName;
    private List<Task> taskList;
    private Button btn_saveTask;
    private SearchView search_view;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;

    private String listId;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference = db.getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        TextView detsListName = findViewById(R.id.dets_listName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String listName = bundle.getString("listName");
            listId = bundle.getString("listId");

            detsListName.setText(listName + " List");
        }

        recyclerViewTask = findViewById(R.id.recyclerViewTask);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));

        String currentId = user.getUid();
        taskList = new ArrayList<>();
        databaseReference.child(currentId).child("List").child(listId).child("Task")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        taskList.clear();
                        for (DataSnapshot tasks : snapshot.getChildren()) {
                            Task task = tasks.getValue(Task.class);
                            taskList.add(0, task);
                        }

                        recyclerViewTaskAdapter = new RecyclerViewTaskAdapter(TaskActivity.this, taskList);
                        recyclerViewTask.setAdapter(recyclerViewTaskAdapter);
                        recyclerViewTaskAdapter.notifyItemInserted(0);
                        recyclerViewTask.smoothScrollToPosition(0);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTask);

        Button btn_createTask = findViewById(R.id.btn_createTask);
        btn_createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };

        TextView back_task = findViewById(R.id.back_task);
        back_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskActivity.this, ListActivity.class));
                finish();
            }
        });

        TextView textview_deleteList = findViewById(R.id.textview_deleteList);
        textview_deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        search_view = findViewById(R.id.search_viewTask);
        search_view.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.hintSearchMess) + "</font>"));
        int id = search_view.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) search_view.findViewById(id);
        textView.setTextColor(Color.WHITE);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewTaskAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void deleteItem() {

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
                        .child(listId).removeValue()
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
                                Toast.makeText(TaskActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void createPopDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        itemTaskName = view.findViewById(R.id.aListItem);
        btn_saveTask = view.findViewById(R.id.btn_saveList);

        TextView title = view.findViewById(R.id.addList);
        title.setText("Enter Task Name : ");
        itemTaskName.setHint("Task Name");

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        btn_saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemTaskName.getText().toString().isEmpty()) {
                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View v) {

        String newItemNOT = itemTaskName.getText().toString().trim();

        String currentId = user.getUid();

        Task task = new Task();
        task.setTaskName(newItemNOT);
        task.setIsChecked(false);
        task.setListId(listId);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String formattedDate = dateFormat.format(new Date().getTime());
        task.setTimeAdded(formattedDate);

        String taskId = databaseReference.child(currentId).child("List").child(listId).child("Task").push().getKey();
        task.setTaskId(taskId);

        databaseReference.child(currentId).child("List").child(listId).child("Task")
                .child(taskId).setValue(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        alertDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TaskActivity", "onFailure: " + e.getMessage());

                    }
                });
    }

    Task newItem = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(taskList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    newItem = taskList.get(position);

                    builder = new AlertDialog.Builder(TaskActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.popup, null);

                    TextView title = view.findViewById(R.id.addList);

                    itemTaskName = view.findViewById(R.id.aListItem);
                    btn_saveTask = view.findViewById(R.id.btn_saveList);

                    title.setText("Edit Item : ");
                    itemTaskName.setText(newItem.getTaskName());
                    btn_saveTask.setText("Update");

                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();


                    btn_saveTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update our item
                            newItem.setTaskName(itemTaskName.getText().toString());

                            String currentId = user.getUid();

                            if (!itemTaskName.getText().toString().isEmpty()) {

                                databaseReference.child(currentId).child("List")
                                        .child(newItem.getListId()).child("Task")
                                        .child(newItem.getTaskId()).setValue(newItem);
                                recyclerViewTaskAdapter.notifyItemChanged(position);
                                alertDialog.dismiss();

                            } else {
                                Snackbar.make(view, "Field Empty", Snackbar.LENGTH_SHORT).show();

                            }
                        }
                    });

                    break;
            }
        }
    };
}