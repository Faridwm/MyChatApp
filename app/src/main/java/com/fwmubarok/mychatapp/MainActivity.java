package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fwmubarok.mychatapp.Adapter.MessageAdapter;
import com.fwmubarok.mychatapp.Model.Message;
import com.fwmubarok.mychatapp.Model.Notification;
import com.fwmubarok.mychatapp.Model.ReadMessageTopic;
import com.fwmubarok.mychatapp.Model.SendResponse;
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
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private final String topic = "test";
    public String token;
    private String dvc_token = "e2vrdkAzTEy25cb66sKXAX:APA91bFsKMoG1WA4PT3DjZ-0GcwnEe1uLwMuDyfrQQmEocjk1l0BYdJZH0-pVcv3XrLvdCu0M_U17czpRBZAmh0wV-eaB3rfDNcMvQT6pP3SJzhMaL6i9j1M3-BO3urQJe6WUitf6yVt";
    private String dvc_token_api_22 = "dGqJqjcdSZq1XyvP5MSWTk:APA91bHIZJy7d317J2RBENDvjg89WdZxcuKo0SkCQgoeTrcJhUBC2fjt45cnpIUX3XFLXzLzJhVAwzYAs5abRQjamFiKMtqKlndeCKXZ8P6n8Xz335hY2asb1faB9UieQLmDYhtM3T7Z";
    private final String SENDER_ID = "108088922114";
    private FCMinterface fcm_interface;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MessageAdapter messageAdapter;
    private MainActivity mainActivity;

    private ArrayList<ReadMessageTopic> readMessageTopics = new ArrayList<>();

    private EditText et_text_message;
    private ImageView btn_send;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list_message);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

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
//                        dvc_token = token;
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

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

        btn_send = findViewById(R.id.btn_send_1);
        et_text_message = (EditText)findViewById(R.id.text_message);

        // initial button_send state
        btn_send.setVisibility(View.GONE);

        // listener to make button_send invisible when edit_text is empty
        et_text_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    btn_send.setVisibility(View.GONE);
                } else {
                    btn_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // on click button_send
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_text_message.onEditorAction(EditorInfo.IME_ACTION_DONE);
                SendMessage(et_text_message.getText().toString());
                et_text_message.setText("");
            }
        });

        getMessage(topic);
    }


    public void getMessage(String topic) {

        Query message_topic_query =  databaseReference.child("db_chat").child("chat").child(topic).orderByChild("timestamp");
        message_topic_query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Log.d("On Child Added", snapshot.getKey());
                ReadMessageTopic msg_topic = snapshot.getValue(ReadMessageTopic.class);
                msg_topic.setMessage_id(snapshot.getKey());

                readMessageTopics.add(msg_topic);
                for (int i = 0; i < readMessageTopics.size(); i++) {
                    Log.d(TAG, "listMessage[" + i + "] = " + readMessageTopics.get(i).getTimestamp());
                }
                /*Collections.sort(readMessageTopics, new Comparator<ReadMessageTopic>() {
                    @Override
                    public int compare(ReadMessageTopic o1, ReadMessageTopic o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                });*/
                messageAdapter = new MessageAdapter(readMessageTopics, token);
                recyclerView.setAdapter(messageAdapter);

                /*String output = "";
                for (int i = 0; i < readMessageTopics.size(); i++) {
                    output += readMessageTopics.get(i).getMessage() + "-----" + readMessageTopics.get(i).getTimestamp() + "\n";
                }
                tv_db_read.setText(output);*/
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void SendMessage(String str_msg){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        String str_msg = "Halo ini tes kirim";

        String send_from = token;
        String send_to = topic;
        String strDate = dateFormat.format(date);

        HashMap<String, String> data = new HashMap<>();
        data.put("From", send_from);
        data.put("To", send_to);
        data.put("Message", str_msg);
        data.put("Timestamp", strDate);

        Notification notification = new Notification(str_msg);

//        Data data = new Data(topic, dvc_token, msg);
        Message message = new Message(topic, data, notification);

//        Log.d("topic", message.getTopic());

        Call<SendResponse> call = fcm_interface.sendMessage(message);
        call.enqueue(new Callback<SendResponse>() {
            @Override
            public void onResponse(Call<SendResponse> call, Response<SendResponse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "Code: " + response.code());
                    Log.d(TAG, "Message"+ response.toString());
                    return;
                }

                SendResponse sr = response.body();
                Log.d("Sukses Kirim", Long.toString(sr.getMessage_id()));
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("message");
//
//                myRef.setValue("Hello, World!");

                SendToDB(sr.getMessage_id(), send_from, send_to, str_msg, strDate);

            }

            @Override
            public void onFailure(Call<SendResponse> call, Throwable t) {
                Log.d(TAG, "Message: " + t.getMessage());
            }
        });
    }

    public void SendToDB(long message_id, String from, String to, String message, String timestamp) {
        Log.d("DB CRUD", "Masuk Ke Fungsi SendToDB");


        HashMap<String, Object> data = new HashMap<>();
//        data.put("message_id", message_id);
        data.put("from", from);
        data.put("to", to);
        data.put("message", message);
        data.put("timestamp", timestamp);

        databaseReference.child("db_chat").child("chat").child(topic).child(Long.toString(message_id)).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Write_DB", "Berhasil Menulis Ke DB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Write_DB", "Gagal Menulis Ke DB");
                    }
                });
    }

    public void SendMessage_2(String str_msg) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(date);
        AtomicInteger msgId = new AtomicInteger();

        String to = SENDER_ID + "@fcm.googleapis.com";

        HashMap<String, String> data = new HashMap<>();
        data.put("From", topic);
        data.put("To", "");
        data.put("Message", str_msg);
        data.put("Timestamp", strDate);
        RemoteMessage message = new RemoteMessage.Builder(to)
                .setMessageId(String.valueOf(msgId.get()))
                .setData(data)
                .build();

        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(message);
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}