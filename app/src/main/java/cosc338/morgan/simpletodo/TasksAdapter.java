package cosc338.morgan.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//responsible for displaying data from the model into a row in the recycler view
public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    public interface OnClickListener{
        void onItemClicked(int position);
    }

    public interface OnLongClickListener{
        void onTaskLongClicked(int position);
    }

    List<String> tasks;

    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    public TasksAdapter(List<String> tasks, OnLongClickListener longClickListener, OnClickListener clickListener) {
        this.tasks = tasks;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    //creating each view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //use layout inflator to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        //wrap it inside a view holder and return it
        return new ViewHolder(todoView);
    }

    //taking data at a particular view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //grab the item at the position
        String task = tasks.get(position);
        //bind the item into the specified view holder
        holder.bind(task);

    }

    //tells the recycler view how many items are in the list
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    //container to provide easy access to views that represent each row in the list
    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTask = itemView.findViewById(android.R.id.text1);
        }

        //update the view inside of the view holder with this data
        public void bind(String task) {

            tvTask.setText(task);
            tvTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
            tvTask.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //notifying listener which position an item is inserted
                    longClickListener.onTaskLongClicked(getAdapterPosition());
                    return false;
                }
            });
        }
    }
}
