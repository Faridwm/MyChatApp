package com.fwmubarok.mychatapp.My_interface;

import com.fwmubarok.mychatapp.Model.Message;
import com.fwmubarok.mychatapp.Model.SendResponse;
import com.fwmubarok.mychatapp.Model.SubscribedTopicResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FCMinterface {
    String SERVER_KEY = "AAAAGSqaEAI:APA91bGL2_rJ2jDls7KwTCGKeHx3S6s4YDTjKwG6Ni9t4kuAnElL7qUByG6PJ-kisdxkSJTLXbSZ-ZlSH3UZLd_5uPhONq43VrIm0wQ_IVOTJeEQ994ab3Usq5TI--U25ZDIiq8phxVn";

    @Headers("Authorization:key=AAAAGSqaEAI:APA91bGL2_rJ2jDls7KwTCGKeHx3S6s4YDTjKwG6Ni9t4kuAnElL7qUByG6PJ-kisdxkSJTLXbSZ-ZlSH3UZLd_5uPhONq43VrIm0wQ_IVOTJeEQ994ab3Usq5TI--U25ZDIiq8phxVn")
    @POST("send")
    Call<SendResponse> sendMessage(@Body Message message);

    @Headers("Authorization:key="+SERVER_KEY)
    @GET("{dvc_token}")
    Call<SubscribedTopicResponse> getSubscribedTopic(@Path("dvc_token") String token, @Query("details") String details);
}
