package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SubscribeTopicActivity extends AppCompatActivity {
    private final static String TAG = "SubscribeTopicActivity";


    private DatabaseReference databaseReference;
    private String topic;
    private ArrayList<String> topics = new ArrayList<>();

    private EditText edt_text_topic;
    private Button btn_find_topic, btn_new_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_topic);

        databaseReference = FirebaseDatabase.getInstance("https://mychatapp-8a494-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        edt_text_topic = findViewById(R.id.input_topic_id);
        btn_find_topic = findViewById(R.id.button_find_topic);
        btn_new_topic = findViewById(R.id.button_generate_topic);

        getAllTopics();

        btn_new_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_topic = GenerateTopic();
                Log.d(TAG, "New Topic: " + new_topic);
                CreateNewTopic(new_topic);
            }
        });

        btn_find_topic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_text_topic.onEditorAction(EditorInfo.IME_ACTION_DONE);
                topic = edt_text_topic.getText().toString();
                if (topic.equals("")) {
                    Toast.makeText(SubscribeTopicActivity.this, "Topic ID tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    if (TopicIsExists(topic)) {
                        Log.d(TAG, "Topik Ditemukan");
                        SubscribeToTopic(topic);
                    } else {
                        Log.d(TAG, "Topik Tidak Ditemukan");
                        Toast.makeText(SubscribeTopicActivity.this, "Topic Tidak Ditemukan", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

    public void getAllTopics() {
        Query get_topic = databaseReference.child("db_chat").child("topics");
        get_topic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sp: snapshot.getChildren()) {
                    Map<String, String> data_topic = (Map) sp.getValue();
                    add_topics(data_topic.get("topic_name"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: DB Error: " + error.getMessage());
            }
        });
    }

    private void add_topics(String topic) {
        topics.add(topic);
        Log.d(TAG, "Array Topics : " + topics);
    }

    private boolean TopicIsExists(String topic) {
        return topics.contains(topic);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(this, MainActivity.class));
        return;
    }
}
