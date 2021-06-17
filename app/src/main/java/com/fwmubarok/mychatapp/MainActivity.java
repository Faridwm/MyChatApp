package com.fwmubarok.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fwmubarok.mychatapp.Model.Message;
import com.fwmubarok.mychatapp.Model.SendResponse;
import com.fwmubarok.mychatapp.My_interface.FCMinterface;
import com.fwmubarok.mychatapp.REST.ApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private final String topic = "test";
    private String dvc_token = "e2vrdkAzTEy25cb66sKXAX:APA91bFsKMoG1WA4PT3DjZ-0GcwnEe1uLwMuDyfrQQmEocjk1l0BYdJZH0-pVcv3XrLvdCu0M_U17czpRBZAmh0wV-eaB3rfDNcMvQT6pP3SJzhMaL6i9j1M3-BO3urQJe6WUitf6yVt";
    private String dvc_token_api_22 = "dGqJqjcdSZq1XyvP5MSWTk:APA91bHIZJy7d317J2RBENDvjg89WdZxcuKo0SkCQgoeTrcJhUBC2fjt45cnpIUX3XFLXzLzJhVAwzYAs5abRQjamFiKMtqKlndeCKXZ8P6n8Xz335hY2asb1faB9UieQLmDYhtM3T7Z";
    private final String SENDER_ID = "108088922114";
    private FCMinterface fcm_interface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        String token = task.getResult();
                        dvc_token = token;
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        String str_msg = "Halo ini tes kirim";
        HashMap<String, String> data = new HashMap<>();
        data.put("From", topic);
        data.put("To", "");
        data.put("Message", str_msg);

//        Data data = new Data(topic, dvc_token, msg);
        Message message = new Message(topic, data);

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
                Log.d("Sukses", Integer.toString(sr.getSuccess()));
            }

            @Override
            public void onFailure(Call<SendResponse> call, Throwable t) {
                Log.d(TAG, "Message: " + t.getMessage());
            }
        });


//        AtomicInteger msgId = new AtomicInteger();
//
//        String to = SENDER_ID + "@fcm.googleapis.com";
//
//
//        Date date = new Date();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//        String strDate = dateFormat.format(date);
//

//
//        RemoteMessage message = new RemoteMessage.Builder(to)
//                .setMessageId(String.valueOf(msgId.get()))
//                .setData(msg)
//                .build();
//
//        FirebaseMessaging fm = FirebaseMessaging.getInstance();
//        fm.send(message);




    }
}