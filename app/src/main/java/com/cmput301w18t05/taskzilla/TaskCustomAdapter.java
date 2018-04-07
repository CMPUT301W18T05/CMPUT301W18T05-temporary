/*
 * Copyright 2018 (c) Andy Li, Colin Choi, James Sun, Jeremy Ng, Micheal Nguyen, Wyatt Praharenka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cmput301w18t05.taskzilla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskCustomAdapter extends ArrayAdapter<Task> {

    public TaskCustomAdapter(Context context, int layoutResource, ArrayList<Task> taskList) {
        super(context, layoutResource, taskList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tasks_list_view2, parent, false);
        }
        TextView taskTitleView = (TextView) convertView.findViewById(R.id.taskTitle);
        TextView requesterUsernameView = (TextView) convertView.findViewById(R.id.requesterUsername);
        TextView taskStatusView = (TextView) convertView.findViewById(R.id.taskStatus);
        TextView lowestBidView = (TextView) convertView.findViewById(R.id.lowestBid);
        taskTitleView.setText(task.getName());
        requesterUsernameView.setText("Requester: " + task.getRequesterUsername());
        taskStatusView.setText("Status: " + task.getStatus());

        try {
            Float bestBid = task.getBestBid();
            if (bestBid <= 0) {
                lowestBidView.setText("Lowest Bid: None");
            } else {
                lowestBidView.setText("Lowest Bid: " + Float.toString(bestBid));
            }
        } catch (Exception e){
            lowestBidView.setText("Lowest Bid: None");
        }
        return convertView;
    }
}