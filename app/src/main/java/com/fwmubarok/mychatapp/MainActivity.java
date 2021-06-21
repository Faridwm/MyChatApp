package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fwmubarok.mychatapp.Adapter.TopicAdapter;
import com.fwmubarok.mychatapp.Model.ReadMessageTopic;
import com.fwmubarok.mychatapp.Model.SubscribedTopicResponse;
import com.fwmubarok.mychatapp.My_interface.FCMinterface;
import com.fwmubarok.mychatapp.REST.ApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String token;
    private ArrayList<ArrayList<String>> topics = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ApiClient apiClient;
    private FCMinterface fcm_interface;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TopicAdapter topicAdapter;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiClient.BASE_URL = "https://iid.googleapis.com/iid/info/";
        fcm_interface = ApiClient.getClient().create(FCMinterface.class);
        databaseReference = FirebaseDatabase.getInstance("https://mychatapp-8a494-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        getSubscribedTopic(token);
                    }
                });

        recyclerView = findViewById(R.id.topic_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        mainActivity = this;

        // populate static array
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("Topic 01"); // topic name
        s1.add("Ada anak bertanya pada bapaknya"); // topic last message

        ArrayList<String> s2 = new ArrayList<>();
        s2.add("Topic 02"); // topic name
        s2.add("Ada anak bertanya pada bapaknya"); // topic last message

        topics.add(s1);
        topics.add(s2);
        Log.d(TAG, "ArrayList topics: " + topics);

        topicAdapter = new TopicAdapter(topics);
        recyclerView.setAdapter(topicAdapter);
    }

    public void getSubscribedTopic(String token) {
        Call<SubscribedTopicResponse> call = fcm_interface.getSubscribedTopic(token, "true");
        call.enqueue(new Callback<SubscribedTopicResponse>() {
            @Override
            public void onResponse(Call<SubscribedTopicResponse> call, Response<SubscribedTopicResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Code: " + response.code());
                    Log.d(TAG, "Message"+ response.toString());
                    return;
                }
                SubscribedTopicResponse subscribedTopicResponse = response.body();
                Log.d(TAG, "onResponse: " + subscribedTopicResponse.getRel().getTopics().keySet());
                Set<String> set_topic = subscribedTopicResponse.getRel().getTopics().keySet();
                getLastMessage(set_topic);
            }

            @Override
            public void onFailure(Call<SubscribedTopicResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void getLastMessage(Set<String> topics) {
//        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (String topic: topics) {
            ArrayList<String> topic_lastMsg = new ArrayList<>();
            Query query_lastMsg = databaseReference.child("db_chat").child("chat").child(topic);
            query_lastMsg.orderByChild("timestamp").limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    topic_lastMsg.add(topic);
                    ReadMessageTopic read = snapshot.getValue(ReadMessageTopic.class);
//                    topic_lastMsg.add(read.getMessage());
                    Log.d(TAG, "onDataChange: Topic=" + topic + " Message=" + read.getMessage());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });

        }
    }


    public void SubscribeToTopic(String topic, boolean isNew) {
        if (isNew) {
            if (!TopicIsExists(topic)) {
                Log.d(TAG, "SubscribeToTopic: Topic Tidak ditemukan");
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Subscribe Topic", "Berhasil subscribe ke topik " + topic);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Subscribe Topic", "Gagal subscribe ke topik " + topic + "\nError: " + e.getMessage());
                    }
                });

    }

    public boolean TopicIsExists(String topic) {
        final boolean[] isExists = {true};
        Query topic_query = databaseReference.child("db_chat").child("chat");
        topic_query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(topic)) {
                    isExists[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

        return isExists[0];
    }

    public String GenerateNewTopic() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}