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

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ContextWrapper;
import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cmput301w18t05.taskzilla.request.RequestManager;
import com.cmput301w18t05.taskzilla.request.command.AddNotificationRequest;
import com.cmput301w18t05.taskzilla.request.command.GetNotificationsByUserIdRequest;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andy on 4/4/2018.
 */

/**
 * NotificationManager
 *
 * Singleton object that deals with notifications of the current user.
 *
 * @author Andy Li
 * @version 1.0
 */

public class NotificationManager extends ContextWrapper {

    // Taken from https://code.tutsplus.com/tutorials/android-o-how-to-use-notification-channels--cms-28616
    // 2018/04/04

    //public static final String CHANNEL_ID = "com.cmput301w18t05.taskzilla.ANDROID";
    public static final String CHANNEL_ID = "com.cmput301w18t05.taskzilla";
    public static final String ANDROID_CHANNEL_NAME = "Android Channel";
    private int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
    private android.app.NotificationManager mManager;
    private static NotificationManager instance = null;
    private TabLayout tabs;
    private int count = 0;

    protected NotificationManager(Context context, TabLayout tabs) {
        super(context);
        this.tabs = tabs;

        System.out.println("Setting up notification poller");
        new pollNotifications(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels(context);
        }
    }

    public static NotificationManager getInstance(Context context, TabLayout tabs) {
        if(instance == null) {
            instance = new NotificationManager(context, tabs);
        }
        return instance;
    }

    public static NotificationManager getInstance() {
        return instance;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannels(Context context) {
        NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID, ANDROID_CHANNEL_NAME, importance);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLightColor(Color.BLUE);
        androidChannel.setImportance(android.app.NotificationManager.IMPORTANCE_HIGH);

        androidChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);

        mManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.createNotificationChannel(androidChannel);
    }

    public void createNotification(Notification notification) {
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notification.getNotificationIntent(), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(notification.getUser().getPhoto().StringToBitmap())
                .setContentTitle(notification.getEvent())
                .setContentText(notification.getContext())
                .setLights(Color.BLUE, 300, 100)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(android.app.Notification.DEFAULT_ALL);
        //.setContentIntent(pendingIntent);

        Random rand = new Random();
        int id = rand.nextInt(1000)+1;

        mManager.notify(id, mBuilder.build());
    }

    public void sendNotification(Notification notification) {
        AddNotificationRequest task = new AddNotificationRequest(notification);
        RequestManager.getInstance().invokeRequest(task);
    }

    public void notificationCallback(ArrayList<Notification> newNotifs) {
        System.out.println("Notification received.");

        for (Notification n : newNotifs) {
            if(!n.isAcknowledged()) {
                count += 1;
                n.acknowledge();
                createNotification(n);
                updateBadge();
                System.out.println("Received: "+n);
            }
        }
    }

    // decreases count
    public void decrementCount() {
        this.count -= 1;
    }

    // Taken from https://stackoverflow.com/questions/31968162/android-tablayout-tabs-with-notification-badge-like-whatsapp/40493102#40493102
    // 2018-04-08

    // updates badge icon
    public void updateBadge() {
        if(tabs.getTabAt(3) != null && tabs.getTabAt(3).getCustomView() != null) {
            TextView badge = (TextView) tabs.getTabAt(3).getCustomView().findViewById(R.id.badge);
            if(badge != null) {
                badge.setText(count + "");
            }
            View v = tabs.getTabAt(3).getCustomView().findViewById(R.id.badgeContainer);
            if(v != null && count != 0) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.INVISIBLE);
            }
        }
    }

    // Called in the beginning of the app to get current amount of notifications to users
    public void countNotifications(){
        GetNotificationsByUserIdRequest task = new GetNotificationsByUserIdRequest(currentUser.getInstance().getId());
        RequestManager.getInstance().invokeRequest(task);

        for(Notification n : task.getResult())
            count += 1;
    }

    public static class pollNotifications extends AsyncTask<Void, Void, Void> {
        NotificationManager listener;
        public pollNotifications(NotificationManager listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                while (true) {
                    Thread.sleep(5000);
                    GetNotificationsByUserIdRequest task = new GetNotificationsByUserIdRequest(currentUser.getInstance().getId());
                    RequestManager.getInstance().invokeRequest(task);
                    System.out.println("Trying to get notification for user");

                    ArrayList<Notification> newNotifs = task.getResult();
                    if (!newNotifs.isEmpty()) {
                        listener.notificationCallback(newNotifs);
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Something went wrong with the poll notifications");
            }
            return null;
        }
    }
}
