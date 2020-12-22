package com.akahlouts.todo_list.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.akahlouts.todo_list.R;
import com.akahlouts.todo_list.TaskActivity;
import com.akahlouts.todo_list.model.ListItem;

import java.util.List;


public class RecyclerViewListAdapter extends Adapter<RecyclerViewListAdapter.ViewHolder> {

    private Context context;
    private List<ListItem> itemList;

    public RecyclerViewListAdapter(Context context, List<ListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListAdapter.ViewHolder viewHolder, int position) {
        ListItem item = itemList.get(position);
        viewHolder.itemListName.setText(item.getListName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemListName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemListName = itemView.findViewById(R.id.listName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ListItem item = itemList.get(position);
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("listName", item.getListName());

                    context.startActivity(intent);
                }
            });

        }


    }

}
