package com.fwmubarok.mychatapp;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "My firebase mgs service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log data to Log Cat
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String[] str = remoteMessage.getFrom().split("/");
        String topic = str[2];
        Log.d(TAG, "Topic: " + topic);


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("Pesan", "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            createNotification(remoteMessage.getMessageId(), remoteMessage.getNotification().getBody(), topic);
        }

    }

    private void createNotification(String message_id, String messageBody, String topic) {
        Log.d(TAG, "createNotification: masuk ke notif builder");
        int NOTIFICATION_ID =  (int) System.currentTimeMillis();
        String CHANNEL_ID = message_id;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Test Channel";
            String description = "Any will do";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setAutoCancel(true)
                        .setContentTitle("New Message From " + topic)
                        .setContentText(messageBody);

        ArrayList<String> topics = new ArrayList<>();
        topics.add(topic);

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Topics", topics);
        Intent notificationIntent = new Intent(this, ChatActivity.class);
        notificationIntent.putExtras(bundle);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        manager.notify(NOTIFICATION_ID, builder.build());

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "Refreshed Token: " + s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
        Log.d(TAG, "FCM demo: sendRegistrationToServer token: " + token);
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        Log.d("Sending", "Message Sent: " + s );
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        super.onSendError(s, e);
        Log.d("Sending Error", "Message Error: " + s );
    }
}
