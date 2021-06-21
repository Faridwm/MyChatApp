package com.fwmubarok.mychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.fwmubarok.mychatapp.Adapter.TopicAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ArrayList<ArrayList<String>> topics = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TopicAdapter topicAdapter;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        topicAdapter= new TopicAdapter(topics);
        recyclerView.setAdapter(topicAdapter);
    }
}