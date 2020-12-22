package com.akahlouts.todo_list;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akahlouts.todo_list.adapter.RecyclerViewListAdapter;
import com.akahlouts.todo_list.model.ListItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerViewListAdapter recyclerViewListAdapter;
    private RecyclerView recyclerViewList;

    private EditText itemListName;
    private List<ListItem> itemList;
    private Button btn_saveList;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerViewList = findViewById(R.id.recyclerViewList);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();

        recyclerViewListAdapter = new RecyclerViewListAdapter(this, itemList);
        recyclerViewList.setAdapter(recyclerViewListAdapter);

        //Drag and Drop Reorder items
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewList);

        Button btn_createList = findViewById(R.id.btn_createList);
        btn_createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });
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
                if (!itemListName.getText().toString().isEmpty()) {
                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View view) {

        ListItem item = new ListItem();

        String newItemNOL = itemListName.getText().toString().trim();

        item.setListName(newItemNOL);
        itemList.add(0,item);
        recyclerViewListAdapter.notifyItemInserted(0);
        recyclerViewList.smoothScrollToPosition(0);

        alertDialog.dismiss();

    }

    ListItem deletedItem = null;
    ListItem newItem = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
                case ItemTouchHelper.LEFT:
                    deletedItem = itemList.get(position);
                    itemList.remove(deletedItem);
                    recyclerViewListAdapter.notifyItemRemoved(position);
                    String nameList = deletedItem.getListName();
                    Snackbar.make(recyclerViewList, nameList + " DELETED", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    itemList.add(position, deletedItem);
                                    recyclerViewListAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;

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

                            if (!itemListName.getText().toString().isEmpty()){
//                                recyclerViewListAdapter.notifyItemChanged(position,newItem);

                                itemList.remove(newItem);
                                recyclerViewListAdapter.notifyItemRemoved(position);

                                itemList.add(position, newItem);
                                recyclerViewListAdapter.notifyItemInserted(position);
                                alertDialog.dismiss();

                                Snackbar.make(view,"Updated done",Snackbar.LENGTH_SHORT).show();
                            }else {
                                Snackbar.make(view,"Field Empty",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }
        }
    };

}