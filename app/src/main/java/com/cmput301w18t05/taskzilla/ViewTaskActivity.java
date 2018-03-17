package com.cmput301w18t05.taskzilla;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;


public class ViewTaskActivity extends AppCompatActivity {
    private String taskID;
    private ViewTaskController viewTaskController;
    private Task task;
    private String taskStatus;
    private String currentUserId;
    private String taskUserId;
    private String description;
    private User TaskRequester;
    private User TaskProvider;
    private String taskName;

    private TextView ProviderName;
    private TextView DescriptionView;
    private TextView RequesterName;
    private TextView TaskName;

    private ImageButton EditButton;
    private ImageButton DeleteButton;
    private ImageButton ProviderPicture;
    private ImageButton RequesterPicture;

    private Button PinkButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        EditButton = findViewById(R.id.EditButton);
        DeleteButton = findViewById(R.id.DeleteButton);
        ProviderPicture = findViewById(R.id.ProviderPicture);
        RequesterPicture = findViewById(R.id.RequesterPicture);
        ProviderName = findViewById(R.id.ProviderName);
        DescriptionView = findViewById(R.id.Description);
        RequesterName = findViewById(R.id.RequesterName);
        TaskName = findViewById(R.id.TaskName);
        PinkButton = findViewById(R.id.PinkButton);

        this.viewTaskController = new ViewTaskController(this.findViewById(android.R.id.content),this);
        taskID = getIntent().getStringExtra("TaskId");

        viewTaskController.setTaskID(taskID);
        viewTaskController.getTaskRequest();
        task = viewTaskController.getTask();

        currentUserId = currentUser.getInstance().getId();
        taskUserId = task.getTaskRequester().getId();

        taskName = task.getName();
        taskStatus = task.getStatus();
        description = task.getDescription();

        TaskRequester = task.getTaskRequester();
        try {
            TaskProvider = task.getTaskProvider();
        }
        catch (Exception e){
            TaskProvider = new User();
        }
        RequesterName.setText(TaskRequester.getName());
        DescriptionView.setText(description);
        TaskName.setText(taskName);
        PinkButton.setText("PLACE BID");

        if (currentUserId.equals(taskUserId) && taskStatus.equals("requested")) {
            EditButton.setVisibility(View.VISIBLE);
        } else {
            EditButton.setVisibility(View.INVISIBLE);
        }
        if (currentUserId.equals(taskUserId)) {
            DeleteButton.setVisibility(View.VISIBLE);
        } else {
            DeleteButton.setVisibility(View.INVISIBLE);
        }
        if (taskStatus.equals("assigned")) {
            ProviderPicture.setVisibility(View.VISIBLE);
            ProviderName.setVisibility(View.VISIBLE);
            ProviderName.setText(TaskProvider.getName());
        }

        //Provider Profile
        ProviderPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("user id", TaskProvider.getId());
                    startActivity(intent);
                }
                catch (Exception e){

                }
            }

        });

        //Requester Profile
        RequesterPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("user id", TaskRequester.getId());
                    startActivity(intent);
                }
                catch (Exception e){

                }
            }
        });

        //Edit Task Button
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditTaskActivity.class);
                intent.putExtra("task Name", taskName);
                intent.putExtra("Description", description);
                startActivityForResult(intent, 1);
            }
        });

        //Implement delete button
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewTaskActivity.this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete?");
                //DELETE CODE
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                //DELETE CANCEL CODE
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }

        });

        ArrayList<Bid> bidsList = new ArrayList<>();


        bidsList.add(new Bid(new User(), 1.0f));
        bidsList.add(new Bid(new User(), 1.0f));
        bidsList.add(new Bid(new User(), 1.0f));
        bidsList.add(new Bid(new User(), 1.0f));
        bidsList.add(new Bid(new User(), 1.0f));

    }


    /**
     * thePinkButton
     * upon pressing place button on task page
     * depending on if the user viewing the task is the owner of task or someone else
     * if they are the owner
     * show a dialog or something for the user to select a bid to accept
     * otherwise
     * prompts user to enter in a bid amount
     * if valid input, will add bid to task
     * <p>
     * notes
     * can probably add more stuff to dialog
     *
     * @author myapplestory
     */
    public void thePinkButton(android.view.View view) {

        // if this task's requester is the current logged in user
        // show a dialog or fragment where the requester can select which bid to accept
        // otherwise

        final AlertDialog mBuilder = new AlertDialog.Builder(ViewTaskActivity.this).create();
        final View mView = getLayoutInflater().inflate(R.layout.dialog_place_bid, null);

        final EditText incomingBidText = mView.findViewById(R.id.place_bid_edittext);
        Button submitBidButton = mView.findViewById(R.id.submit_bid_button);

        submitBidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float incomingBidFloat;
                try {
                    incomingBidFloat = Float.parseFloat(incomingBidText.getText().toString());
                    incomingBidFloat = (float) (Math.round(incomingBidFloat * 100.0) / 100.0);
                } catch (Exception exception) {
                    Toast.makeText(ViewTaskActivity.this,
                            "Please enter in a valid bid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                // do stuff here to actually add bid








                Toast.makeText(ViewTaskActivity.this,
                        incomingBidFloat.toString(), Toast.LENGTH_SHORT).show();
                mBuilder.dismiss();
            }
        });
        mBuilder.setView(mView);
        mBuilder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (1): {
                //code to add to ESC
                if (resultCode == RESULT_OK) {
                    taskName = data.getStringExtra("Task Name");
                    description = data.getStringExtra("Description");
                    task.setName(taskName);
                    task.setDescription(description);
                    viewTaskController.updateTaskRequest(task);
                    TextView DescriptionView = (TextView) findViewById(R.id.Description);
                    TextView TaskNameView = (TextView) findViewById(R.id.TaskName);
                    TaskNameView.setText(taskName);
                    if (description.length() > 0) {
                        DescriptionView.setText(description);
                    } else {
                        DescriptionView.setText("No Description");
                    }
                }
            }
        }
    }
}

