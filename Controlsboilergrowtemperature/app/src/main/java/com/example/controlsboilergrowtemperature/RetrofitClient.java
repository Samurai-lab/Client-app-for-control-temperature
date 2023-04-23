package com.example.controlsboilergrowtemperature;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit postRetrofitInstance = new Retrofit.Builder()
            .baseUrl("http://x958887o.beget.tech/")  // Указываем базовый URL API
            .addConverterFactory(GsonConverterFactory.create())  // Добавляем конвертер для обработки JSON-ответов
            .build();

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            String BASE_URL = "http://x958887o.beget.tech/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
