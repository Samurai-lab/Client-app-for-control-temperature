package com.example.controlsboilergrowtemperature;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AnswerAPI {

    @GET("{user}")
    Call<AnswerModel> getUser(@Path("user") String userId);
}
