package cosc338.morgan.simpletodo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> tasks;

    Button buttonAdd;
    EditText editItem;
    RecyclerView rvTasks;
    TasksAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = findViewById(R.id.buttonAdd);
        editItem = findViewById(R.id.editItem);
        rvTasks = findViewById(R.id.rvItem);

        loadTasks();
//        tasks.add("Buy bread");
//        tasks.add("Get gas");
//        tasks.add("Do homework");

        TasksAdapter.OnLongClickListener onLongClickListener = new TasksAdapter.OnLongClickListener(){
            @Override
            public void onTaskLongClicked(int position) {
                //delete the item from the model
                tasks.remove(position);
                //notify adapter which position was deleted
                tasksAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Task was removed", Toast.LENGTH_SHORT).show();
                saveTasks();
            }
        };

        TasksAdapter.OnClickListener onClickListener = new TasksAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("Main Activity", "Single click at position " + position);
                //create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, tasks.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                //display the activity
                //startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };


        tasksAdapter = new TasksAdapter(tasks, onLongClickListener, onClickListener);
        rvTasks.setAdapter(tasksAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoTask = editItem.getText().toString();
                //add item to the model
                tasks.add(todoTask);
                //notify adapter that an item is inserted
                tasksAdapter.notifyItemInserted(tasks.size()-1);
                editItem.setText("");
                Toast.makeText(getApplicationContext(),"Task was added", Toast.LENGTH_SHORT).show();
                saveTasks();
            }
        });
    }

//    handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //retrieve the updated text value
            String taskText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the edited item from the key position
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update the model at the right position with new text
            tasks.set(position, taskText);
            //notify the adapter
            tasksAdapter.notifyItemChanged(position);
            //persist the changes
            saveTasks();
            Toast.makeText(getApplicationContext(), "Task updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    //This function will load items by reading every line of the data file
    //only called when app starts up
    private void loadTasks(){
        try {
            tasks = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading tasks", e);
            tasks = new ArrayList<>();
        }
    }

    //This function saves items by writing them into the data file
    //called whenever we add or remove an item
    private void saveTasks(){
        try {
            FileUtils.writeLines(getDataFile(),tasks);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing tasks", e);
        }
    }
}