package com.akahlouts.todo_list;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akahlouts.todo_list.adapter.RecyclerViewListAdapter;
import com.akahlouts.todo_list.model.ListItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerViewListAdapter recyclerViewListAdapter;
    private AlertDialog alertDialog;
    private EditText itemNameOfList;
    private List<ListItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerViewList = findViewById(R.id.recyclerViewList);
        Button btn_createList = findViewById(R.id.btn_createList);

        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();

        recyclerViewListAdapter = new RecyclerViewListAdapter(this, itemList);
        recyclerViewList.setAdapter(recyclerViewListAdapter);

        btn_createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });
    }

    private void createPopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popupforlist, null);
        itemNameOfList = view.findViewById(R.id.aListItem);
        Button btn_saveList = view.findViewById(R.id.btn_saveList);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        btn_saveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemNameOfList.getText().toString().isEmpty()) {
                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View view) {

        ListItem item = new ListItem();

        String newItemNOL = itemNameOfList.getText().toString().trim();

        item.setListName(newItemNOL);
        itemList.add(item);
        recyclerViewListAdapter.notifyDataSetChanged();

        Snackbar.make(view, "Item Saved", Snackbar.LENGTH_SHORT).show();

        alertDialog.dismiss();

    }
}