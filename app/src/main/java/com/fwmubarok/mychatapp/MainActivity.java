package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fwmubarok.mychatapp.Model.Message;
import com.fwmubarok.mychatapp.Model.Notification;
import com.fwmubarok.mychatapp.Model.SendResponse;
import com.fwmubarok.mychatapp.My_interface.FCMinterface;
import com.fwmubarok.mychatapp.REST.ApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private final String topic = "test";
    private String token;
    private String dvc_token = "e2vrdkAzTEy25cb66sKXAX:APA91bFsKMoG1WA4PT3DjZ-0GcwnEe1uLwMuDyfrQQmEocjk1l0BYdJZH0-pVcv3XrLvdCu0M_U17czpRBZAmh0wV-eaB3rfDNcMvQT6pP3SJzhMaL6i9j1M3-BO3urQJe6WUitf6yVt";
    private String dvc_token_api_22 = "dGqJqjcdSZq1XyvP5MSWTk:APA91bHIZJy7d317J2RBENDvjg89WdZxcuKo0SkCQgoeTrcJhUBC2fjt45cnpIUX3XFLXzLzJhVAwzYAs5abRQjamFiKMtqKlndeCKXZ8P6n8Xz335hY2asb1faB9UieQLmDYhtM3T7Z";
    private final String SENDER_ID = "108088922114";
    private FCMinterface fcm_interface;


    private Button btn_send_1, btn_send_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //Test Langsung ketika run.
//        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mychatapp-8a494-default-rtdb.asia-southeast1.firebasedatabase.app");
//        DatabaseReference myRef = database.getReference("mychatapp-8a494-default-rtdb");
//
//        myRef.setValue("Hello, World!");
//
//        //Akhir dari test langsung.

        fcm_interface = ApiClient.getClient().create(FCMinterface.class);

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

        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        btn_send_1 = findViewById(R.id.btn_send_1);
        btn_send_2 = findViewById(R.id.btn_send_2);

        btn_send_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage("Halo ini tes kirim melalui HTTPS");
            }
        });

        btn_send_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage_2("Halo ini tes kirim melalui method");
            }
        });


    }

    public void SendMessage(String str_msg){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

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
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!");

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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://mychatapp-8a494-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference();

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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
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
}