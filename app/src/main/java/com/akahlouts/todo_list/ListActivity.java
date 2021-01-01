package com.akahlouts.todo_list;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.RecyclerViewListAdapter;
import model.ListItem;

public class ListActivity extends AppCompatActivity {

    private RecyclerViewListAdapter recyclerViewListAdapter;
    private RecyclerView recyclerViewList;

    private EditText itemListName;
    private List<ListItem> itemList;
    private Button btn_saveList;
    private TextView back_list;
    private TextView textview_logout;
    private SearchView search_view;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firebase Database
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    DatabaseReference databaseReference = db.getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        recyclerViewList = findViewById(R.id.recyclerViewList);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));

        back_list = findViewById(R.id.back_list);
        back_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListActivity.this,LoginActivity.class));
                finish();
            }
        });


        String currentId = user.getUid();

        itemList = new ArrayList<>();
        databaseReference.child(currentId).child("List")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for (DataSnapshot lists : snapshot.getChildren()) {
                            ListItem item = lists.getValue(ListItem.class);
                            itemList.add(0, item);
                        }

                        recyclerViewListAdapter = new RecyclerViewListAdapter(ListActivity.this, itemList);
                        recyclerViewList.setAdapter(recyclerViewListAdapter);
                        recyclerViewListAdapter.notifyItemInserted(0);
                        recyclerViewList.smoothScrollToPosition(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Drag and Drop Reorder items
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewList);

        textview_logout = findViewById(R.id.textview_logout);
        textview_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(ListActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

        Button btn_createList = findViewById(R.id.btn_createList);
        btn_createList.setOnClickListener(new View.OnClickListener() {
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

        search_view = findViewById(R.id.search_view);
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
                recyclerViewListAdapter.getFilter().filter(newText);
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

    private void createPopDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        itemListName = view.findViewById(R.id.aListItem);
        btn_saveList = view.findViewById(R.id.btn_saveList);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        btn_saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(itemListName.getText().toString())) {
                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View view) {

        String newItemNOL = itemListName.getText().toString().trim();

        String currentId = user.getUid();

        ListItem item = new ListItem();
        item.setListName(newItemNOL);
        String listId = databaseReference.child(currentId).child("List").push().getKey();
        item.setListId(listId);

        databaseReference.child(currentId).child("List")
                .child(listId).setValue(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        alertDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ListActivity", "onFailure: " + e.getMessage());

                    }
                });

    }

    ListItem newItem = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(itemList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    newItem = itemList.get(position);

                    builder = new AlertDialog.Builder(ListActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.popup, null);

                    TextView title = view.findViewById(R.id.addList);

                    itemListName = view.findViewById(R.id.aListItem);
                    btn_saveList = view.findViewById(R.id.btn_saveList);

                    title.setText("Edit Item : ");
                    itemListName.setText(newItem.getListName());
                    btn_saveList.setText("Update");

                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();


                    btn_saveList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //update our item
                            newItem.setListName(itemListName.getText().toString());
                            newItem.setListId(newItem.getListId());

                            String currentId = user.getUid();

                            if (!itemListName.getText().toString().isEmpty()) {
                                databaseReference.child(currentId).child("List")
                                        .child(newItem.getListId()).child("listName")
                                        .setValue(newItem.getListName());
                                recyclerViewListAdapter.notifyItemChanged(position);

                                alertDialog.dismiss();
                            } else {
                                Snackbar.make(view, "Field is Empty", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }
        }
    };

}