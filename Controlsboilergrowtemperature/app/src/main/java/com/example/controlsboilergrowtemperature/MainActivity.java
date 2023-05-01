package com.example.controlsboilergrowtemperature;

import static com.example.controlsboilergrowtemperature.RetrofitClient.postRetrofitInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        TimerTask timerTask = new TimerTask()
        {
            @Override
            public void run()
            {

                String jsonFileName = user.getEmail() + ".json";
                Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
                Call<Model> call  = methods.getUser(jsonFileName);


                call.enqueue(new Callback<Model>() {

                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
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

                            getDataDisplayHelper += "Расход газа: " + model.getRasxGaza() + "\n";
                            getDataDisplayHelper += "Расход воздуха: " + model.getRasxVozd() + "\n";
                            getDataDisplayHelper += "Давление воды: " + model.getDavlVod() + "\n";
                            getDataDisplayHelper += "Давление газа: " + model.getDavlGaza() + "\n";

                            priseTextView.setText((int) (Integer.parseInt(model.getRasxGaza()) * 6.51) + " rub");
                            getDataDisplayHelperNull = getDataDisplayHelper;
                        } else {
                            getDataDisplay.setText(getDataDisplayHelperNull);
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
                                Log.e(TAG, "onFailure: emails :" + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            Log.e(TAG, "onFailure: emails :" + t.getMessage());// Обработка ошибки
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Right only number in this line", Toast.LENGTH_SHORT).show();
                }

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                logoutIntent();
            }
        });
    }
}