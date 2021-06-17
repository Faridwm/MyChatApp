package com.fwmubarok.mychatapp.My_interface;

import com.fwmubarok.mychatapp.Model.Message;
import com.fwmubarok.mychatapp.Model.SendResponse;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMinterface {
    public String SERVER_KEY = "AAAAGSqaEAI:APA91bGL2_rJ2jDls7KwTCGKeHx3S6s4YDTjKwG6Ni9t4kuAnElL7qUByG6PJ-kisdxkSJTLXbSZ-ZlSH3UZLd_5uPhONq43VrIm0wQ_IVOTJeEQ994ab3Usq5TI--U25ZDIiq8phxVn";

    @Headers("Authorization:key=AAAAGSqaEAI:APA91bGL2_rJ2jDls7KwTCGKeHx3S6s4YDTjKwG6Ni9t4kuAnElL7qUByG6PJ-kisdxkSJTLXbSZ-ZlSH3UZLd_5uPhONq43VrIm0wQ_IVOTJeEQ994ab3Usq5TI--U25ZDIiq8phxVn")
    @POST("send")
    Call<SendResponse> sendMessage(@Body Message message);
}
