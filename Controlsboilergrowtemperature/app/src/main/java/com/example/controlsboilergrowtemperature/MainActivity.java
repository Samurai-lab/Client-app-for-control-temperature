package com.example.controlsboilergrowtemperature;

import static com.example.controlsboilergrowtemperature.RetrofitClient.postRetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView getDataDisplay;
    private EditText editTextTemperature;

    private String getDataDisplayHelper;
    private String getDataDisplayHelperNull;
    private String editTextString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getDataBtn = findViewById(R.id.getDataBtn);
        Button postDataBtn = findViewById(R.id.postDataBtn);
        getDataDisplay = findViewById(R.id.getDataDisplay);
        editTextTemperature = findViewById(R.id.editTextTemperature);


        getDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                Call<Model> call = methods.getAllData();

                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(@NonNull Call<Model> call, @NonNull Response<Model> response) {
                        Log.e(TAG, "onResponse: code :" + response.code());

                        Model model = response.body();

                        getDataDisplay.setText(getDataDisplayHelper);
                        if (model.getTempGor() != null) {
                            getDataDisplayHelper = "";
                            getDataDisplayHelper += "Температура горячей воды: " +  model.getTempGor() + "\n";
                            getDataDisplayHelper += "Температура холодной воды: " +  model.getTempXol()+ "\n";
                            getDataDisplayHelper += "Температура воздуха в комнате: " + model.getTempKomn() + "\n";
                            getDataDisplayHelper += "Мощность нагревателя: " + model.getPNagr() + "\n";
                            getDataDisplayHelper += "Работает ли помпа: " + model.getPomp() + "\n";
                            getDataDisplayHelper += "Ошибки: " + model.getError() + "\n";
                            getDataDisplayHelperNull = getDataDisplayHelper;
                        } else {
                            getDataDisplay.setText(getDataDisplayHelperNull);
                        }
                            /*Log.e(TAG, "onResponse: emails :" + model.error);*/
                    }
                    @Override
                    public void onFailure(@NonNull Call<Model> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: emails :" + t.getMessage());
                    }
                });
            }
        });

        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Methods methods = postRetrofitInstance.create(Methods.class);
                JSONObject jsonObject = new JSONObject();
                editTextString = editTextTemperature.getText().toString();
                try {
                    jsonObject.put("Temperature: ", editTextString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                Call<ResponseBody> call = methods.postData(requestBody);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.e(TAG, "onFailure: emails :" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e(TAG, "onFailure: emails :" + t.getMessage());// Обработка ошибки
                    }
                });
            }
        });
    }
}