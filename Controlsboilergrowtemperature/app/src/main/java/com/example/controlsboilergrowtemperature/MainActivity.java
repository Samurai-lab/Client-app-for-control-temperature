package com.example.controlsboilergrowtemperature;

import static com.example.controlsboilergrowtemperature.RetrofitClient.postRetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView getDataDisplay;
    private TextView priseTextView;
    private EditText editTextTemperature;
    private Button postDataBtn;
    private ImageButton logoutBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private String getDataDisplayHelper;
    private String getDataDisplayHelperNull;
    private String editTextString;

    private void logoutIntent() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            logoutIntent();
        }

        postDataBtn = findViewById(R.id.postDataBtn);
        logoutBtn = findViewById(R.id.logout);
        getDataDisplay = findViewById(R.id.getDataDisplay);
        editTextTemperature = findViewById(R.id.editTextTemperature);
        priseTextView = findViewById(R.id.priseTextView);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                String jsonFileName = user.getEmail() + ".json";
                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                Call<Model> call = methods.getUser(jsonFileName);
                call.enqueue(new Callback<Model>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        Log.e(TAG, "onResponse: code :" + response.code());
                        if (response.code() != 404) {
                            Model model = response.body();
                            getDataDisplay.setText(getDataDisplayHelper);
                            if (model.getRasxGaza() != null) {
                                getDataDisplayHelper = "";
                                getDataDisplayHelper += getString(R.string.hot_water) +
                                        model.getTempGor() + "\n";
                                getDataDisplayHelper += getString(R.string.cold_water) +
                                        model.getTempXol() + "\n";
                                getDataDisplayHelper += getString(R.string.air_temperature) +
                                        model.getTempKomn() + "\n";
                                getDataDisplayHelper += getString(R.string.heater_power) +
                                        model.getPNagr() + "\n";
                                getDataDisplayHelper += getString(R.string.pomp_work) +
                                        model.getPomp() + "\n";
                                getDataDisplayHelper += getString(R.string.errors) +
                                        model.getError() + "\n";

                                getDataDisplayHelper += getString(R.string.gas_consumption) +
                                        model.getRasxGaza() + "\n";
                                getDataDisplayHelper += getString(R.string.air_consumption) +
                                        model.getRasxVozd() + "\n";
                                getDataDisplayHelper += getString(R.string.water_pressure) +
                                        model.getDavlVod() + "\n";
                                getDataDisplayHelper += getString(R.string.gas_pressure) +
                                        model.getDavlGaza() + "\n";
                                int gasPrice = (int)
                                        (Integer.parseInt(model.getRasxGaza()) * 19.540);
                                priseTextView.setText(gasPrice + " rub");
                                getDataDisplayHelperNull = getDataDisplayHelper;
                            } else {
                                getDataDisplay.setText(getDataDisplayHelperNull);
                            }
                        } else {
                            timer.cancel();
                            Toast.makeText(MainActivity.this,
                                    R.string.unavailable_user,
                                    Toast.LENGTH_SHORT).show();
                            logoutIntent();
                        }
                    }

                    @Override
                    public void onFailure(Call<Model> call, Throwable t) {
                        Log.e(TAG, "onFailure: emails :" + t.getMessage());
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);


        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Methods methods = postRetrofitInstance.create(Methods.class);
                JSONObject jsonObject = new JSONObject();
                editTextString = editTextTemperature.getText().toString();
                if (editTextString.matches("[-+]?\\d+")) {
                    try {
                        jsonObject.put(Objects.requireNonNull(user.getEmail()), editTextString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    Call<ResponseBody> call = methods.postData(requestBody);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.e(TAG, "onResponse: emails :" + response.code());
                                Toast.makeText(MainActivity.this, R.string.temperature_send, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: emails :" + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, R.string.write_number_only, Toast.LENGTH_SHORT).show();
                }

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutIntent();
            }
        });
    }
}