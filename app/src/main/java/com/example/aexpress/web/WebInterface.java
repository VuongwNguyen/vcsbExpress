package com.example.aexpress.web;

import com.example.aexpress.model.ChatMessage;
import com.example.aexpress.model.ResponseModel;
import com.example.aexpress.model.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WebInterface {

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseModel> register(
            @Field("name") String name,
            @Field("phone_number") String phone_number
    );


    @GET("users.php")
    Call<List<UserModel>> get_user(
            @Query("phone_number") String phone_number
    );


    @GET("users.php")
    Call<List<UserModel>> get_users();


    @GET("get_messages.php")
    Call<List<ChatMessage>> get_chats(
            @Query("user_id") String user_id,
            @Query("min_time") String min_time
    );


    @FormUrlEncoded
    @POST("send_message.php")
    Call<ResponseModel> send_message(
            @Field("sender") String sender,
            @Field("receiver") String receiver,
            @Field("body") String body
    );

}
