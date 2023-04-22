package com.example.controlsboilergrowtemperature;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button getDataBtn;
    private TextView getDataDisplay;
    private String getDataDisplayHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDataBtn = findViewById(R.id.getDataBtn);
        getDataDisplay = findViewById(R.id.getDataDisplay);
        getDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                Call<Model> call = methods.getAllData();

                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        Log.e(TAG, "onResponse: code :" + response.code());

                       ArrayList<Model.data> data = response.body().getData();
                        getDataDisplayHelper = "";
                        for (Model.data dataCount : data) {
                            Log.e(TAG, "onResponse: emails :" + dataCount.getEmail());
                            getDataDisplayHelper += dataCount.getEmail() + "\n";
                        }
                        getDataDisplay.setText(getDataDisplayHelper);
                    }

                    @Override
                    public void onFailure(Call<Model> call, Throwable t) {
                        Log.e(TAG, "onFailure: emails :" + t.getMessage());
                    }
                });
            }
        });
    }
}