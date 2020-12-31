package adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.akahlouts.todo_list.R;
import com.akahlouts.todo_list.TaskActivity;

import java.util.ArrayList;
import java.util.List;

import model.ListItem;


public class RecyclerViewListAdapter extends Adapter<RecyclerViewListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<ListItem> itemList;
    private List<ListItem> forSearchList;

    public RecyclerViewListAdapter(Context context, List<ListItem> itemList) {
        this.context = context;
        this.itemList = itemList;
        forSearchList = new ArrayList<>(itemList);
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
        public TextView taskNumber;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemListName = itemView.findViewById(R.id.listName);
            taskNumber = itemView.findViewById(R.id.taskNumber);


//            TaskCount taskCount = TaskCount.getInstance();


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ListItem item = itemList.get(position);
                    Intent intent = new Intent(context, TaskActivity.class);
                    intent.putExtra("listName", item.getListName());
                    intent.putExtra("listId",item.getListId());

//                    ListItem item2 = new ListItem();
//                    if (item2.getListId().equals( (Object)item.getListId().toString())){
//                        taskNumber.setText(taskCount.getCurrentId()+" tasks");
//                    }else{
//                        taskNumber.setText("0 tasks");
//                    }

                    context.startActivity(intent);
                }
            });

        }


    }

    @Override
    public Filter getFilter() {
        return itemSearch;
    }

    private Filter itemSearch = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ListItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(forSearchList);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ListItem item : forSearchList){
                    if (item.getListName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
