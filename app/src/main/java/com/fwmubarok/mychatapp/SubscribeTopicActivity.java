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

import java.util.HashMap;
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

    public void CreateNewTopic(String topic) {
        HashMap<String, String> data = new HashMap<>();
        data.put("topic_name", topic);
        databaseReference.child("db_chat").child("topics").push().setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Write_DB", "Berhasil Menulis Ke DB");
                        SubscribeToTopic(topic);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Write_DB", "Gagal Menulis Ke DB");
                    }
                });
    }

    public String GenerateTopic() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    public void checkTopic(String topic) {
        Query get_topic = databaseReference.child("db_chat").child("topics");
        get_topic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sp: snapshot.getChildren()) {
                    if (sp.getValue() != null) {
                        SubscribeToTopic(topic);
                    } else {
                        String msg = "topic " + topic + " tidak ditemukan";
                        Log.d("Subscribe Topic", msg);
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
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
                        String msg = "Berhasil subscribe ke topik " + topic;
                        Log.d("Subscribe Topic", msg);
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String msg = "Gagal subscribe ke topik " + topic;
                        Log.d("Subscribe Topic", "Gagal subscribe ke topik " + topic + "\nError: " + e.getMessage());
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void UnSubscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String msg = "Berhasil unsubscribe ke topik " + topic;
                        Log.d("Subscribe Topic", msg);
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String msg = "Gagal unsubscribe ke topik " + topic;
                        Log.d("Subscribe Topic", "Gagal subscribe ke topik " + topic + "\nError: " + e.getMessage());
                        Toast.makeText(SubscribeTopicActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
