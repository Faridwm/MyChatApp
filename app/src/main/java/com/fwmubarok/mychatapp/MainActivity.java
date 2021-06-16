package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fwmubarok.mychatapp.Model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        String topic = "test";

        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to test topic";
                        if (!task.isSuccessful()) {
                            msg = "Failed Subscribe To test topic";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        FirebaseMessaging.getInstance().send(
                new RemoteMessage.Builder("108088922114" + "@gcm.googleapis.com")
                        .setMessageId("1")
                        .addData("my_message", "Hello World")
                        .addData("my_action","SAY_HELLO")
                        .build());

//        Map<String, String> doubleBraceMap  = new HashMap<String, String>() {{
//            put("score1", "850");
//            put("score2", "900");
//        }};

//        Message message = new Message.Builder()
//                .putData("score", "850")
//                .setTopic(topic)
//                .build();
//
//        String response = FirebaseMessaging.getInstance().send(message);
//
//        System.out.println("Successfully sent message: " + response);
    }
}