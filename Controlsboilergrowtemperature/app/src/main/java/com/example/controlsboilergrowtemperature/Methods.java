package com.example.controlsboilergrowtemperature;

import com.google.firebase.auth.FirebaseAuth;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface Methods {

    @GET("{user}")
    Call<Model> getUser(@Path("user") String userId);


    @POST("getM.php")
    Call<ResponseBody> postData(@Body RequestBody requestBody);

}