package com.cmput301w18t05.taskzilla.request.command;

import android.os.AsyncTask;
import android.util.Log;

import com.cmput301w18t05.taskzilla.ElasticSearchController;
import com.cmput301w18t05.taskzilla.User;
import com.cmput301w18t05.taskzilla.request.InsertionRequest;

import java.util.concurrent.TimeUnit;

/**
 * Created by wyatt on 07/03/18.
 */

public class AddUserRequest extends InsertionRequest {
    User user;
    ElasticSearchController.AddUser task;

    public AddUserRequest(User user) {
        this.user = user;
    }

    public void execute() {
        task = new ElasticSearchController.AddUser();
        task.execute(user); // for now, subject to change.
    }

    public void executeOffline() {
    }

    public boolean getResult() {
        try {
            return task.get();
        }
        catch (Exception e) {
            return false;
        }
    }

}