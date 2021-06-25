package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements TopicAdapter.OnTopicListener {

    private static final String TAG = "MainActivity";
    private String token;
    private ArrayList<ArrayList<String>> topics = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FCMinterface fcm_interface;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TopicAdapter topicAdapter;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiClient.BASE_URL = "https://iid.googleapis.com/";
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
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setLayoutManager(layoutManager);
        mainActivity = this;

        /* populate static array
        ArrayList<String> s1 = new ArrayList<>();
        s1.add("Topic 01"); // topic name
        s1.add("Ada anak bertanya pada bapaknya"); // topic last message

        ArrayList<String> s2 = new ArrayList<>();
        s2.add("Topic 02"); // topic name
        s2.add("Ada anak bertanya pada bapaknya"); // topic last message

        topics.add(s1);
        topics.add(s2);
        Log.d(TAG, "ArrayList topics: " + topics); */
    }

    // onCreateOptionsMenu and onOptionsItemSelected are both method to handle action bar
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.subscribe:
                Intent intent = new Intent(this, SubscribeTopicActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                if (subscribedTopicResponse.getRel() != null) {
                    Set<String> set_topic = subscribedTopicResponse.getRel().getTopics().keySet();
                    getLastMessage(set_topic);
                    Log.d(TAG, "onResponse: " + subscribedTopicResponse.getRel().getTopics().keySet());
                }
            }

            @Override
            public void onFailure(Call<SubscribedTopicResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void getLastMessage(Set<String> topics) {
        for (String topic: topics) {
            ArrayList<String> topic_lastMsg = new ArrayList<>();
            Query query_lastMsg = databaseReference.child("db_chat").child("chat").child(topic);
            query_lastMsg.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        topic_lastMsg.add(topic);
                        for(DataSnapshot s_child: snapshot.getChildren()) {
                            ReadMessageTopic read = s_child.getValue(ReadMessageTopic.class);
                            topic_lastMsg.add(read.getMessage());
                            topic_lastMsg.add(read.getTimestamp());
                            addToTopics(topic_lastMsg);
                            Log.d(TAG, "onDataChange: Topic=" + topic + " Message=" + read.getMessage());
                        }
                    } else {
                        topic_lastMsg.add(topic);
                        topic_lastMsg.add("");
                        topic_lastMsg.add("");
                        addToTopics(topic_lastMsg);
                        Log.d(TAG, "onDataChange: Topic=" + topic + " Message Kosong");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: " + error.getMessage());
                }
            });
        }
    }

    void addToTopics(ArrayList<String> topicLastMessage) {
        topics.add(topicLastMessage);
        Log.d(TAG, "Array Topics : " + topics);

        topicAdapter = new TopicAdapter(topics, this);
        recyclerView.setAdapter(topicAdapter);
    }

    public void SubscribeToTopic(String topic, boolean isNew) {
        if (isNew) {
//            if (!TopicIsExists(topic)) {
//                Log.d(TAG, "SubscribeToTopic: Topic Tidak ditemukan");
//            }
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

    public String GenerateNewTopic() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    @Override
    public void onTopicClick(int position) {
        Log.d(TAG, "onTopicClick: Clickity Clackity Mantab");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Topics", topics.get(position));
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(ChatActivity.EXTRA_TOKEN, token);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    // Divider class
    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);

            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}