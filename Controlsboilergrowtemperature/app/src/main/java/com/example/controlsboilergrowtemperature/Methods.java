package com.example.controlsboilergrowtemperature;

import android.graphics.ColorSpace;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Methods {

    @GET("new.json")
    Call<Model> getAllData();

    @POST("getM.php")  // Указываем путь к API
    Call<ResponseBody> postData(@Body RequestBody requestBody);  // Определяем метод для POST-запроса

}