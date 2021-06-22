package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Random;

public class SubscribeTopicActivity extends AppCompatActivity {
    private final static String TAG = "SubscribeTopicActivity";

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_topic);

        databaseReference = FirebaseDatabase.getInstance("https://mychatapp-8a494-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    }

    public String GenerateTopic() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    public void checkTopic(String topic) {
        Query get_topic = databaseReference.child("db_chat").child("chat").child(topic);
        get_topic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    SubscribeToTopic(topic);
                } else {
                    String msg = "topic " + topic + " tidak ditemukan";
                    Log.d("Subscribe Topic", msg);
                    Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: DB Error: " + error.getMessage());
            }
        });
    }

    public void SubscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String msg = "Berhasil subcribe ke topik " + topic;
                        Log.d("Subscribe Topic", msg);
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String msg = "Gagal subcribe ke topik " + topic;
                        Log.d("Subscribe Topic", "Gagal subscribe ke topik " + topic + "\nError: " + e.getMessage());
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
